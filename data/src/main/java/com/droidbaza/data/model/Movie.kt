package com.droidbaza.data.model

data class Movie(
    val id: Int,
    val name: String,
    val releaseDate: String,
    val posterPath: String,
    val voteAverage: Double,
    val voteCount: Int,
    var pageMeta:String="",
)
