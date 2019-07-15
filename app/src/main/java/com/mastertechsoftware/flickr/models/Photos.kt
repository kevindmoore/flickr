package com.mastertechsoftware.flickr.models

import com.squareup.moshi.Json

/**
 *
 * {
"photos": {
"page": 1,
"pages": 4623,
"perpage": 100,
"total": "462245",
"photo": [
{
"id": "48261325001",
"owner": "182472710@N03",
"secret": "04853e6dbf",
"server": "65535",
"farm": 66,
"title": "Queretaro, Mexico",
"ispublic": 1,
"isfriend": 0,
"isfamily": 0,
"url_s": "https://live.staticflickr.com/65535/48261325001_04853e6dbf_m.jpg",
"height_s": "168",
"width_s": "240"
},
]
},
"stat": "ok"
}
 */

data class Photo(val id: String, val title: String, @Json(name = "url_s") val url: String, @Json(name = "height_s") val height: String, @Json(name = "width_s") val width: String)
data class Photos(val page: Int, val pages: Int, val perpage: Int, val total: String, val photo: List<Photo>)
data class PhotoResponse(val photos: Photos)