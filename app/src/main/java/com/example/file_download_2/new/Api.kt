package com.example.file_download_2.new

import okhttp3.ResponseBody
import retrofit2.http.*


interface Api {

    @Streaming
    @FormUrlEncoded
    @POST("NoticeDownloadS")
    fun getFileType(@Field("id") id: String?): retrofit2.Call<ResponseBody>

    @Streaming
    @GET("App")
    fun getApkFile(): retrofit2.Call<ResponseBody>
}