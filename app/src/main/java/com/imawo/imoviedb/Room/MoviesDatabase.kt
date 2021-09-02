package com.imawo.imoviedb.Room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = arrayOf(Movies::class), version = 1)
abstract class MoviesDatabase : RoomDatabase() {
    abstract fun movieDao(): MoviesDao
}