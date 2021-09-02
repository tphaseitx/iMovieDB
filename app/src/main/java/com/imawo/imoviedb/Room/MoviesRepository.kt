package com.imawo.imoviedb.Room

import android.content.Context
import android.os.AsyncTask
import com.imawo.imoviedb.Models.ModelFilme

class MoviesRepository(private val db: MoviesDao) {

    //Fetch All the Users
    fun getAllMovies() : ArrayList<ModelFilme> {
        return db.getAllMovies()
    }

    // Insert new user
    fun insertMovie(movie: Movies) {
        db.insertMovie(movie)
    }

    // Delete user
    fun delete(movie: Movies) {
        db.delete(movie)
    }
}