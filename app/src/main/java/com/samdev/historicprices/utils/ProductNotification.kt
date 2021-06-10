package com.samdev.historicprices.utils

/**
 * @author Sam
 *
 * Class to help us propagate and identify different
 * notification types to the UI
 */
sealed class ProductNotification

// send when an product is either created or updated
object ItemUpdated: ProductNotification()

// send when an product is deleted
object ItemDeleted: ProductNotification()

// send when the initial items are fetched
object InitialItemsFetched: ProductNotification()
