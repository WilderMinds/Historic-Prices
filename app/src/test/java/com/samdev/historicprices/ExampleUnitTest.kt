package com.samdev.historicprices

import org.junit.Test

import org.junit.Assert.*
import java.text.SimpleDateFormat
import java.time.*
import java.util.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }


    @Test
    fun offset_date_to_regular_date() {
        // date format
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")

        // inputs
        val offsetDateInput = "2018-11-01T17:16:32+00:00"
        val regularDateInput = "2018-11-01T17:16:32Z"

        // convert offset date
        val odt = OffsetDateTime.parse(offsetDateInput)


        println("--------")
        println(odt.offset)
        println(odt.offset.id)

        // parse with date format
        val offsetResult = Date.from(odt.toInstant())
        val regularResult = dateFormat.parse(regularDateInput)

        println(offsetResult)
        println(regularResult)

        // check result
        assertEquals(regularResult?.toString(), offsetResult?.toString())
    }

}