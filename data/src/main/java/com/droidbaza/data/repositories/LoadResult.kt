package com.droidbaza.data.repositories

sealed class LoadResult<out T : Any?> {
    data class Success<out T : Any?>(
        val data: T?
    ) : LoadResult<T>()

    data class Error(val code: Int = 500, val message: String? = null) : LoadResult<Nothing>()
    object Loading : LoadResult<Nothing>()

    inline fun onSuccess(data: (T) -> Unit) {
        if (this is Success) {
            if (this.data != null) {
                data(this.data)
            }
        }
    }

    inline fun onError(error: (Int) -> Unit) {
        if (this is Error) {
            error(this.code)
        }
    }

    inline fun onLoading(loading: () -> Unit) {
        if (this is Loading) {
            loading()
        }
    }
}
