package com.samdev.historicprices.data.remote

import com.samdev.historicprices.data.model.ProductsApiResponse
import retrofit2.http.GET

/**
 * @author Sam
 */
interface ApiService {

    @GET("5c3e15e63500006e003e9795")
    suspend fun fetchInitialData() : ProductsApiResponse?

}