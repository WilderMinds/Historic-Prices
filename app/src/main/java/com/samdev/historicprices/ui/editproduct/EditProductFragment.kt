package com.samdev.historicprices.ui.editproduct

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.samdev.historicprices.data.local.entities.ProductWithPrices
import com.samdev.historicprices.databinding.FragmentEditProductBinding
import com.samdev.historicprices.ui.home.HomeViewModel
import com.samdev.historicprices.utils.*
import dagger.hilt.android.AndroidEntryPoint
import java.lang.Exception


@AndroidEntryPoint
class EditProductFragment : Fragment() {

    companion object {
        fun newInstance() = EditProductFragment()
    }

    private val viewModel: HomeViewModel by activityViewModels()
    private lateinit var binding: FragmentEditProductBinding

    private var inEditState = false
    private var productWithPrices: ProductWithPrices? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditProductBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeUINotifications()
        observeSelectedProduct()
        initClickListeners()
    }

    private fun initClickListeners() {
        binding.apply {
            btnSave.setOnClickListener {
                saveOrUpdateProduct()
            }
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
                presetDataFields(it)
                inEditState = true
            }
        })
    }


    /**
     * Preset fields for existing product
     */
    private fun presetDataFields(item: ProductWithPrices) {
        binding.apply {
            etProductName.setText(item.product.name)
            etProductPrice.setText(item.latestPrice())
        }
    }


    /**
     * Save or update depending on [inEditState] value
     */
    private fun saveOrUpdateProduct() {
        if (inEditState) {
            updateProduct()
        } else {
            saveNewProduct()
        }
    }


    /**
     * Check if the user has made any changes, if he hasn't,
     * just exit
     */
    private fun updateProduct() {

        lifecycleScope.launchWhenResumed {
            productWithPrices?.let {
                if (isEditRequired()) {

                    // get updated product
                    val product = it.product.apply {
                        name = binding.etProductName.text.toString()
                    }

                    // get price if its set
                    val price: Double? = getNewPrice()

                    // update product
                    viewModel.editProduct(product, price)
                } else {
                    // exit
                    Toast.makeText(context, "The product has not been modified", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    /**
     * Get newly entered price string as double
     * if left blank, we do not set a new price
     * if equal to current latest price, we do not set
     * a new price either
     */
    private fun getNewPrice() : Double? {
        return try {
            val priceStr = binding.etProductPrice.text.toString()
            val oldPrice = productWithPrices?.latestPrice().orEmpty()
            if (priceStr.isBlank() || priceStr == oldPrice) {
                null
            } else {
                priceStr.toDouble()
            }
        } catch (e: Exception) {
            null
        }
    }


    /**
     * Compare previous state with current state and
     * check whether the product has been modified at all
     */
    private fun isEditRequired() : Boolean {
        return isProductModified() || isPriceModified()
    }


    /**
     * Check whether the product is modified
     */
    private fun isProductModified() : Boolean {
        return productWithPrices?.let {
            val currentProductName = binding.etProductName.text.toString()
            val previousProductName = it.product.name
            currentProductName != previousProductName
        } ?: false
    }


    /**
     * Check whether the price is modified to a value
     * other than an empty string.
     *
     * We ignore the price when updating if its null
     */
    private fun isPriceModified() : Boolean {
        return productWithPrices?.let {
            val currentPrice = binding.etProductPrice.text.toString()
            val previousPrice = it.latestPrice()

            if (currentPrice.isBlank()) {
                false
            } else {
                currentPrice != previousPrice
            }
        } ?: false
    }


    /**
     * Save new item with optional price to
     * local db
     */
    private fun saveNewProduct() {
        lifecycleScope.launchWhenResumed {
            val name = binding.etProductName.text.toString().trim()
            val price = getNewPrice()
            viewModel.addNewProduct(name, price)
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


    private fun handleLoadingState(item: Any?) {
        item?.let {
            Toast.makeText(context, "loading", Toast.LENGTH_LONG).show()
        }
    }


    private fun handleErrorState(item: String?) {
        val msg = item ?: "Error"
        item?.let {
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
        }
    }


    /**
     * Navigate back when product is edited or deleted successfully
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
}