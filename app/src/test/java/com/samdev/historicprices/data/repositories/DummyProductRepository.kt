package com.samdev.historicprices.data.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.samdev.historicprices.data.local.entities.Price
import com.samdev.historicprices.data.local.entities.Product
import com.samdev.historicprices.data.local.entities.ProductWithPrices
import com.samdev.historicprices.data.model.ApiProduct
import com.samdev.historicprices.utils.Event
import com.samdev.historicprices.utils.ProductNotification
import com.samdev.historicprices.utils.Resource
import com.samdev.historicprices.utils.toOffsetString
import java.util.*


/**
 * @author Sam
 */
class DummyProductRepository : IProductRepository {

    // initialise dummy datastores to emulate our local database
    private val dummyProductsDataSource = mutableSetOf(
        Product(1, "Paracetamol"),
        Product(2, "Vitamin C"),
    )

    private val dummyPricesDataStore = mutableSetOf(
        Price(id = 1, price = 12.0, productId = 1, date = "2018-11-01T17:16:32+00:00"),
        Price(id = 2, price = 43.0, productId = 1, date = "2019-11-01T17:16:32+00:00"),
        Price(id = 3, price = 54.0, productId = 2, date = "2021-11-01T17:16:32+00:00"),
        Price(id = 4, price = 89.0, productId = 2, date = "2014-12-01T17:16:32+00:00"),
    )


    private val liveProductWithPrices = MutableLiveData(getProductWithPrices())
    private var showNetworkError = false

    /**
     * Emulate failure from network to test
     * error handling by the viewModel
     */
    fun setNetworkError(show: Boolean) {
        showNetworkError = show
    }

    override val stateNotifier: MutableLiveData<Event<Resource<ProductNotification?>>>
        get() = MutableLiveData<Event<Resource<ProductNotification?>>>()


    override suspend fun fetchInitialProducts(): Resource<List<ApiProduct>> {
        return if (showNetworkError) {
            Resource.error("Something happened")
        } else {
            Resource.success(listOf())
        }
    }

    override fun fetchProductWithPrices()
            : LiveData<List<ProductWithPrices>> {
        return liveProductWithPrices
    }

    override suspend fun insertNewProduct(name: String, price: Double?) {
        // create product
        val product = Product(name = name)

        // check if a price was attached
        if (price != null) {
            val prices = mutableListOf(
                Price(
                    price = price,
                    date = Date().toOffsetString()
                )
            )

            // insert product with corresponding price
            insertProductWithPrices(
                ProductWithPrices(product, prices)
            )

            return
        }

        // if no price, store product only
        insertProductOnly(product)
    }


    override suspend fun insertProductOnly(product: Product) {
        dummyProductsDataSource.add(product)
        notifyChange()
    }


    override suspend fun insertProductWithPrices(
        productWithPrices: ProductWithPrices
    ) {
        dummyProductsDataSource.add(productWithPrices.product)
        dummyPricesDataStore.addAll(productWithPrices.prices)
        notifyChange()
    }

    override suspend fun updateProductWithPrices(
        productWithPrices: List<ProductWithPrices>,
    ) {

    }


    override suspend fun updateProductPrice(price: Price) {
        dummyPricesDataStore.add(price)
        notifyChange()
    }


    override suspend fun editProduct(product: Product, price: Double?) {
        val result = dummyProductsDataSource.filter { it.id == product.id }

        if (result.isNotEmpty()) {
            dummyProductsDataSource.remove(result[0])
            dummyProductsDataSource.add(product)
        }

        price?.let {
            dummyPricesDataStore.add(
                Price(
                    price = price,
                    productId = product.id,
                    date = Date().toOffsetString()
                )
            )
        }
        notifyChange()
    }


    override suspend fun deleteProduct(product: Product) {
        val result = dummyProductsDataSource.filter { it.id == product.id }
        if (result.isNotEmpty()) {
            dummyProductsDataSource.remove(result[0])
        }
        notifyChange()
    }


    override suspend fun deleteProductById(id: Int) {
        val result = dummyProductsDataSource.filter { it.id == id }
        if (result.isNotEmpty()) {
            dummyProductsDataSource.remove(result[0])
        }
    }

    override fun getProductById(id: Int): LiveData<ProductWithPrices?> {
        return MutableLiveData(getProductWithPricesById(id))
    }


    /**
     * Emulate database Live Data Callbacks
     */
    private fun notifyChange() {
        liveProductWithPrices.postValue(getProductWithPrices())
    }


    private fun getProductWithPrices(): List<ProductWithPrices> {
        val result = mutableSetOf<ProductWithPrices>()

        for (product in dummyProductsDataSource) {
            val productWithPrices = ProductWithPrices(product, mutableListOf())

            for (price in dummyPricesDataStore) {
                if (product.id == price.productId) {
                    productWithPrices.prices.add(price)
                }
            }
            result.add(productWithPrices)
        }

        return result.toList()
    }


    private fun getProductWithPricesById(id: Int): ProductWithPrices {
        val result = mutableSetOf<ProductWithPrices>()

        for (product in dummyProductsDataSource.filter { it.id == id }) {
            val productWithPrices = ProductWithPrices(product, mutableListOf())

            for (price in dummyPricesDataStore) {
                if (product.id == price.productId) {
                    productWithPrices.prices.add(price)
                }
            }
            result.add(productWithPrices)
        }

        return result.toList()[0]
    }

}