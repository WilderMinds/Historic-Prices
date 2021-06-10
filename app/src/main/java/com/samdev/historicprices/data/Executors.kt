package com.samdev.historicprices.data

import java.util.concurrent.Executors

/**
 * @author Sam
 */
private val IO_EXECUTOR = Executors.newSingleThreadExecutor()

// utility method used to help run tasks or blocks of code in a background thread
fun ioThread(function : () -> Unit) {
    IO_EXECUTOR.execute(function)
}