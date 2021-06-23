package com.droidbaza.data.api


import com.droidbaza.data.responses.*
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiMovies {
    @GET("movie/popular?api_key=7e8f60e325cd06e164799af1e317d7a7")
    suspend fun moviesByPageAsync(
        @Query("page") pageNumber: Long,
        @Query("with_genres") genreId: Int? = null,
    ): Response<MoviesResponse>

    @GET("genre/movie/list?api_key=7e8f60e325cd06e164799af1e317d7a7")
    suspend fun genresAsync(): GenresResponse

    @GET("movie/{movie_id}?api_key=7e8f60e325cd06e164799af1e317d7a7")
    suspend fun movieDetailsAsync(@Path("movie_id") movieId: Int): MovieDetailResponse

    @GET("movie/{movie_id}/credits?api_key=7e8f60e325cd06e164799af1e317d7a7")
    suspend fun movieCreditsAsync(@Path("movie_id") movieId: Int): CreditsResponse

    @GET("movie/{movie_id}/images?api_key=7e8f60e325cd06e164799af1e317d7a7")
    suspend fun movieImagesAsync(@Path("movie_id") movieId: Int): ImagesResponse
}
