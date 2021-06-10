package com.samdev.historicprices.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.samdev.historicprices.data.local.entities.Price
import com.samdev.historicprices.data.local.entities.Product
import com.samdev.historicprices.data.local.entities.ProductWithPrices

/**
 * @author Sam
 */

@Dao
abstract class ProductWithPricesDao {

    @Transaction
    @Query("SELECT * FROM products")
    abstract fun getProductsWithPrices() : LiveData<List<ProductWithPrices>>

    @Transaction
    @Query("SELECT * FROM products WHERE product_id =:id LIMIT 1")
    abstract fun getProductsWithPricesById(id: Int) : LiveData<ProductWithPrices?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun updateProductWithPrices(product: Product, prices: List<Price>)

    suspend fun insertProductsWithPrices(product: Product, prices: List<Price>) {

        // insert new item
        val newId = insetProduct(product)

        // set relational id
        prices.map {
            it.productId = newId.toInt()
        }

        // insert prices
        insertPrices(prices)
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insetProduct(product: Product) : Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertPrices(product: List<Price>)

}