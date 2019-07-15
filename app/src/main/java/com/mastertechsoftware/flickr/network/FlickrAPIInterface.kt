package com.mastertechsoftware.flickr.network

import com.mastertechsoftware.flickr.models.PhotoResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 *
 */
interface FlickrAPIInterface {
    // ?method=flickr.photos.search&api_key=675894853ae8ec6c242fa4c077bcf4a0&text=dogs&extras=url_s&format=json&nojsoncallback=1#
    @GET("services/rest/?method=flickr.photos.search&api_key=${FlickrAPI.API_KEY}&extras=url_s&format=json&nojsoncallback=1")
    suspend fun getPhotos(@Query("text") search: String, @Query("page") page: Int, @Query("per_page") pageSize: Int): PhotoResponse

}