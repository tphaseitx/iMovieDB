package com.imawo.veaguard.Helpers

import android.animation.*
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Point
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.Base64
import android.util.DisplayMetrics
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.*
import android.widget.CalendarView
import android.widget.DatePicker
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.google.android.material.snackbar.Snackbar
import org.apache.commons.io.IOUtils
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.math.BigDecimal
import java.math.RoundingMode
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.SocketTimeoutException
import java.net.URL
import java.nio.charset.StandardCharsets
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import eightbitlab.com.blurview.RenderScriptBlur

import eightbitlab.com.blurview.BlurView




object Utils {
    var mError = ""
    var mCurrentAnimator: Animator? = null
    var mShortAnimationDuration = 200
    var scaleDown: ObjectAnimator? = null
    var scaleDownSecond: ObjectAnimator? = null
    var OutToRight: Animation? = null
    var OutToLeft: Animation? = null
    var InFromRight: Animation? = null
    private var
            InFromLeft: Animation? = null
    fun initSwipeAnimation() {
        OutToRight = TranslateAnimation(
            Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, +1.0f,
            Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f
        )
        OutToRight?.setDuration(400)
        OutToRight?.setInterpolator(AccelerateDecelerateInterpolator())
        OutToLeft = TranslateAnimation(
            Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, -1.0f,
            Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f
        )
        OutToLeft?.setDuration(400)
        OutToLeft?.setInterpolator(AccelerateDecelerateInterpolator())
        InFromRight = TranslateAnimation(
            Animation.RELATIVE_TO_PARENT, +1.0f, Animation.RELATIVE_TO_PARENT, 0.0f,
            Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f
        )
        InFromRight?.setDuration(400)
        InFromRight?.setInterpolator(AccelerateDecelerateInterpolator())
        InFromLeft = TranslateAnimation(
            Animation.RELATIVE_TO_PARENT, -1.0f, Animation.RELATIVE_TO_PARENT, 0.0f,
            Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f
        )
        InFromLeft?.setDuration(400)
        InFromLeft?.setInterpolator(AccelerateDecelerateInterpolator())
    }

