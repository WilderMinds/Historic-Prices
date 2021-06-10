package com.samdev.historicprices.data.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.samdev.historicprices.data.local.AppDatabase
import com.samdev.historicprices.data.local.entities.Price
import com.samdev.historicprices.data.local.entities.Product
import com.samdev.historicprices.data.local.entities.ProductWithPrices
import com.samdev.historicprices.data.model.ApiProduct
import com.samdev.historicprices.data.remote.ApiService
import com.samdev.historicprices.utils.*
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import kotlin.Exception

/**
 * @author Sam
 */
class ProductRepository @Inject constructor(
    private val database: AppDatabase,
    private val apiService: ApiService
) : IProductRepository {

    override val stateNotifier = MutableLiveData<Event<Resource<ProductNotification?>>>()

    /**
     * Fetch Items from API
     */
    override suspend fun fetchInitialProducts(): Resource<List<ApiProduct>> {
        return try {
            apiService.fetchInitialData()?.let {
                return@let Resource.success(it.products)
            } ?: Resource.error("No Data Found!")

        } catch (e: Exception) {
            Resource.error("Sorry, we were unable to fetch data")
        }
    }


    override fun fetchProductWithPrices(): LiveData<List<ProductWithPrices>> {
        return database.productWithPricesDao().getProductsWithPrices()
    }

    override suspend fun insertNewProduct(name: String, price: Double?) {
        try {
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
                notifySuccess(ItemUpdated)
                return
            }

            // if no price, store product only
            insertProductOnly(product)
            notifySuccess(ItemUpdated)
        } catch (e: Exception) {
            e.printStackTrace()
            notifyError(e.message ?: "Failed to insert new product")
        }
    }


    override suspend fun insertProductOnly(product: Product) {
        database.productDao().insert(product)
    }


    override suspend fun insertProductWithPrices(
        productWithPrices: ProductWithPrices,
    ) {
        database.productWithPricesDao().insertProductsWithPrices(
            product = productWithPrices.product,
            prices = productWithPrices.prices
        )
    }

    override suspend fun updateProductWithPrices(
        productWithPrices: List<ProductWithPrices>,
    ) {
        for (item in productWithPrices) {
            Timber.e("updateProductWithPrices -> $item")
            database.productWithPricesDao()
                .updateProductWithPrices(item.product, item.prices)
        }
    }


    override suspend fun updateProductPrice(price: Price) {
        database.priceDao().insert(price)
    }


    /**
     * Update product in db,
     * if the user does not present a price, we
     * only update the product
     */
    override suspend fun editProduct(
        product: Product,
        price: Double?
    ) {
        try {
            database.productDao().insert(product)
            price?.let {
                database.priceDao().insert(
                    Price(
                        price = price,
                        productId = product.id,
                        date = Date().toOffsetString()
                    )
                )
            }
            notifySuccess(ItemUpdated)
        } catch (e: Exception) {
            e.printStackTrace()
            notifyError("Failed to edit product")
        }
    }

    override suspend fun deleteProduct(product: Product) {
        database.productDao().delete(product)
    }

    override suspend fun deleteProductById(id: Int) {
        try {
            database.productDao().deleteById(id)
            notifySuccess(ItemDeleted)
        } catch (e: Exception) {
            e.printStackTrace()
            notifyError("Failed to delete product")
        }
    }

    override fun getProductById(id: Int): LiveData<ProductWithPrices?> {
        return database.productWithPricesDao().getProductsWithPricesById(id)
    }


    /**
     * Notify UI when a transaction has completed successfully
     */
    private fun notifySuccess(type: ProductNotification, ignore: Boolean = false) {
        stateNotifier.postValue(
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
        stateNotifier.postValue(
            Event(
                Resource.error(msg),
                ignore
            )
        )
    }

}