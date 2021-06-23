package com.droidbaza.data.repositories

import com.droidbaza.data.model.MetaPage
import com.droidbaza.data.model.Movie

interface MoviesRepository {

    suspend fun moviesByPage(page: Long): LoadResult<Pair<MetaPage, List<Movie>>>
}