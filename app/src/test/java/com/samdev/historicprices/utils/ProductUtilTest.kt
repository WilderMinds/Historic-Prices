package com.samdev.historicprices.utils

import com.google.common.truth.Truth.*
import org.junit.Test

/**
 * @author Sam
 */
class ProductUtilTest {

    @Test
    fun empty_product_name_returns_false() {
        val name = ""
        val price = 0.0
        val result = ProductUtil.validateCreateProduct(name, price)
        assertThat(result).isFalse()
    }

    @Test
    fun price_less_than_zero_returns_false() {
        val name = "asas"
        val price = -1.0
        val result = ProductUtil.validateCreateProduct(name, price)
        assertThat(result).isFalse()
    }

    @Test
    fun empty_name_and_price_less_than_zero_returns_false() {
        val name = ""
        val price = -2.0
        val result = ProductUtil.validateCreateProduct(name, price)
        assertThat(result).isFalse()
    }

    @Test
    fun filled_name_and_price_greater_than_zero_returns_true() {
        val name = "asdasas"
        val price = 2.0
        val result = ProductUtil.validateCreateProduct(name, price)
        assertThat(result).isTrue()
    }

    @Test
    fun filled_name_and_null_price_returns_true() {
        val name = "asdasas"
        val price = null
        val result = ProductUtil.validateCreateProduct(name, price)
        assertThat(result).isTrue()
    }

}