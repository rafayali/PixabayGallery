package com.rafay.gallery.screens.home

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.rafay.gallery.databinding.ItemImageBinding

sealed class HomeViewHolder(
    view: View,
) : RecyclerView.ViewHolder(view) {
    abstract fun bind(item: HomeItem)

    class Image(
        view: View,
        private val onImageClickListener: (view: View, position: Int) -> Unit,
    ) : HomeViewHolder(view) {
        private val binding = ItemImageBinding.bind(view)

        override fun bind(item: HomeItem) {
            with(item as HomeItem.Image) {
                Glide
                    .with(binding.thumbImage.context)
                    .load(thumbUrl)
                    .centerCrop()
                    .thumbnail(0.1f)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(binding.thumbImage)

                binding.thumbImage.transitionName = "${item.id}"

                binding.flImage.setOnClickListener {
                    onImageClickListener.invoke(binding.flImage, bindingAdapterPosition)
                }
            }
        }
    }

    class Loader(
        view: View,
    ) : HomeViewHolder(view) {
        override fun bind(item: HomeItem) {
        }
    }
}
