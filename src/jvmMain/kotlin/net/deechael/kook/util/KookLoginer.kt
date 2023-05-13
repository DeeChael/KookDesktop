package net.deechael.kook.util

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import net.deechael.kook.exception.LoginFailedException
import net.deechael.kookcli.network.Routes
import net.deechael.kookdesktop.HTTP_CLIENT
import net.deechael.kookdesktop.LOGGER
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

internal val GSON = Gson()

object KookLoginer {

    fun login(phone: String?, password: String?): String {
        LOGGER.debug("Trying to login as user")
        val params = JsonObject()
        params.addProperty("mobile", phone)
        params.addProperty("mobile_prefix", "86")
        params.addProperty("password", password)
        params.addProperty("remember", false)
        val req = Request.Builder()
            .post(GSON.toJson(params).toRequestBody("application/json".toMediaType()))
            .header("Content-type", "application/json")
            .url(Routes.AUTH_LOGIN).build()

        val call = HTTP_CLIENT.newCall(req)
        val response = call.execute()
        if (!response.isSuccessful) {
            LOGGER.debug("Login failed because the request is not legal")
            throw LoginFailedException()
        }
        val body = JsonParser.parseString(response.body!!.string()).asJsonObject
        response.close()
        LOGGER.debug("Response body when login as user: {}", body)
        if (!body.has("token"))
            throw LoginFailedException()
        val userInfo = body["user"].asJsonObject
        LOGGER.info("Login successfully")
        LOGGER.info("Login as ${userInfo["username"].asString}#${userInfo["identify_num"].asString}")
        return body["token"].asString
    }

}