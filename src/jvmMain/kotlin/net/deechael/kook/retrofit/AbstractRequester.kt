package net.deechael.kook.retrofit

import com.google.gson.JsonObject
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.QueryMap

interface AbstractRequester {

    @GET("{path}")
    fun get(@Path("path") path: String, @QueryMap queries: Map<String, String>): JsonObject

    @POST("{path}")
    fun post(@Path("path") path: String, @Body requestBody: RequestBody): JsonObject

}