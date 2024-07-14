package com.rafay.gallery.common

sealed class State<out T> {
    data class Success<T>(
        val data: T,
    ) : State<T>()

    data object Loading : State<Nothing>()

    data object Retry : State<Nothing>()
}
