package com.droidbaza.data.di

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.droidbaza.data.api.ApiMovies
import com.droidbaza.data.repositories.MoviesRepository
import com.droidbaza.data.repositories.MoviesRepositoryImpl
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

private const val BASE_URL = "https://api.themoviedb.org/3/"

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(@ApplicationContext context: Context): OkHttpClient {
        return OkHttpClient.Builder().apply {
            addInterceptor(ChuckerInterceptor(context))
        }.build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()
    }

    @Provides
    @Singleton
    fun provideApiMovies(retrofit: Retrofit) = retrofit.create(ApiMovies::class.java)


    @Provides
    @Singleton
    fun provideMoviesRepository(apiMovies: ApiMovies): MoviesRepository {
        return MoviesRepositoryImpl(apiMovies)
    }

}
