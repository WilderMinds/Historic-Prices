package com.samdev.historicprices.data.local.preferences

/**
 * @author Sam
 */
interface IPrefManager {
    fun setFirstRun(status: Boolean)
    fun isFirstRun(): Boolean
}