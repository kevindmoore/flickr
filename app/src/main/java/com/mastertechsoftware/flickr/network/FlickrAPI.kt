package com.mastertechsoftware.flickr.network

import android.util.Log
import com.mastertechsoftware.flickr.models.PhotoResponse
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

/**
 *
 */

object FlickrAPI {
    const val BASE_URL = "https://api.flickr.com/"
    const val API_KEY = "675894853ae8ec6c242fa4c077bcf4a0"
    private val service: FlickrAPIInterface

    init {
        val httpClient = OkHttpClient().newBuilder()
        httpClient.connectTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY

        httpClient.addInterceptor(interceptor)

        val moshi = Moshi.Builder()
            // ... add your own JsonAdapters and factories ...
            .add(KotlinJsonAdapterFactory())
            .build()
        val retrofit = Retrofit.Builder().baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(httpClient.build())
            .build()

        service = retrofit.create(FlickrAPIInterface::class.java)

    }

    suspend fun getPhotos(search: String, page: Int, pageSize: Int = 100): PhotoResponse? {
        try {
            return service.getPhotos(search, page, pageSize)
        } catch (e: Exception) {
            Log.e("FlickrAPI", "Problems getting Photos", e)
        }
        return null
    }
}