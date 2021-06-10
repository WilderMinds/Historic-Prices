package com.samdev.historicprices.ui.common

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.samdev.historicprices.utils.toDate
import com.samdev.historicprices.utils.toReadableString
import timber.log.Timber

/**
 * @author Sam
 */

@BindingAdapter("formattedDate")
fun TextView.setFormattedDate(rawDate: String?) {
    Timber.e("bind raw date $rawDate")
    rawDate?.let {
        val date = it.toDate()
        text = date?.toReadableString()
    }
}
