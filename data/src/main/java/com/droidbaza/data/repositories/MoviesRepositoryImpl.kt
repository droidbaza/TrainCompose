package com.droidbaza.data.repositories

import com.droidbaza.data.api.ApiMovies
import com.droidbaza.data.model.MetaPage
import com.droidbaza.data.model.Movie
import com.rusatom.data.repository.BaseRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MoviesRepositoryImpl @Inject constructor(private val apiMovies: ApiMovies) :
    MoviesRepository {

    override suspend fun moviesByPage(page: Long): LoadResult<Pair<MetaPage, List<Movie>>> {
        return when (val result = BaseRepository.safeCall {
            apiMovies.moviesByPageAsync(page)
        }) {
            is LoadResult.Loading -> LoadResult.Loading
            is LoadResult.Success -> {
                val model = result.data?.mapToModels()
                val current = result.data?.page
                val total = result.data?.totalPages
                val meta = if (current != null && total != null) {
                    MetaPage(current, current == total)
                } else null

                if (meta != null && model != null) {
                    LoadResult.Success(Pair(meta, model))
                } else {
                    LoadResult.Error(500, "body is empty")
                }
            }
            is LoadResult.Error -> LoadResult.Error(result.code)
        }
    }

}