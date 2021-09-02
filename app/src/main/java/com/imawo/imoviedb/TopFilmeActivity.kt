package com.imawo.imoviedb

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.imawo.imoviedb.Helpers.Globals
import com.imawo.imoviedb.Models.ModelFilme
import com.imawo.imoviedb.Room.Movies
import com.imawo.imoviedb.Room.MoviesDao
import com.imawo.imoviedb.Room.MoviesDatabase
import com.imawo.veaguard.Helpers.Utils
import eightbitlab.com.blurview.BlurView
import imawo.prognosis.adapter.AdapterFilme
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

class TopFilmeActivity : AppCompatActivity() {

    private var mRootView: ConstraintLayout? = null
    private var mLinearLoading: LinearLayout? = null
    private var mViewListTopFilme: RecyclerView? = null
    private val mHandler = Handler()
    private var mAdapterFilme: AdapterFilme? = null

    private lateinit var listTopFilmeRestGetList: MutableList<ModelFilme>
    //private var db: MoviesDatabase? = null
    //private lateinit var movieDao: MoviesDao
    
    private var TAG: String = "TopFilmeActivity"

    private val showDetaliiFilmActivity = Runnable {
        try {
            val intent = Intent(this@TopFilmeActivity, DetaliiFilmActivity::class.java)
            intent.putExtra("id", Globals.id)
            val REQUEST_CODE = 0
            startActivityForResult(intent, REQUEST_CODE)
        } catch (e: Exception) {
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_top_filme)

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

        mViewListTopFilme = findViewById(R.id.recycler_view_top_filme)
        val layoutManager = LinearLayoutManager(this)
        mViewListTopFilme!!.layoutManager = layoutManager
        mViewListTopFilme!!.setHasFixedSize(true)

        /*db = Room.databaseBuilder(
            applicationContext,
            MoviesDatabase::class.java, "moviesdb"
        ).build()

        movieDao = db?.movieDao()!!*/

        //If is no internet connection, use Room
        //loadMoviesFromRoom()

        //If we have internet connection, use REST API
        getListFromREST()
    }

    fun loadListView() {
        mAdapterFilme = AdapterFilme(
            Globals.listTopFilmeGetList,
            this)
        { item, position ->

            Globals.id = item.id.toInt()
            mHandler.postDelayed(showDetaliiFilmActivity, 220)
        }

        mViewListTopFilme!!.adapter = mAdapterFilme
    }

    fun getListFromREST() {
        Globals.jsonIP = "https://api.themoviedb.org"
        Globals.jsonPath = "/3"
        Globals.jsonAPI = "/discover/movie"
        Globals.jsonParams = "?api_key=a2b514f30f5ba475c87191403bf1c6d0&language=en-US&sort_by=popularity.desc&include_adult=false&include_video=false&page=1&with_watch_monetization_types=flatrate"
        Globals.jsonUrl = Globals.jsonIP + Globals.jsonPath + Globals.jsonAPI + Globals.jsonParams

        Log.d(TAG, Globals.jsonUrl)
        requestJSON(Globals.jsonUrl)
    }

    private fun requestJSON(jsonURL: String) {
        val queue = Volley.newRequestQueue(this)
        // Request a string response from the provided URL.

        val stringRequest = StringRequest(Request.Method.GET, jsonURL,
            { response ->
                Log.d(TAG, "RESPONSE: ")
                Log.d(TAG, ">>$response")

                try {
                    //getting the whole json object from the response
                    val obj = JSONObject(response)
                    listTopFilmeRestGetList = ArrayList<ModelFilme>()
                    val dataArray = obj.getJSONArray("results")

                    for (i in 0 until dataArray.length()) {
                        val result = dataArray.getJSONObject(i)
                        listTopFilmeRestGetList?.add(
                            ModelFilme(
                                result.getLong("id"),
                                result.getString("title"),
                                result.getString("overview"),
                                result.getString("poster_path"),
                                result.getString("backdrop_path"),
                                result.getDouble("vote_average"),
                                result.getString("release_date")
                            )
                        )
                    }

                    //Delete old movies from Room and write the new list of movies from REST API
                    /*movieDao.deleteAllMovies()
                    for (i in listTopFilmeRestGetList.indices) {
                        movieDao.insertMovie(Movies(
                            listTopFilmeRestGetList.get(i).id,
                            listTopFilmeRestGetList.get(i).title,
                            listTopFilmeRestGetList.get(i).overview,
                            listTopFilmeRestGetList.get(i).releaseDate
                        ))
                    }*/

                    Globals.listTopFilmeGetList = ArrayList(listTopFilmeRestGetList)
                    Globals.listTopFilmeGetList.clear()
                    Globals.listTopFilmeGetList.addAll(listTopFilmeRestGetList)
                    
                    loadListView();
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

    /*fun loadMoviesFromRoom() {
        listTopFilmeRestGetList = movieDao.getAllMovies()

        Globals.listTopFilmeGetList = ArrayList(listTopFilmeRestGetList)
        Globals.listTopFilmeGetList.clear()
        Globals.listTopFilmeGetList.addAll(listTopFilmeRestGetList)

        loadListView();
        mLinearLoading?.visibility = View.GONE
    }*/
}