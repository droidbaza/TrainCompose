package com.droidbaza.traincompose

import androidx.lifecycle.ViewModel
import com.droidbaza.data.model.MetaPage
import com.droidbaza.data.repositories.LoadResult
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow

data class PageData<T : Any>(
    val items: List<T> = emptyList(),
    val hash: Int = items.hashCode()
)

abstract class ViewModelPageProgress<T : Any>() : ViewModel() {

    var isLastPage: Boolean = false
    var page: Long = 1L
    var target: Long = 0L

    val pageItems by lazy {
        mutableListOf<T>()
    }

    var job: Job? = null

    val liveError: MutableStateFlow<Int?> by lazy {
        MutableStateFlow(null)
    }
    private val _pagesList: MutableStateFlow<PageData<T>> by lazy {
        MutableStateFlow(PageData())
    }

    val newsState: MutableStateFlow<PageData<T>> get() = _pagesList

    fun onResultLoading(result: LoadResult<Pair<MetaPage, List<T>>>) {
        result.apply {
            onSuccess {
                pageItems.addAll(it.second)
                it.first.apply {
                    page = pageNumber
                    isLastPage = isLast
                    if (!isLastPage) page += 1
                }
                onHandlePage(pageItems)
            }
            onError {
                onHandleError(it)
            }
        }
    }

    open fun onRestart() {
        if (job?.isActive == true) return
        target = 0L
        page = 1L
        isLastPage = false
        pageItems.clear()
        onTryNext(page)
    }

    fun onRefresh(isRestart: Boolean = true) {
        if (isRestart) {
            onRestart()
        } else {
            onTryNext(page)
        }
    }

    fun onTryNext(page: Long) {
        if (job?.isActive == true) return
        if (!isLastPage) {
            onJobRequestPage(page)
        } else {
            onHandlePage(pageItems)
        }
    }

    private fun onHandleError(error: Int) {
        liveError.value = error
    }

    private fun onHandlePage(page: List<T>) {
        _pagesList.value = PageData(page)
    }

    abstract fun onJobRequestPage(nextPage: Long)

    fun onNextPage(inProgress: (Boolean) -> Unit) {
        if (!isLastPage) {
            inProgress(true)
            onTryNext(page)
        }
    }
}



