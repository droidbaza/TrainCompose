package com.droidbaza.data.responses

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GenresResponse(@Json(name = "genres") val genres: List<Genre>)

@JsonClass(generateAdapter = true)
data class Genre(@Json(name = "id") val id: Int, @Json(name = "name") val name: String?)
