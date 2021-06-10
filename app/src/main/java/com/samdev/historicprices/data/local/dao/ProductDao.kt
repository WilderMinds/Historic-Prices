package com.samdev.historicprices.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.samdev.historicprices.data.local.entities.Product

/**
 * @author Sam
 */
@Dao
interface ProductDao: BaseDao<Product> {

    @Query("DELETE FROM products WHERE product_id = :id")
    suspend fun deleteById(id: Int)

    @Query("SELECT * FROM products")
    fun fetchAllProducts() : LiveData<List<Product>>

//    @Transaction
//    //@Query("SELECT name, price FROM products _ppp INNER JOIN tb_price ON _ppp.parent_product_id = product_id ORDER BY date DESC ")
//    @Query("SELECT name, price FROM products INNER JOIN tb_price tt ON parent_product_id = product_id AND MAX(tt.date) = date INNER JOIN (SELECT price, MAX(date) as MaxDate FROM tb_price GROUP BY price) p on tt.date = MaxDate")
//    fun getProductsWithLatestPrice() : LiveData<List<ProductLatestPrice>>

}