package com.samdev.historicprices.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.samdev.historicprices.R
import com.samdev.historicprices.data.local.entities.ProductWithPrices
import com.samdev.historicprices.databinding.FragmentHomeBinding
import com.samdev.historicprices.ui.common.CustomDialogs
import com.samdev.historicprices.ui.common.ProductListAdapter
import com.samdev.historicprices.utils.*
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class HomeFragment : Fragment(), OnClickItem {

    companion object {
        fun newInstance() = HomeFragment()
    }

    private val viewModel: HomeViewModel by activityViewModels()
    private val listAdapter = ProductListAdapter(this)
    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initClickListeners()
        initRecyclerView()
        observeUINotifications()
        observeProductList()
        fetchData()
    }


    private fun initClickListeners() {
        binding.apply {
            fabAddProduct.setOnClickListener {
                viewModel.selectProduct(null)
                findNavController().navigate(R.id.action_homeFragment_to_editProductFragment)
            }
        }
    }


    private fun initRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(context)
        binding.apply {
            recyclerView.layoutManager = linearLayoutManager
            recyclerView.adapter = listAdapter
        }
    }


    /**
     * Fetch Initial data from API
     * Wrap in coroutine scope to ensure leaks and
     * allow cancellation
     */
    private fun fetchData() {
        lifecycleScope.launchWhenResumed {
            viewModel.fetchInitialProducts()
        }
    }


    /**
     * Listen to UI notifications triggered by the viewModel
     * But react only if the event ignore flag is false
     */
    // TODO: 09/06/2021 Abstract this functionality into a base fragment
    private fun observeUINotifications() {
        viewModel.notification.observe(viewLifecycleOwner, { event ->
            hideLoading()
            if (!event.ignore) {
                event.getContentIfNotHandled()?.let {
                    when(it.status) {
                        Status.LOADING -> handleLoadingState(it.data)
                        Status.SUCCESS -> handleSuccessState(it.data)
                        else-> handleErrorState(it.message)
                    }
                }
            }
        })
    }


    private fun handleSuccessState(item: Any?) {
        Toast.makeText(context, "success", Toast.LENGTH_LONG).show()
        hideLoading()
    }


    private fun handleLoadingState(item: Any?) {
        Toast.makeText(context, "loading", Toast.LENGTH_SHORT).show()
        showLoading()
    }


    private fun handleErrorState(msg: String?) {
        msg?.let {
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            CustomDialogs.showErrorDialog(
                requireContext(),
                msg
            ) {
                fetchData()
            }
        }
        hideLoading()
    }


    private fun showLoading() {
        binding.progressLayout.show()
    }

    private fun hideLoading() {
        binding.progressLayout.hide()
    }


    /**
     * Observe changes to product database
     * and update list accordingly
     */
    private fun observeProductList() {
        viewModel.productList.observe(viewLifecycleOwner, {
            it?.let {
                listAdapter.submitList(it)
            }
        })
    }


    /**
     * Set selected product and route to product details
     */
    override fun onClick(item: Any?) {
        Timber.e("received item $item")
        if (item is ProductWithPrices) {
            Timber.e("set selected product and route")
            viewModel.selectProduct(item)
            navigateToProductDetails()
        }
    }


    private fun navigateToProductDetails() {
        findNavController().navigate(R.id.action_homeFragment_to_productDetailsFragment)
    }

}