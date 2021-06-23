package com.droidbaza.traincompose.components.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.droidbaza.traincompose.data.LatestData
import com.droidbaza.traincompose.data.NewsItem
import com.droidbaza.traincompose.data.NewsService
import com.droidbaza.traincompose.data.toData
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(@ApplicationContext context: Context) : ViewModel() {

    private val _newsState = MutableStateFlow(LatestData(emptyList(), ""))
    val newsState: StateFlow<LatestData> get() = _newsState

    private val newsService = Retrofit.Builder()
        .baseUrl("https://omni-content-stage.omni.news/")
        .addConverterFactory(MoshiConverterFactory.create())
        .build()
        .create(NewsService::class.java)

    init {
        getNews()
    }

    private fun getNews() {
        viewModelScope.launch {
            try {
                val response = newsService.getLatest()
                _newsState.value = response.toData()
            } catch (ex: Exception) {
                error(ex)
            }
        }
    }

    fun getMoreNews() {
        viewModelScope.launch {
            try {
                val response = newsService.getMoreArticles(_newsState.value.next)
                _newsState.value = response.toData(_newsState.value.articles)
            } catch (ex: Exception) {
                error(ex)
            }
        }
    }

    fun onSelectedNews(news: NewsItem) {

    }
}