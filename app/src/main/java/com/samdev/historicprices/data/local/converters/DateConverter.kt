package com.samdev.historicprices.data.local.converters

import androidx.room.TypeConverter
import java.text.SimpleDateFormat
import java.time.OffsetDateTime
import java.util.*

/**
 * @author Sam
 */

@TypeConverter
fun offsetStringToDate(value: String?): Date? {
    return value?.let {
        val offsetDateTime = OffsetDateTime.parse(value)
        Date.from(offsetDateTime.toInstant())
    }
}

@TypeConverter
fun dateToOffsetString(date: Date?): String? {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
    return date?.let {
        dateFormat.format(date)
    }
}