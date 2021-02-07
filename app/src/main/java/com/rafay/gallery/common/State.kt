package com.rafay.gallery.common

sealed class State<out T> {

    data class Success<T>(val data: T): State<T>()

    object Loading : State<Nothing>()

    object Retry : State<Nothing>()
}