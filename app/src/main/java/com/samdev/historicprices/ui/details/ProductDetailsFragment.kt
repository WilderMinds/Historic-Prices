package com.samdev.historicprices.ui.details

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.samdev.historicprices.R
import com.samdev.historicprices.data.local.entities.ProductWithPrices
import com.samdev.historicprices.databinding.FragmentProductDetailsBinding
import com.samdev.historicprices.ui.common.PriceListAdapter
import com.samdev.historicprices.ui.home.HomeViewModel
import com.samdev.historicprices.utils.ItemDeleted
import com.samdev.historicprices.utils.ItemUpdated
import com.samdev.historicprices.utils.ProductNotification
import com.samdev.historicprices.utils.Status

/**
 * A simple [Fragment] subclass.
 * Use the [ProductDetailsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProductDetailsFragment : Fragment() {

    companion object {
        @JvmStatic
        fun newInstance() = ProductDetailsFragment()
    }

    private lateinit var binding: FragmentProductDetailsBinding
    private val viewModel: HomeViewModel by activityViewModels()
    private val listAdapter = PriceListAdapter()
    private var productWithPrices: ProductWithPrices? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProductDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        initClickListeners()
        observeUINotifications()
        observeSelectedProduct()
    }

    private fun initClickListeners() {
        binding.apply {
            btnEdit.setOnClickListener {
                editProduct()
            }

            btnDelete.setOnClickListener {
                deleteProduct()
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
     * Listen to UI notifications sent by
     * viewModel and react if necessary
     */
    private fun observeUINotifications() {
        viewModel.notification.observe(viewLifecycleOwner, { event ->
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


    /**
     * Handle Success notifications received
     */
    private fun handleSuccessState(item: Any?) {
        item?.let {
            if (item is ProductNotification) {
                when(item) {

                    is ItemUpdated -> {
                        Toast.makeText(context, "Product updated", Toast.LENGTH_SHORT).show()
                        findNavController().popBackStack()
                    }

                    is ItemDeleted -> {
                        Toast.makeText(context, "Product deleted", Toast.LENGTH_SHORT).show()
                        findNavController().popBackStack()
                    }
                }
            }
        }
    }


    /**
     * Indicate resource laoding to user
     */
    private fun handleLoadingState(item: Any?) {
        item?.let {
            val loadingMsg  = it as String
            Toast.makeText(context, loadingMsg, Toast.LENGTH_LONG).show()
        }
    }


    /**
     * Indicate user error
     */
    private fun handleErrorState(item: Any?) {
        item?.let {
            val errorMsg = it as String
            Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
        }
    }

    /**
     * Listen to updates to selected product
     * and reflect the changes in the UI
     */
    private fun observeSelectedProduct() {
        viewModel.getSelectedProduct().observe(viewLifecycleOwner, {
            it?.let {
                productWithPrices = it
                binding.item = it
                it.prices.sort()
                listAdapter.submitList(it.prices)
            }
        })
    }

    private fun deleteProduct() {
        productWithPrices?.let {
            viewModel.deleteProduct(it.product)
        }
    }

    private fun editProduct() {
        findNavController().navigate(R.id.action_productDetailsFragment_to_editProductFragment)
    }
}