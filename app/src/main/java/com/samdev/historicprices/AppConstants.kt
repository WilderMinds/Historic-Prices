package com.samdev.historicprices

/**
 * @author Sam
 */
object AppConstants {
    // network
    const val NETWORK_CONNECT_TIMEOUT: Long = 30
    const val NETWORK_READ_TIMEOUT: Long = 30
    const val BASE_URL = "https://www.mocky.io/v2/"

    // shared preferences
    const val PREF_NAME = "mPharmaPriceHistory"
    const val PREF_IS_FIRST_RUN = "PREF_IS_FIRST_RUN"
}