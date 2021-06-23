package com.droidbaza.data.responses

import com.droidbaza.data.model.Movie
import com.droidbaza.data.toPosterUrl
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MoviesResponse(
    @Json(name = "page") val page: Long,
    @Json(name = "results") val movies: List<MovieResponse>,
    @Json(name = "total_pages") val totalPages: Long,
    @Json(name = "total_results") val totalResults: Long
) {
    fun mapToModels(): List<Movie> {
        return movies.map { it.mapToModel() }
    }
}


@JsonClass(generateAdapter = true)
data class MovieResponse(
    @Json(name = "id") val id: Int,
    @Json(name = "release_date") val firstAirDate: String?=null,
    @Json(name = "title") val name: String,
    @Json(name = "original_title") val originalTitle: String,
    @Json(name = "original_language") val originalLanguage: String,
    @Json(name = "overview") val overview: String,
    @Json(name = "poster_path") val posterPath: String?,
    @Json(name = "vote_average") val voteAverage: Double,
    @Json(name = "vote_count") val voteCount: Int
) {
    fun mapToModel(): Movie {
        return Movie(
            id = id,
            name = name,
            releaseDate = firstAirDate?:"",
            posterPath = posterPath.orEmpty().toPosterUrl(),
            voteAverage = voteAverage,
            voteCount = voteCount
        )
    }
}
