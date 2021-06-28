package com.droidbaza.traincompose

import androidx.lifecycle.ViewModel
import com.droidbaza.data.model.MetaPage
import com.droidbaza.data.repositories.LoadResult
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

abstract class PagedViewModel<T : Any>() : ViewModel() {

    private var isLastPage: Boolean = false
    private var page: Long = 1L
    var job: Job? = null

    private val _errorState: MutableStateFlow<Int?> by lazy {
        MutableStateFlow(null)
    }
    val errorState: StateFlow<Int?> get() = _errorState

    private val _loading: MutableStateFlow<Boolean> by lazy {
        MutableStateFlow(false)
    }
    val loading: StateFlow<Boolean> get() = _loading

    private val _itemsState: MutableStateFlow<List<T>> by lazy {
        MutableStateFlow(emptyList())
    }
    val itemsState: StateFlow<List<T>> get() = _itemsState

    fun onResultLoading(result: LoadResult<Pair<MetaPage, List<T>>>) {
        result.apply {
            onSuccess {
                it.first.apply {
                    page = pageNumber
                    isLastPage = isLast
                    if (!isLastPage) page += 1
                }
                onHandlePage(it.second)
            }
            onError {
                onHandleError(it)
            }
        }
    }

    open fun onRestart() {
        if (job?.isActive == true) return
        page = 1L
        isLastPage = false
        onReset()
        onTryNext(page)
    }

    fun onReset() {
        _itemsState.value = emptyList()
        _errorState.value = null
    }

    fun onRefresh(isRestart: Boolean = true) {
        if (isRestart) {
            onRestart()
        } else {
            onTryNext(page)
        }
    }

    open fun onTryNext(page: Long) {
        _loading.value = true
        if (job?.isActive == true) return
        if (!isLastPage) {
            onJobRequestPage(page)
        } else {
            onHandlePage()
        }
    }

    open fun onHandleError(error: Int) {
        _loading.value = false
        _errorState.value = error
    }

    private fun onHandlePage(page: List<T>? = null) {
        if (_loading.value) _loading.value = false
        if (_errorState.value != null) _errorState.value = null
        _itemsState.value = _itemsState.value + (page ?: emptyList())
    }

    abstract fun onJobRequestPage(nextPage: Long)

    fun onNextPage() {
        if (!isLastPage) {
            onTryNext(page)
        }
    }
}



