package com.example.file_download_2.new

import retrofit2.Retrofit

import retrofit2.converter.gson.GsonConverterFactory

import okhttp3.OkHttpClient


object ApiService {
    const val API_BASE_URL = "http://10.11.201.180:8081/AgentBanking/"
    private val httpClient = OkHttpClient.Builder()
    private val builder = Retrofit.Builder()
        .baseUrl(API_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())

    fun <S> createService(serviceClass: Class<S>?): S {
        val retrofit = builder.client(httpClient.build()).build()
        return retrofit.create(serviceClass)
    }
}