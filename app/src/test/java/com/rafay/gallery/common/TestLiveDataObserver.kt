package com.rafay.gallery.common

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

/**
 * Observer for tests which records all values emitted by [LiveData]
 */
class TestLiveDataObserver<T> : Observer<T> {

    private val _values = mutableListOf<T>()
    val values: List<T> = _values

    override fun onChanged(value: T) {
        _values.add(value)
    }

    fun clear() {
        _values.clear()
    }
}