package com.samdev.historicprices.ui.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.samdev.historicprices.CoroutineRule
import com.samdev.historicprices.data.local.DummyPrefManager
import com.samdev.historicprices.data.repositories.DummyProductRepository
import com.samdev.historicprices.getOrAwaitValueTest
import com.samdev.historicprices.utils.Status
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.TimeoutException

/**
 * @author Sam
 */
@ExperimentalCoroutinesApi
class HomeViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val coroutineRule = CoroutineRule()

    private lateinit var viewModel: HomeViewModel
    private lateinit var repository: DummyProductRepository
    private lateinit var prefManager: DummyPrefManager

    @Before
    fun setup() {
        // test view model with dummy repository
        repository = DummyProductRepository()
        prefManager = DummyPrefManager()
        viewModel = HomeViewModel(repository, prefManager)
    }

    @Test
    fun insert_product_with_empty_name_and_invalid_price_posts_error() = runBlockingTest {
        val name = ""
        val price = -99.0
        viewModel.addNewProduct(name, price)

        val state = viewModel.notification.getOrAwaitValueTest()
        assertThat(state.getContentIfNotHandled()?.status).isEqualTo(Status.ERROR)
    }

    @Test
    fun insert_product_with_empty_name_and_valid_price_posts_error() = runBlockingTest {
        val name = ""
        val price = 99.0
        viewModel.addNewProduct(name, price)

        val state = viewModel.notification.getOrAwaitValueTest()
        assertThat(state.getContentIfNotHandled()?.status).isEqualTo(Status.ERROR)
    }

    @Test
    fun insert_product_with_empty_name_and_null_price_posts_error() = runBlockingTest {
        val name = ""
        val price = null
        viewModel.addNewProduct(name, price)

        val state = viewModel.notification.getOrAwaitValueTest()
        assertThat(state.getContentIfNotHandled()?.status).isEqualTo(Status.ERROR)
    }


    @Test
    fun insert_product_with_valid_name_and_null_price_posts_success() = runBlockingTest {
        val name = "asasas"
        val price = null
        viewModel.addNewProduct(name, price)

        val state = viewModel.notification.getOrAwaitValueTest()
        assertThat(state.getContentIfNotHandled()?.status).isEqualTo(Status.SUCCESS)
    }


    @Test
    fun insert_product_with_valid_name_and_valid_price_posts_success() = runBlockingTest {
        val name = "asasas"
        val price = 12.0
        viewModel.addNewProduct(name, price)

        val state = viewModel.notification.getOrAwaitValueTest()
        assertThat(state.getContentIfNotHandled()?.status).isEqualTo(Status.SUCCESS)
    }


    @Test
    fun initial_data_fetch_on_first_run_returns_data() = runBlockingTest {
        prefManager.setFirstRun(true)
        viewModel.fetchInitialProducts()
        val state = viewModel.notification.getOrAwaitValueTest()
        assertThat(state.getContentIfNotHandled()?.status).isEqualTo(Status.SUCCESS)
    }

    @Test(expected = TimeoutException::class)
    fun initial_data_fetch_on_subsequent_run_expects_Exception() = runBlockingTest {
        prefManager.setFirstRun(false)
        viewModel.fetchInitialProducts()
        viewModel.notification.getOrAwaitValueTest()
    }


    @Test
    fun initial_data_fetch_without_connection_returns_network_error() = runBlockingTest {
        prefManager.setFirstRun(true)
        repository.setNetworkError(true)
        viewModel.fetchInitialProducts()
        val state = viewModel.notification.getOrAwaitValueTest()
        assertThat(state.getContentIfNotHandled()?.status).isEqualTo(Status.ERROR)
    }



    @After
    fun teardown() {

    }

}