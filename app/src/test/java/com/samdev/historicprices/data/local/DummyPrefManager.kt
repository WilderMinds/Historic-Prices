package com.samdev.historicprices.data.local

import com.samdev.historicprices.data.local.preferences.IPrefManager

/**
 * @author Sam
 */
class DummyPrefManager: IPrefManager {

    private var firstRun = false

    override fun setFirstRun(status: Boolean) {
        firstRun = status
    }

    override fun isFirstRun(): Boolean {
        return firstRun
    }


}