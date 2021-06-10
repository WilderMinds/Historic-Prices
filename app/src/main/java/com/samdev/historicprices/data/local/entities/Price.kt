package com.samdev.historicprices.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.samdev.historicprices.utils.toDate

/**
 * @author Sam
 */
@Entity(tableName = "tb_price")
data class Price(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "price_id")
    val id: Int = 0,

    var price: Double,

    @ColumnInfo(name = "parent_product_id")
    var productId: Int = 0,

    val date: String?,

) : Comparable<Price> {

    override fun compareTo(other: Price): Int {
        return if (date == null || other.date == null) {
            0
        } else {
            other.date.toDate()?.compareTo(date.toDate()) ?: 0
        }
    }
}
