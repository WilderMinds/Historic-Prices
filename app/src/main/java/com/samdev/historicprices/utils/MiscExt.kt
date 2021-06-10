package com.samdev.historicprices.utils

import com.samdev.historicprices.data.local.entities.Product
import com.samdev.historicprices.data.local.entities.ProductWithPrices
import com.samdev.historicprices.data.model.ApiProduct
import java.text.SimpleDateFormat
import java.time.OffsetDateTime
import java.util.*

/**
 * @author Sam
 */


fun String.toDate() : Date? {
    val str = this.replace("\\s".toRegex(), "")
    val offsetDateTime = OffsetDateTime.parse(str)
    return Date.from(offsetDateTime.toInstant())
}

fun Date.toOffsetString(): String? {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
    return dateFormat.format(this)
}

fun Date.toReadableString(): String? {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy @ HH:mm:ss", Locale.getDefault())
    return dateFormat.format(this)
}

fun ApiProduct.toProductWithPrice() : ProductWithPrices {
    val product = Product(id, name)
    prices.map { it.productId = id }
    return ProductWithPrices(product, prices)
}