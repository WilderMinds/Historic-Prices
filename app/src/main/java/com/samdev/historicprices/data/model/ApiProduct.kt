package com.samdev.historicprices.data.model

import com.samdev.historicprices.data.local.entities.Price

/**
 * @author Sam
 */
data class ApiProduct(
    val id: Int,
    val name: String,
    val prices: MutableList<Price>
)