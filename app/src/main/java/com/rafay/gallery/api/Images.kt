package com.rafay.gallery.api

import com.squareup.moshi.FromJson
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.ToJson

@JsonClass(generateAdapter = true)
data class Images(
    val total: Long,
    val totalHits: Long,
    val hits: List<Hit>,
) {
    @JsonClass(generateAdapter = true)
    data class Hit(
        val id: Long,
        val pageURL: String,
        val type: Type,
        val tags: String,
        val previewURL: String,
        val previewWidth: Long,
        val previewHeight: Long,
        val webformatURL: String,
        val webformatWidth: Long,
        val webformatHeight: Long,
        val largeImageURL: String,
        val imageWidth: Long,
        val imageHeight: Long,
        val imageSize: Long,
        val views: Long,
        val downloads: Long,
        val likes: Long,
        val comments: Long,
        @Json(name = "user_id")
        val userID: Long,
        val user: String,
        val userImageURL: String,
    ) {
        enum class Type(
            val value: String,
        ) {
            Illustration("illustration"),
            Photo("photo"),
            Vector("vector/svg"),
            ;

            companion object {
                fun from(value: String) = entries.first { it.value == value }
            }

            class JsonAdapter {
                @FromJson
                fun fromJson(value: String) = from(value)

                @ToJson
                fun toJson(value: Type) = value.value
            }
        }
    }
}