    fun animateTranslation(context: Context, mView: View, duration: Int, nCode: Int) {
        mView.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        var translation = TranslateAnimation(0f, 0f, 0f, 0f)
        when (nCode) {
            0 -> translation = TranslateAnimation(0f, 0f,
                (getDisplayHeight(context) / 2).toFloat(), 0f)
            1 -> translation = TranslateAnimation(0f, 0f,
                (-1 * getDisplayHeight(context)).toFloat(), 0f)
            3 -> translation = TranslateAnimation(0f, 0f, 0f,
                (-1 * getDisplayHeight(context)).toFloat()
            )
            2 -> translation = TranslateAnimation(0f, 0f, getDisplayHeight(context), 0f)
            4 -> translation = TranslateAnimation(0f, 0f, 0f, getDisplayHeight(context))
        }
        translation.setStartOffset(0)
        translation.setDuration(duration.toLong())
        translation.setFillAfter(true)
        translation.setInterpolator(DecelerateInterpolator())
        translation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                mView.setLayerType(View.LAYER_TYPE_NONE, null)
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })
        mView.startAnimation(translation)
    }

    private fun getDisplayWidth(context: Context): Int {
        val metrics = DisplayMetrics()
        (context as Activity).getWindowManager().getDefaultDisplay().getMetrics(metrics)
        return metrics.widthPixels
    }

    private fun getDisplayHeight(context: Context): Float {
        val metrics = DisplayMetrics()
        (context as Activity).getWindowManager().getDefaultDisplay().getMetrics(metrics)
        return metrics.heightPixels.toFloat()
    }

    fun zoomImageFromThumb(
        thumbView: View,
        expandedImageView: ImageView,
        container: RelativeLayout
    ) {
        // If there's an animation in progress, cancel it
        // immediately and proceed with this one.
        if (mCurrentAnimator != null) {
            mCurrentAnimator?.cancel()
        }

        // Load the high-resolution "zoomed-in" image.
        //expandedImageView.setImageResource(imageResId);

        // Calculate the starting and ending bounds for the zoomed-in image.
        // This step involves lots of math. Yay, math.
        val startBounds = Rect()
        val finalBounds = Rect()
        val globalOffset = Point()

        // The start bounds are the global visible rectangle of the thumbnail,
        // and the final bounds are the global visible rectangle of the container
        // view. Also set the container view's offset as the origin for the
        // bounds, since that's the origin for the positioning animation
        // properties (X, Y).
        thumbView.getGlobalVisibleRect(startBounds)
        container.getGlobalVisibleRect(finalBounds, globalOffset)
        startBounds.offset(-globalOffset.x, -globalOffset.y)
        finalBounds.offset(-globalOffset.x, -globalOffset.y)

        // Adjust the start bounds to be the same aspect ratio as the final
        // bounds using the "center crop" technique. This prevents undesirable
        // stretching during the animation. Also calculate the start scaling
        // factor (the end scaling factor is always 1.0).
        val startScale: Float
        if (finalBounds.width() as Float / finalBounds.height()
            > startBounds.width() as Float / startBounds.height()
        ) {
            // Extend start bounds horizontally
            startScale = startBounds.height() as Float / finalBounds.height()
            val startWidth: Float = startScale * finalBounds.width()
            val deltaWidth: Float = (startWidth - startBounds.width()) / 2
            startBounds.left -= deltaWidth.toInt()
            startBounds.right += deltaWidth.toInt()
        } else {
            // Extend start bounds vertically
            startScale = startBounds.width() as Float / finalBounds.width()
            val startHeight: Float = startScale * finalBounds.height()
            val deltaHeight: Float = (startHeight - startBounds.height()) / 2
            startBounds.top -= deltaHeight.toInt()
            startBounds.bottom += deltaHeight.toInt()
        }

        // Hide the thumbnail and show the zoomed-in view. When the animation
        // begins, it will position the zoomed-in view in the place of the
        // thumbnail.
        //thumbView.setAlpha(0f);
        thumbView.setVisibility(View.GONE)
        expandedImageView.setVisibility(View.VISIBLE)

        // Set the pivot point for SCALE_X and SCALE_Y transformations
        // to the top-left corner of the zoomed-in view (the default
        // is the center of the view).
        expandedImageView.setPivotX(0f)
        expandedImageView.setPivotY(0f)

        // Construct and run the parallel animation of the four translation and
        // scale properties (X, Y, SCALE_X, and SCALE_Y).
        val set = AnimatorSet()
        set
            .play(
                ObjectAnimator.ofFloat<View>(
                    expandedImageView, View.X,
                    startBounds.left.toFloat(), finalBounds.left.toFloat()
                )
            )
            .with(
                ObjectAnimator.ofFloat<View>(
                    expandedImageView, View.Y,
                    startBounds.top.toFloat(), finalBounds.top.toFloat()
                )
            )
            .with(
                ObjectAnimator.ofFloat<View>(
                    expandedImageView, View.SCALE_X,
                    startScale, 1f
                )
            )
            .with(
                ObjectAnimator.ofFloat<View>(
                    expandedImageView,
                    View.SCALE_Y, startScale, 1f
                )
            )
        set.setDuration(mShortAnimationDuration.toLong())
        set.setInterpolator(DecelerateInterpolator())
        set.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                mCurrentAnimator = null
            }

            override fun onAnimationCancel(animation: Animator) {
                mCurrentAnimator = null
            }
        })
        set.start()
        mCurrentAnimator = set
    }

    fun showMessage(view: View?, message: String?) {
        val snackbar: Snackbar = Snackbar
            .make(view!!, message!!, Snackbar.LENGTH_LONG)
        snackbar.show()
    }

    fun showDialog(context: Context?, title: String?, message: String?) {
        val alertDialog = AlertDialog.Builder(context).create()
        alertDialog.setTitle(title)
        alertDialog.setMessage(message)
        alertDialog?.setButton("OK", object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface, which: Int) {}
        })
        alertDialog.show()
    }

    fun requestWebServiceToString(
        serviceUrl: String?,
        username: String,
        password: String
    ): String? {
        disableConnectionReuseIfNecessary()
        var jsonResponse: String? = null
        var urlConnection: HttpURLConnection? = null
        try {
            val urlToRequest = URL(serviceUrl)
            urlConnection = urlToRequest.openConnection() as HttpURLConnection
            urlConnection.setConnectTimeout(60 * 1000)
            urlConnection.setReadTimeout(60 * 1000)
            val userCredentials = "$username:$password"
            var basicAuth = ""
            var credentials = ByteArray(0)
            credentials = userCredentials.toByteArray(StandardCharsets.UTF_8)
            basicAuth = "Basic " + String(
                Base64.encode(credentials, Base64.URL_SAFE),
                StandardCharsets.UTF_8
            )
            urlConnection.setRequestProperty("Authorization", basicAuth)
            urlConnection.setRequestProperty("Content-Type", "application/json")
            val statusCode: Int = urlConnection.getResponseCode()
            if (statusCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                mError = "Unauthorized access \n\nHttpURLConnection.HTTP_UNAUTHORIZED"
                return mError
            } else if (statusCode != HttpURLConnection.HTTP_OK) {
                mError = "HTTP error " + statusCode + "\\nn" + urlConnection.getResponseMessage()
                return mError
            }
            if (statusCode == 200) {
                val inputStr: InputStream = urlConnection.getInputStream()
                val encoding =
                    if (urlConnection.getContentEncoding() == null) "UTF-8" else urlConnection.getContentEncoding()
                jsonResponse = IOUtils.toString(inputStr, encoding)
            } else {
                mError = statusCode.toString()
                return mError
            }
        } catch (e: MalformedURLException) {
            mError = """
                URL is invalid 
                
                ${e.message}
                """.trimIndent()
        } catch (e: SocketTimeoutException) {
            mError = """
                Data retrieval or connection timed out 
                
                ${e.message}
                """.trimIndent()
        } catch (e: IOException) {
            mError = """
                Could not read response body 
                
                ${e.message}
                """.trimIndent()
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect()
            }
        }
        mError = "NO ERROR IN WEB REQUEST"
        return jsonResponse
    }

    private fun disableConnectionReuseIfNecessary() {
        if (Build.VERSION.SDK.toInt() < Build.VERSION_CODES.FROYO) {
            System.setProperty("http.keepAlive", "false")
        }
    }

    fun getDateFromDatePicker(datePicker: DatePicker): Date {
        val day: Int = datePicker.getDayOfMonth()
        val month: Int = datePicker.getMonth()
        val year: Int = datePicker.getYear()
        val calendar = Calendar.getInstance()
        calendar[year, month] = day
        return calendar.time
    }

    fun getCalendarFromDatePicker(datePicker: DatePicker): Calendar {
        val day: Int = datePicker.getDayOfMonth()
        val month: Int = datePicker.getMonth()
        val year: Int = datePicker.getYear()
        val calendar = Calendar.getInstance()
        calendar[year, month] = day
        return calendar
    }

    fun getCalendarFromCalendarView(calendarView: CalendarView): Calendar {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = calendarView.getDate()
        return calendar
    }

    fun getDateFromText(textDate: String?, formatString: String?): Date? {
        val dateFormat = SimpleDateFormat(formatString)
        var date: Date? = Date()
        try {
            date = dateFormat.parse(textDate)
            return date
        } catch (e: ParseException) {
        }
        return null
    }

    fun getIndexArrayFromString(value: String?, arrayString: Array<Array<String>>): Int {
        //se cauta in arrayString index-ul matricii corespunzator Status
        var index = -1
        for (i in arrayString.indices) {
            if (arrayString[i][1].contains(value!!)) {
                index = i
            }
        }
        return index
    }

    fun getIndexArrayFromId(Id: Int, arrayString: Array<Array<String>>): Int {
        //se cauta in arrayString index-ul matricii corespunzator StatusId
        var index = -1
        for (i in arrayString.indices) {
            if (arrayString[i][0].contains(Id.toString())) {
                index = i
            }
        }
        return index
    }

    fun rotateView(view: View) {
        val rotate = RotateAnimation(
            0.0F,
            180F,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        )
        rotate.setDuration(5000)
        rotate.setInterpolator(LinearInterpolator())
        rotate.setRepeatCount(ObjectAnimator.INFINITE)
        view.startAnimation(rotate)
    }

    fun pulsateView(view: View?) {
        scaleDown = ObjectAnimator.ofPropertyValuesHolder(
            view,
            PropertyValuesHolder.ofFloat("scaleX", 0.9f),
            PropertyValuesHolder.ofFloat("scaleY", 0.9f)
        )
        scaleDown?.setDuration(310)
        scaleDown?.setInterpolator(FastOutSlowInInterpolator())
        scaleDown?.setRepeatCount(ObjectAnimator.INFINITE)
        scaleDown?.setRepeatMode(ObjectAnimator.REVERSE)
        scaleDown?.start()
    }

    fun pulsateViewSecond(view: View?) {
        scaleDownSecond = ObjectAnimator.ofPropertyValuesHolder(
            view,
            PropertyValuesHolder.ofFloat("scaleX", 0.9f),
            PropertyValuesHolder.ofFloat("scaleY", 0.9f)
        )
        scaleDownSecond?.setDuration(310)
        scaleDownSecond?.setInterpolator(FastOutSlowInInterpolator())
        scaleDownSecond?.setRepeatCount(ObjectAnimator.INFINITE)
        scaleDownSecond?.setRepeatMode(ObjectAnimator.REVERSE)
        scaleDownSecond?.start()
    }

    fun pulsateClickView(view: View?) {
        scaleDown = ObjectAnimator.ofPropertyValuesHolder(
            view,
            PropertyValuesHolder.ofFloat("scaleX", 0.9f),
            PropertyValuesHolder.ofFloat("scaleY", 0.9f)
        )
        scaleDown?.setDuration(110)
        scaleDown?.setInterpolator(FastOutSlowInInterpolator())
        scaleDown?.setRepeatCount(1)
        scaleDown?.setRepeatMode(ObjectAnimator.REVERSE)
        scaleDown?.start()
    }

    fun isCurrentDay(compare: String): Boolean {
        val calendar = Calendar.getInstance()
        val day = calendar[Calendar.DAY_OF_MONTH]
        return String.format("%02d", day) == compare
    }

    fun isWeekEnd(date: Date?): Boolean {
        val calendar = Calendar.getInstance()
        calendar.time = date
        return calendar[Calendar.DAY_OF_WEEK] == Calendar.SATURDAY || calendar[Calendar.DAY_OF_WEEK] == Calendar.SUNDAY
    }

    fun setOnTouch(view: View, gesture: GestureDetector) {
        view.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                return gesture.onTouchEvent(event)
            }
        })
    }

    fun replaceNull(input: String?): String {
        return input ?: ""
    }

    fun setImageViewCompressedJPEG(imageView: ImageView, drawable: Drawable) {
        val BYTE: ByteArray
        val bytearrayoutputstream = ByteArrayOutputStream()
        val bitmap1: Bitmap = (drawable as BitmapDrawable).getBitmap()
        bitmap1.compress(Bitmap.CompressFormat.JPEG, 50, bytearrayoutputstream)
        BYTE = bytearrayoutputstream.toByteArray()
        val bitmap2: Bitmap = BitmapFactory.decodeByteArray(BYTE, 0, BYTE.size)
        imageView.setImageBitmap(bitmap2)
    }

    fun setViewCompressedJPEG(view: ViewGroup, drawable: Drawable) {
        val BYTE: ByteArray
        val bytearrayoutputstream = ByteArrayOutputStream()
        val bitmap1: Bitmap = (drawable as BitmapDrawable).getBitmap()
        bitmap1.compress(Bitmap.CompressFormat.JPEG, 50, bytearrayoutputstream)
        BYTE = bytearrayoutputstream.toByteArray()
        val bitmap2: Bitmap = BitmapFactory.decodeByteArray(BYTE, 0, BYTE.size)
        val drw: Drawable = BitmapDrawable(bitmap2)
        view.setBackgroundDrawable(drw)
    }

    fun isNumeric(strNumber: String?): Boolean {
        if (strNumber == null) {
            return false
        }
        try {
            val d = strNumber.toDouble()
        } catch (nfe: NumberFormatException) {
            return false
        }
        return true
    }

    fun round(value: Double, places: Int): Double {
        require(places >= 0)
        var bd = BigDecimal.valueOf(value)
        bd = bd.setScale(places, RoundingMode.HALF_UP)
        return bd.toDouble()
    }

    fun setupBlurView(
        context: Context?,
        rootView: ViewGroup?,
        windowBackground: Drawable?,
        blurView: BlurView,
        radius: Float
    ) {
        blurView.setupWith(rootView!!)
            .windowBackground(windowBackground)
            .blurAlgorithm(RenderScriptBlur(context))
            .blurRadius(radius)
            .setHasFixedTransformationMatrix(true)
    }
}