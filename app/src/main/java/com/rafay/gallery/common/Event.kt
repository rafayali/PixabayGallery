package com.rafay.gallery.common

/**
 * Values class which holds data until its consumed.
 */
class Event<T>(var data: T?) {

    private var handled = false

    /**
     * Returns non null data if not already handled
     */
    fun consume(): T? {
        if (handled) {
            return null
        }
        handled = true
        return data
    }
}