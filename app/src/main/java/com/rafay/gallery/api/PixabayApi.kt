package com.rafay.gallery.api

import retrofit2.http.GET
import retrofit2.http.Query

interface PixabayApi {

    @GET("/api/?per_page=50&image_type=photo&order=popular&safesearch=true")
    suspend fun getImages(@Query("page") page: Int): Images
}
