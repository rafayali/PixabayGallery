package com.rafay.gallery.common

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import timber.log.Timber

/**
 * Custom scroll listener for [RecyclerView] which triggers [loadMore] callback when remaining
 * visible items reach or exceed [THRESHOLD] value.
 */
class RecyclerViewPaginationListener(
    private val onScrollStateChange: ((recyclerView: RecyclerView, newState: Int) -> Unit)? = null,
    private val onScrolled: ((recyclerView: RecyclerView, dx: Int, dy: Int) -> Unit)? = null,
    private val loadMore: () -> Unit,
) : RecyclerView.OnScrollListener() {
    override fun onScrolled(
        recyclerView: RecyclerView,
        dx: Int,
        dy: Int,
    ) {
        super.onScrolled(recyclerView, dx, dy)

        onScrolled?.invoke(recyclerView, dx, dy)

        recyclerView.layoutManager?.let {
            val findLastVisibleItem = fun(lastVisibleItemPositions: IntArray): Int {
                var maxSize = 0
                for (i in lastVisibleItemPositions.indices) {
                    if (i == 0) {
                        maxSize = lastVisibleItemPositions[i]
                    } else if (lastVisibleItemPositions[i] > maxSize) {
                        maxSize = lastVisibleItemPositions[i]
                    }
                }
                return maxSize
            }

            val lastVisibleItem =
                when (it) {
                    is LinearLayoutManager -> {
                        it.findLastVisibleItemPosition()
                    }
                    is StaggeredGridLayoutManager -> {
                        findLastVisibleItem(it.findLastVisibleItemPositions(null))
                    }
                    is GridLayoutManager -> {
                        it.findLastVisibleItemPosition()
                    }
                    else -> error("Unsupported layout manager class ${it.javaClass}")
                }

            if (lastVisibleItem >= it.itemCount - THRESHOLD) {
                loadMore.invoke()
            }
        } ?: Timber.e("LayoutManager has not been set")
    }

    override fun onScrollStateChanged(
        recyclerView: RecyclerView,
        newState: Int,
    ) {
        super.onScrollStateChanged(recyclerView, newState)
        onScrollStateChange?.invoke(recyclerView, newState)
    }

    companion object {
        const val THRESHOLD = 20
    }
}
