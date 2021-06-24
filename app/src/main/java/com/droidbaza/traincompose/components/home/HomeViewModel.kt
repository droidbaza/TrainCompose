package com.droidbaza.traincompose.components.home

import androidx.lifecycle.viewModelScope
import com.droidbaza.data.model.Movie
import com.droidbaza.data.repositories.MoviesRepository
import com.droidbaza.traincompose.PagedViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val moviesRepository: MoviesRepository) :
    PagedViewModel<Movie>() {

    init {
        onRestart()
    }

    override fun onJobRequestPage(nextPage: Long) {
        job = viewModelScope.launch(Dispatchers.IO) {
            onResultLoading(moviesRepository.moviesByPage(nextPage))
        }
    }
}