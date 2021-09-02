package com.imawo.imoviedb.Models

import com.google.gson.annotations.SerializedName

class ModelFilme(
    @SerializedName("id") var id: Long,
    @SerializedName("title") var title: String,
    @SerializedName("overview") var overview: String,
    @SerializedName("poster_path") var posterPath: String,
    @SerializedName("backdrop_path") var backdropPath: String,
    @SerializedName("vote_average") var rating: Double,
    @SerializedName("release_date") var releaseDate: String
    )
{
    init {
    }
}