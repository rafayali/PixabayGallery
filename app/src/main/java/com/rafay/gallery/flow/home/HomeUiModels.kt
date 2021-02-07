package com.rafay.gallery.flow.home

import com.rafay.gallery.api.Images
import java.util.*

sealed class HomeItem(open val id: Long) {

    data class Image(
        override val id: Long,
        val title: String,
        val thumbUrl: String,
        val url: String
    ) : HomeItem(id)

    object Loader : HomeItem(UUID.randomUUID().leastSignificantBits)
}

fun Images.Hit.toHomeImageItem() = HomeItem.Image(
    id = id,
    title = "Image $id",
    thumbUrl = previewURL,
    url = largeImageURL,
)