package com.imawo.imoviedb

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.imawo.veaguard.Helpers.Utils
import com.imawo.imoviedb.Helpers.Globals
import com.imawo.imoviedb.Models.ModelFilme
import eightbitlab.com.blurview.BlurView
import imawo.prognosis.adapter.AdapterFilme
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class CautaFilmeActivity : AppCompatActivity() {

    private var mRootView: ConstraintLayout? = null
    private var mLinearLoading: LinearLayout? = null
    private var mViewListFilme: RecyclerView? = null
    private val mHandler = Handler()
    private var mAdapterFilme: AdapterFilme? = null

    private lateinit var listTopFilmeRestGetList: MutableList<ModelFilme>

    private var TAG: String = "CautaFilmeActivity"

    private val showDetaliiFilmActivity = Runnable {
        try {
            val intent = Intent(this@CautaFilmeActivity, DetaliiFilmActivity::class.java)
            intent.putExtra("id", Globals.id)
            val REQUEST_CODE = 0
            startActivityForResult(intent, REQUEST_CODE)
        } catch (e: Exception) {
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cauta_filme)

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
        mLinearLoading?.visibility = View.GONE

        var searchView: SearchView = findViewById(R.id.search_movie)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                mLinearLoading?.visibility = View.VISIBLE

                Globals.searchString = query
                getListFromREST()

                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {

                return false
            }
        })

        mViewListFilme = findViewById(R.id.recycler_view_lista_filme)
        val layoutManager = LinearLayoutManager(this)
        mViewListFilme!!.layoutManager = layoutManager
        mViewListFilme!!.setHasFixedSize(true)
    }

    fun loadListView() {
        mAdapterFilme = AdapterFilme(
            Globals.listTopFilmeGetList,
            this)
        { item, position ->

            Globals.id = item.id.toInt()
            mHandler.postDelayed(showDetaliiFilmActivity, 220)
        }

        mViewListFilme!!.adapter = mAdapterFilme
    }

    fun getListFromREST() {
        Globals.jsonIP = "https://api.themoviedb.org"
        Globals.jsonPath = "/3"
        Globals.jsonAPI = "/search/movie"
        Globals.jsonParams = "?api_key=a2b514f30f5ba475c87191403bf1c6d0&language=en-US&query=" + Globals.searchString
        Globals.jsonUrl = Globals.jsonIP + Globals.jsonPath + Globals.jsonAPI + Globals.jsonParams

        Log.d(TAG, Globals.jsonUrl)
        requestJSON(Globals.jsonUrl, )
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
}