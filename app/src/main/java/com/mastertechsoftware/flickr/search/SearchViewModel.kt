package com.mastertechsoftware.flickr.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mastertechsoftware.flickr.models.Photo
import com.mastertechsoftware.flickr.network.FlickrAPI
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * Our Search View Model. Handles the search
 */
typealias SearchResult = (List<Photo>?) -> Unit

class SearchViewModel: ViewModel() {
    var totalPages = 0
    var currentPage = 0
    var currentSearch: String = ""
    var currentJob: Job? = null

    fun searchPhotos(currentText: String, result: SearchResult) {
        currentSearch = currentText
        currentJob?.cancel()
        currentJob = viewModelScope.launch {
            val response = FlickrAPI.getPhotos(currentSearch, currentPage)
            response?.let {
                totalPages = it.photos.pages
                result.invoke(it.photos.photo)
            }
        }
    }

    fun searchNextPhotos(result: SearchResult) {
        if (currentPage + 1 < totalPages) {
            currentPage += 1
            searchPhotos(currentSearch, result)
        }
    }

    companion object {
        const val PAGE_SIZE = 100
    }
}