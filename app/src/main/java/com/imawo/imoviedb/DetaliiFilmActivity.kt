package com.imawo.imoviedb

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.imawo.veaguard.Helpers.Utils
import com.mikhaellopez.circularimageview.CircularImageView
import com.imawo.imoviedb.Helpers.Globals
import eightbitlab.com.blurview.BlurView
import org.json.JSONException
import org.json.JSONObject

class DetaliiFilmActivity : AppCompatActivity() {

    private var mRootView: ConstraintLayout? = null
    private var mLinearLoading: LinearLayout? = null
    private var imageMovie: ImageView? = null
    private var imageMoviePoster: CircularImageView? = null
    private var movie: TextView? = null
    private var overview: TextView? = null
    private var releaseDate: TextView? = null

    private var id: Int? = null
    private var TAG: String = "DetaliiFilmActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalii_film)

        mRootView = findViewById(R.id.root_layout)
        Utils.setViewCompressedJPEG(
            findViewById<View>(R.id.root_layout) as ViewGroup,
            resources.getDrawable(R.drawable.wallpaper_blur_1)
        )

        val decorView = window.decorView
        val windowBackground = decorView.background

        var mAllBlur: BlurView = findViewById(R.id.all_blur)
        Utils.setupBlurView(this, mRootView, windowBackground, mAllBlur, 24F)

        mLinearLoading = findViewById(R.id.linear_loading)
        mLinearLoading?.visibility = View.VISIBLE

        val extras = intent.extras
        if (extras != null) {
            id = extras.getInt("id")
        }

        imageMovie = findViewById(R.id.image_movie)
        imageMoviePoster = findViewById(R.id.image_movie_poster)
        movie = findViewById(R.id.text_movie)
        movie?.setText("")

        overview = findViewById(R.id.text_overview)
        releaseDate = findViewById(R.id.text_release)

        getListFromREST()
    }

    fun getListFromREST() {
        Globals.jsonIP = "https://api.themoviedb.org"
        Globals.jsonPath = "/3"
        Globals.jsonAPI = "/movie"
        Globals.jsonParams = "/" + id.toString() + "?api_key=a2b514f30f5ba475c87191403bf1c6d0&language=en-US"
        Globals.jsonUrl = Globals.jsonIP + Globals.jsonPath + Globals.jsonAPI + Globals.jsonParams

        Log.d(TAG, Globals.jsonUrl)
        requestJSON(Globals.jsonUrl)
    }

    private fun requestJSON(jsonURL: String) {
        val queue = Volley.newRequestQueue(this)
        // Request a string response from the provided URL.

        val stringRequest = StringRequest(
            Request.Method.GET, jsonURL,
            { response ->
                Log.d(TAG, "RESPONSE: ")
                Log.d(TAG, ">>$response")

                try {
                    //getting the whole json object from the response
                    val result = JSONObject(response)
                    movie?.setText(result.getString("title"))
                    overview?.setText(result.getString("overview"))
                    releaseDate?.setText(result.getString("release_date"))

                    Glide.with(this)
                        .load("https://image.tmdb.org/t/p/w342${result.getString("backdrop_path")}")
                        .transform(CenterCrop())
                        .into(imageMovie!!)

                    Glide.with(this)
                        .load("https://image.tmdb.org/t/p/w342${result.getString("poster_path")}")
                        .transform(CenterCrop())
                        .into(imageMoviePoster!!)

                    mLinearLoading?.visibility = View.GONE

                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            { error ->
                //displaying the error in toast if occurrs
                Log.d(TAG, "ERROR: ")
                error.message?.let { Log.d(TAG, it) }
            })

        // Add the request to the RequestQueue.
        queue.add(stringRequest)
    }
}