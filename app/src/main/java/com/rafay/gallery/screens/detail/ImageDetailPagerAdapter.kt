package com.rafay.gallery.screens.detail

import android.annotation.SuppressLint
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class ImageDetailPagerAdapter(
    fragment: Fragment,
) : FragmentStateAdapter(fragment) {
    private val images = mutableListOf<DetailFragment.Params.Image>()

    override fun getItemCount() = images.count()

    override fun createFragment(position: Int) = ImageFragment.newInstance(images[position].url)

    override fun getItemId(position: Int): Long = images[position].id

    override fun containsItem(itemId: Long) = images.any { it.id == itemId }

    @SuppressLint("NotifyDataSetChanged")
    fun setImages(images: List<DetailFragment.Params.Image>) {
        this.images.clear()
        this.images.addAll(images)
        notifyDataSetChanged()
    }
}
