package com.rafay.gallery.screens.home

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.rafay.gallery.R
import com.rafay.gallery.common.extensions.layoutInflater

class HomeRecyclerViewAdapter(
    private val onImageClickListener: (view: View, position: Int) -> Unit,
) : ListAdapter<HomeItem, HomeViewHolder>(DiffItemCallback) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): HomeViewHolder =
        when (ItemType.entries[viewType]) {
            ItemType.Image ->
                HomeViewHolder.Image(
                    parent.layoutInflater.inflate(R.layout.item_image, parent, false),
                    onImageClickListener,
                )
            ItemType.Loader ->
                HomeViewHolder.Loader(
                    parent.layoutInflater.inflate(R.layout.item_loader, parent, false),
                )
        }

    override fun onBindViewHolder(
        holder: HomeViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
    }

    override fun getItemViewType(position: Int): Int =
        when (getItem(position)) {
            is HomeItem.Image -> ItemType.Image.ordinal
            HomeItem.Loader -> ItemType.Loader.ordinal
        }

    enum class ItemType {
        Image,
        Loader,
    }

    object DiffItemCallback : DiffUtil.ItemCallback<HomeItem>() {
        override fun areItemsTheSame(
            oldItem: HomeItem,
            newItem: HomeItem,
        ): Boolean = oldItem.id == newItem.id

        override fun areContentsTheSame(
            oldItem: HomeItem,
            newItem: HomeItem,
        ): Boolean = oldItem == newItem
    }
}
