package com.example.integradora4to.network
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
<<<<<<< HEAD
    private const val BASE_URL="http://192.168.122.170:3000/"
=======
    private const val BASE_URL = "http://192.168.252.130:3000/"
>>>>>>> 9169f819197c1737559a093d91dcc4b3de55303a

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}

