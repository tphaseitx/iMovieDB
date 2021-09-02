package com.imawo.imoviedb


import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import com.imawo.veaguard.Helpers.Utils
import com.imawo.veaguard.Helpers.Utils.setViewCompressedJPEG
import com.imawo.veaguard.Helpers.Utils.setupBlurView
import eightbitlab.com.blurview.BlurView


class MainActivity : AppCompatActivity() {

    private var mRootView: ConstraintLayout? = null
    private var toolbar: Toolbar? = null
    private var mTextVersiune: TextView? = null
    private var mCardCautareFilme: CardView? = null
    private var mCardTopFilme: CardView? = null

    private val mHandler = Handler()

    private val launchActivityCautaFilme = Runnable {
        try {
            val intent = Intent(this@MainActivity, CautaFilmeActivity::class.java)
            startActivity(intent)
        } catch (e: Exception) {
        }
    }

    private val launchActivityTopFilme = Runnable {
        try {
            val intent = Intent(this@MainActivity, TopFilmeActivity::class.java)
            startActivity(intent)
        } catch (e: Exception) {
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Toolbar>(R.id.toolBar)
        setSupportActionBar(toolbar)

        mRootView = findViewById(R.id.root_layout)
        setViewCompressedJPEG(
            findViewById<View>(R.id.root_layout) as ViewGroup,
            resources.getDrawable(R.drawable.wallpaper_blur_1)
        )

        val decorView = window.decorView
        val windowBackground = decorView.background

        var mAllBlur: BlurView = findViewById(R.id.all_blur)
        setupBlurView(this, mRootView, windowBackground, mAllBlur, 24F)

        mCardCautareFilme = findViewById(R.id.card_cautare_filme)
        mCardCautareFilme?.setOnClickListener(View.OnClickListener {
            Utils.pulsateClickView(mCardCautareFilme)
            mHandler.postDelayed(launchActivityCautaFilme, 220)
        })

        mCardTopFilme = findViewById(R.id.card_top_filme)
        mCardTopFilme?.setOnClickListener(View.OnClickListener {
            Utils.pulsateClickView(mCardTopFilme)
            mHandler.postDelayed(launchActivityTopFilme, 220)
        })

        var version = ""
        try {
            val pInfo = this.packageManager.getPackageInfo(this.packageName, 0)
            version = pInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        mTextVersiune = findViewById(R.id.text_version)
        mTextVersiune?.setText("Versiunea: $version")
    }
}