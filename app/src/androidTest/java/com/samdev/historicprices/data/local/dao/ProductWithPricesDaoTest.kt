package com.samdev.historicprices.data.local.dao

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.samdev.historicprices.data.local.AppDatabase
import com.samdev.historicprices.data.local.entities.Price
import com.samdev.historicprices.data.local.entities.Product
import com.samdev.historicprices.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Test

import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith

/**
 * @author Sam
 */
@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class ProductWithPricesDaoTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    // init test components
    private lateinit var database: AppDatabase
    private lateinit var dao: ProductWithPricesDao
    private lateinit var productDao: ProductDao
    private lateinit var priceDao: PriceDao

    @Before
    fun setup() {
        // create db in memory rather than usual persistence storage
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()

        // get dao
        dao = database.productWithPricesDao()
        productDao = database.productDao()
        priceDao = database.priceDao()
    }

    @After
    fun teardown() {
        // close db after each test
        database.close()
    }


    @Test
    fun insertProductsWithPrices_insertsProduct() = runBlockingTest {

        val product = Product(0, "Vitamin C")
        val prices = mutableListOf(
            Price(
                price = 12.0,
                date = "12-07-01"
            ),
            Price(
                price = 12.0,
                date = "12-07-01"
            ),
        )

        // action
        dao.insertProductsWithPrices(product, prices)

        // result
        val result = productDao.fetchAllProducts().getOrAwaitValue()

        // assert
        assertThat(result).isNotEmpty()
    }


    @Test
    fun insertProductsWithPrices_insertsPrices() = runBlockingTest {

        val product = Product(0, "Vitamin C")
        val prices = mutableListOf(
            Price(
                price = 12.0,
                date = "12-07-01"
            ),
            Price(
                price = 12.0,
                date = "12-07-01"
            ),
        )

        // action
        dao.insertProductsWithPrices(product, prices)

        // result
        val result = priceDao.fetchAllPrices().getOrAwaitValue()

        // assert
        assertThat(result).isNotEmpty()
    }


    @Test
    fun deleteProduct_doesNotDeletePrice() = runBlockingTest{
        val product = Product(1, "Vitamin C")
        val prices = mutableListOf(
            Price(
                price = 12.0,
                date = "12-07-01"
            ),
            Price(
                price = 12.0,
                date = "12-07-02"
            ),
        )

        // insert items
        dao.insertProductsWithPrices(product, prices)

        // delete product
        productDao.deleteById(product.id)

        // result
        val result = priceDao.fetchAllPrices().getOrAwaitValue()

        // assert
        assertThat(result).hasSize(2)
    }
}