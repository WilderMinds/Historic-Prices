package com.samdev.historicprices.data.model

import java.io.Serializable

/**
 * @author Sam
 */
data class ProductsApiResponse(
    val products: List<ApiProduct>
): Serializable
