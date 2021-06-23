package com.rusatom.data.repository

import android.util.Log
import com.droidbaza.data.repositories.LoadResult
import retrofit2.Response

object BaseRepository {

    inline fun <T : Any?> safeCall(responseCall: () -> Response<T>): LoadResult<T> {
        return try {
            val response = responseCall.invoke()
            val responseCode = response.code()
            val body = response.body()
            if (response.isSuccessful) {
                Log.d("RESPONCE","SUCCESS")
                LoadResult.Success(body)
            } else {
                Log.d("RESPONCE","ERRROR ${ response.message()}")
                LoadResult.Error(responseCode, response.message())
            }
        } catch (e: Exception) {
            Log.d("RESPONCE","ERRROR ${e.message}")
            LoadResult.Error(500)
        }
    }
}