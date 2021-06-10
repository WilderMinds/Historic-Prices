package com.samdev.historicprices.data.local.entities

import androidx.room.Embedded
import androidx.room.Relation

/**
 * @author Sam
 */
data class ProductWithPrices(
    @Embedded
    val product: Product,

    @Relation(
        parentColumn = "product_id",
        entityColumn = "parent_product_id"
    )
    val prices: MutableList<Price>
) {
    fun latestPrice(): String {
        prices.sort()

        return if (prices.isNotEmpty()) {
            "${prices[0].price}"
        } else ""
    }
}
