package com.imawo.imoviedb.Room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.imawo.imoviedb.Models.ModelFilme

@Dao
interface MoviesDao {

    @Query("SELECT * FROM movies ORDER BY id DESC")
    fun getAllMovies() : ArrayList<ModelFilme>

    @Query("SELECT * FROM movies WHERE id = :movieId")
    fun loadMovieById(movieId: Int): ArrayList<ModelFilme>

    @Insert
    fun insertMovie(movie: Movies)

    @Delete
    fun delete(movie: Movies)

    @Delete
    fun deleteAllMovies()
}