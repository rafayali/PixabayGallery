package com.rafay.gallery.flow.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.transition.MaterialSharedAxis
import com.rafay.gallery.R
import com.rafay.gallery.common.GridItemDecoration
import com.rafay.gallery.common.RecyclerViewPaginationListener
import com.rafay.gallery.common.State
import com.rafay.gallery.databinding.FragmentHomeBinding
import com.rafay.gallery.flow.detail.DetailFragment
import org.koin.android.viewmodel.ext.android.viewModel

class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var binding: FragmentHomeBinding

    private val viewModel by viewModel<HomeViewModel>()

    private var adapter: HomeRecyclerViewAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.bind(
            requireNotNull(super.onCreateView(inflater, container, savedInstanceState))
        )

        initView()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.state.observe(viewLifecycleOwner) {
            when (it) {
                is State.Success -> {
                    binding.progressCircular.visibility = View.INVISIBLE
                    binding.recyclerView.visibility = View.VISIBLE
                    binding.clRetry.visibility = View.INVISIBLE

                    requireNotNull(adapter).submitList(it.data)
                }
                State.Loading -> {
                    binding.progressCircular.visibility = View.VISIBLE
                    binding.recyclerView.visibility = View.INVISIBLE
                    binding.clRetry.visibility = View.INVISIBLE
                }
                State.Retry -> {
                    binding.progressCircular.visibility = View.INVISIBLE
                    binding.recyclerView.visibility = View.INVISIBLE
                    binding.clRetry.visibility = View.VISIBLE
                }
            }
        }

        viewModel.error.observe(viewLifecycleOwner) {
            it.consume()?.let {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.text_error_home),
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        }
    }

    override fun onDestroyView() {
        binding.recyclerView.clearOnScrollListeners()

        super.onDestroyView()
    }

    private fun initView() {
        adapter = HomeRecyclerViewAdapter { _, position ->
            val images = viewModel.getImages().filterIsInstance<HomeItem.Image>().map {
                DetailFragment.Params.Image(id = it.id, url = it.url)
            }

            findNavController().navigate(
                R.id.action_homeFragment_to_detailFragment,
                bundleOf(
                    DetailFragment.KEY_PARCELABLE_IMAGES to DetailFragment.Params(
                        images = images,
                        currentItem = position
                    )
                ),
            )
        }

        binding.recyclerView.layoutManager =
            GridLayoutManager(requireContext(), GRID_SPAN, GridLayoutManager.VERTICAL, false)
        binding.recyclerView.addOnScrollListener(
            RecyclerViewPaginationListener { viewModel.loadMore() }
        )
        binding.recyclerView.addItemDecoration(GridItemDecoration())
        binding.recyclerView.setPadding(GRID_PADDING, GRID_PADDING, GRID_PADDING, GRID_PADDING)
        binding.recyclerView.clipToPadding = false
        binding.recyclerView.clipChildren = false
        binding.recyclerView.adapter = adapter

        binding.buttonRetry.setOnClickListener {
            viewModel.retry()
        }
    }

    companion object {
        private const val GRID_SPAN = 4
        private const val GRID_PADDING = 8
    }
}