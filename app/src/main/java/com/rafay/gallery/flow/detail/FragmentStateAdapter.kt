package com.rafay.gallery.flow.detail

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class ImageDetailPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    private val _images = mutableListOf<DetailFragment.Params.Image>()

    override fun getItemCount() = _images.count()

    override fun createFragment(position: Int) = ImageFragment.newInstance(_images[position].url)

    override fun getItemId(position: Int): Long {
        return _images[position].id
    }

    override fun containsItem(itemId: Long) = _images.any { it.id == itemId }

    fun setImages(images: List<DetailFragment.Params.Image>) {
        _images.clear()
        _images.addAll(images)
        notifyDataSetChanged()
    }
}
