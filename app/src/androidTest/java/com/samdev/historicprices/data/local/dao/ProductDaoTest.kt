package com.samdev.historicprices.data.local.dao

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.samdev.historicprices.data.local.AppDatabase
import com.samdev.historicprices.data.local.entities.Product
import com.samdev.historicprices.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * @author Sam
 */
@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class ProductDaoTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    // init test components
    private lateinit var database: AppDatabase
    private lateinit var productDao: ProductDao

    @Before
    fun setup() {
        // create db in memory rather than usual persistence storage
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()

        // get dao
        productDao = database.productDao()
    }

    @After
    fun teardown() {
        // close db after each test
        database.close()
    }


    @Test
    fun insertProduct() = runBlockingTest {

        // insert product
        val product = Product(1, "Paracetamol")
        productDao.insert(product)

        // check if the product was actually inserted
        val result = productDao.fetchAllProducts().getOrAwaitValue()

        // assert
        assertThat(result).contains(product)

    }


    @Test
    fun deleteById() = runBlockingTest {

        // insert item with id = 1
        val product1 = Product(1, "Paracetamol")
        productDao.insert(product1)

        // insert item with id = 2
        val product2 = Product(2, "Tums")
        productDao.insert(product2)

        // delete the product with id = 1.
        // we do not first check if the item was inserted because
        // if the previous insert test passes, this insert will too
        productDao.deleteById(1)

        // check result
        val result = productDao.fetchAllProducts().getOrAwaitValue()

        // assert that product 1 is actually gone
        assertThat(result).doesNotContain(product1)
    }

}