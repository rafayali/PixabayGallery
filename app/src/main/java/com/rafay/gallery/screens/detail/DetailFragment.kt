package com.rafay.gallery.screens.detail

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.transition.MaterialSharedAxis
import com.rafay.gallery.R
import com.rafay.gallery.databinding.FragmentDetailBinding
import kotlinx.parcelize.Parcelize

class DetailFragment : Fragment(R.layout.fragment_detail) {
    private lateinit var binding: FragmentDetailBinding

    private var pagerAdapter: ImageDetailPagerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding =
            FragmentDetailBinding.bind(
                requireNotNull(super.onCreateView(inflater, container, savedInstanceState)),
            )

        initViews()

        return binding.root
    }

    private fun initViews() {
        pagerAdapter =
            ImageDetailPagerAdapter(this).also {
                val images =
                    requireNotNull(
                        requireArguments().getParcelable<Params>(KEY_PARCELABLE_IMAGES),
                    )
                it.setImages(images.images)
            }
        binding.viewPager.adapter = pagerAdapter
        binding.viewPager.setCurrentItem(
            requireNotNull(
                requireArguments().getParcelable<Params>(KEY_PARCELABLE_IMAGES),
            ).currentItem,
            false,
        )

        binding.imageButtonBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    companion object {
        const val KEY_PARCELABLE_IMAGES = "images"
    }

    @Parcelize
    data class Params(
        val images: List<Image>,
        val currentItem: Int,
    ) : Parcelable {
        @Parcelize
        data class Image(
            val id: Long,
            val url: String,
        ) : Parcelable
    }
}
