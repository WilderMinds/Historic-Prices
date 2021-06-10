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

/**
 * @author Sam
 *
 * Abstract repository into interface to make it
 * easier to mock and test.
 *
 * Also provides cleaner architecture for scaling
 * and adding functionality
 */
interface IProductRepository {

    val stateNotifier: MutableLiveData<Event<Resource<ProductNotification?>>>

    suspend fun fetchInitialProducts() : Resource<List<ApiProduct>>

    fun fetchProductWithPrices() : LiveData<List<ProductWithPrices>>

    suspend fun insertNewProduct(name: String, price: Double?)

    suspend fun insertProductOnly(product: Product)

    suspend fun insertProductWithPrices(productWithPrices: ProductWithPrices)

    suspend fun updateProductWithPrices(productWithPrices: List<ProductWithPrices>)

    suspend fun updateProductPrice(price: Price)

    suspend fun editProduct(product: Product, price: Double?)

    suspend fun deleteProduct(product: Product)

    suspend fun deleteProductById(id: Int)

    fun getProductById(id: Int) :  LiveData<ProductWithPrices?>

}