package com.samdev.historicprices.utils

/**
 * @author Sam
 */
object ProductUtil {

    /**
     * Return false if
     * 1. Product name is empty
     * 2. Price is less than 0
     *
     * Null Price is valid input
     */
    fun validateCreateProduct(name: String, price: Double?) : Boolean {

        if (name.isBlank()) {
            return false
        }

        if (price != null && price < 0) {
            return false
        }

        return true
    }

    fun validateCreatePrice() : Boolean {
        TODO("Yet to implement")
    }
}