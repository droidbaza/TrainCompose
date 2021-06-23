package com.droidbaza.data.responses

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MovieDetailResponse(
    @Json(name = "adult") val adult: Boolean,
    @Json(name = "backdrop_path") val backdropPath: String?,
    @Json(name = "budget") val budget: Int,
    @Json(name = "genres") val genres: List<Genre>,
    @Json(name = "homepage") val homepage: String?,
    @Json(name = "id") val id: Int,
    @Json(name = "original_language") val originalLanguage: String,
    @Json(name = "original_title") val originalTitle: String,
    @Json(name = "overview") val overview: String,
    @Json(name = "popularity") val popularity: Double,
    @Json(name = "poster_path") val posterPath: String,
    @Json(name = "production_companies") val productionCompanies: List<ProductionCompanyResponse>,
    @Json(name = "release_date") val releaseDate: String,
    @Json(name = "revenue") val revenue: Double,
    @Json(name = "runtime") val runtime: Int?,
    @Json(name = "spoken_languages") val spokenLanguages: List<SpokenLanguage>,
    @Json(name = "status") val status: String,
    @Json(name = "tagline") val tagline: String,
    @Json(name = "title") val title: String,
    @Json(name = "vote_average") val voteAverage: Double,
    @Json(name = "vote_count") val voteCount: Int
)

@JsonClass(generateAdapter = true)
data class ProductionCompanyResponse(
    @Json(name = "id") val id: Int,
    @Json(name = "logo_path") val logoPath: String?,
    @Json(name = "name") val name: String,
    @Json(name = "origin_country") val originCountry: String
)

@JsonClass(generateAdapter = true)
data class SpokenLanguage(
    @Json(name = "iso_639_1") val iso6391: String,
    @Json(name = "name") val name: String
)
