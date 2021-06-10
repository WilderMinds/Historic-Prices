package com.samdev.historicprices.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samdev.historicprices.data.local.preferences.IPrefManager
import com.samdev.historicprices.data.local.entities.Product
import com.samdev.historicprices.data.local.entities.ProductWithPrices
import com.samdev.historicprices.data.repositories.IProductRepository
import com.samdev.historicprices.utils.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import kotlin.Exception

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: IProductRepository,
    private val prefManager: IPrefManager
) : ViewModel() {

    /**
     * Listen for errors and success during transactions
     * Set the ignore flag on the event to `true` if we do
     * not want the UI to react to the new event notification
     *
     * @see fetchInitialProducts for use-case
     */
    private val _notification = repository.stateNotifier
    val notification: MutableLiveData<Event<Resource<ProductNotification?>>> = _notification


    /**
     * Keep track of the selected product id
     */
    private var selectedProductId = 0


    /**
     * public Product list observer
     */
    val productList = repository.fetchProductWithPrices()


    /**
     * Fetch initial products from API and store them in database
     *
     * Keep track of whether initial products have been fetched
     * successfully and skip fetch for subsequent runs
     *
     * Notify success but ignore result, because the database callback
     * will be triggered for us and we can leverage that to update our UI
     */
    suspend fun fetchInitialProducts() = viewModelScope.launch {

        // only fetch initial data on first run
        val isFirstRun = prefManager.isFirstRun()
        if (isFirstRun) {

            notifyLoading()

            val response = repository.fetchInitialProducts()
            Timber.e("response => $response")

            if (response.status == Status.SUCCESS) {
                response.data?.let { apiProducts ->
                    val morphedList = apiProducts.map { it.toProductWithPrice() }
                    repository.updateProductWithPrices(morphedList)

                    // ignore notification
                    notifySuccess(
                        type = InitialItemsFetched,
                        ignore = true
                    )

                    prefManager.setFirstRun(false)
                }
            } else {
                notifyError(
                    msg = response.message ?: "Something went wrong, unable to fetch inital items"
                )
            }
        }
    }


    /**
     * Currently selected product
     */
    fun selectProduct(item: ProductWithPrices?) {
        selectedProductId = item?.product?.id ?: 0
    }


    /**
     * Observe changes made to the currently selected product
     */
    fun getSelectedProduct(): LiveData<ProductWithPrices?> {
        return repository.getProductById(selectedProductId)
    }


    /**
     * Add new product using UI form
     */
    suspend fun addNewProduct(name: String, price: Double?) {
        try {
            Timber.e("validate name=>$name & price=>$price")
            if (ProductUtil.validateCreateProduct(name, price)) {
                // if no price, store product only
                repository.insertNewProduct(name, price)
                notifySuccess(ItemUpdated)
            } else {
                throw Exception("Product validation failed")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            notifyError(
                msg = e.message ?: "Something went wrong"
            )
        }
    }


    /**
     * Inserting using object
     */
    fun addNewProduct(productWithPrices: ProductWithPrices) = viewModelScope.launch {
        repository.insertProductWithPrices(productWithPrices)
    }


    /**
     * Insert new price for a known product
     */
    suspend fun editProduct(product: Product, price: Double?) = viewModelScope.launch {
        if (ProductUtil.validateCreateProduct(product.name, price)) {
            Timber.e("editProduct => $product <> $price")
            repository.editProduct(product, price)
        } else {
            notifyError("Product validation failed")
        }
    }


    /**
     * Delete product leaving their prices
     */
    fun deleteProduct(product: Product) = viewModelScope.launch {
        Timber.e("delete product => $product")
        repository.deleteProductById(product.id)
    }


    /**
     * Notify UI when a transaction has completed successfully
     */
    private fun notifySuccess(type: ProductNotification, ignore: Boolean = false) {
        _notification.postValue(
            Event(
                Resource.success(type),
                ignore
            )
        )
    }


    /**
     * Notify UI when an error has occurred
     */
    private fun notifyError(msg: String, ignore: Boolean = false) {
        _notification.postValue(
            Event(
                Resource.error(msg),
                ignore
            )
        )
    }


    /**
     * Notify UI when a resource is loading
     */
    private fun notifyLoading() {
        _notification.postValue(
            Event(
                Resource.loading(null),
            )
        )
    }
}