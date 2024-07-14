package com.rafay.gallery.screens.home

import com.rafay.gallery.api.Images
import java.util.UUID

sealed class HomeItem(
    open val id: Long,
) {
    data class Image(
        override val id: Long,
        val thumbUrl: String,
        val url: String,
    ) : HomeItem(id)

    data object Loader : HomeItem(UUID.randomUUID().leastSignificantBits)
}

fun Images.Hit.toHomeImageItem() =
    HomeItem.Image(
        id = id,
        thumbUrl = previewURL,
        url = largeImageURL,
    )
