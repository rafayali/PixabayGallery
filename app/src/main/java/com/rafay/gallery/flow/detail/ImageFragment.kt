package com.rafay.gallery.flow.detail

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.rafay.gallery.R
import com.rafay.gallery.databinding.FragmentImageBinding

class ImageFragment : Fragment(R.layout.fragment_image) {

    private lateinit var binding: FragmentImageBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentImageBinding.bind(
            requireNotNull(super.onCreateView(inflater, container, savedInstanceState))
        )

        Glide.with(binding.imageView.context)
            .load(requireNotNull(requireArguments().getString(KEY_STRING_IMAGE_URL)))
            .thumbnail(0.1f)
            .listener(
                object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        binding.progressIndicator.visibility = View.INVISIBLE
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        binding.progressIndicator.visibility = View.INVISIBLE
                        return false
                    }
                }
            )
            .into(binding.imageView)

        return binding.root
    }

    companion object {
        private const val KEY_STRING_IMAGE_URL = "imageUrl"

        fun newInstance(url: String) = ImageFragment().apply {
            arguments = bundleOf(
                KEY_STRING_IMAGE_URL to url,
            )
        }
    }
}
