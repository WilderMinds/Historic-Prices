package com.samdev.historicprices.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.samdev.historicprices.data.local.entities.Price

/**
 * @author Sam
 */
@Dao
interface PriceDao: BaseDao<Price> {

    @Query("SELECT * FROM tb_price")
    fun fetchAllPrices() : LiveData<List<Price>>

}