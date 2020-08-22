package com.mariokevin.tendamonitor.util

import android.app.Activity
import android.content.Context
import android.net.wifi.WifiManager
import android.text.format.Formatter
import android.util.Base64
import android.util.Log
import okhttp3.*
import okhttp3.internal.notifyAll
import okhttp3.internal.wait
import org.json.JSONObject
import java.io.IOException
import java.lang.Exception
import java.nio.charset.StandardCharsets

class TendaAPI(pass: String, activity: Activity) {
    var __PASSWORD: String? = null
    var __ROUTERIP: String? = null
    var __ACTIVITY: Activity? = null
    var __COOKIE: String? = ""
    val __TAG = "TendaAPI"

    init {
        __ACTIVITY = activity
        __PASSWORD = pass
        Thread {
            getRouterIP()
            login()
        }.start()
    }

    @Synchronized
    private fun login() {
        val url = "http://$__ROUTERIP/login/Auth"
        val body = FormBody.Builder()
            .addEncoded("password", enconde(__PASSWORD!!))
            .build()
        val request = Request.Builder()
            .url(url)
            .post(body)
            .build()
        val client = OkHttpClient.Builder().followRedirects(false).build()
        client.newCall(request).execute().use { response ->
            if (response.code == 302) {
                if (!response.headers["Set-Cookie"].isNullOrEmpty())
                    __COOKIE = response.headers["Set-Cookie"].toString().split(";")[0]
                else {
                    __ROUTERIP = null
                    __COOKIE = null
                    Log.e(__TAG, "Authentication Failed!")
                }
            } else {
                Log.e(__TAG, "Unexpected response: $response Maybe thisn't a Tenda N301 Router")
                __ROUTERIP = null
                __COOKIE = null
            }
        }
        notifyAll()
    }

    fun getOnlineDevices(): JSONObject {
        if (!checkSesion()) return JSONObject("{}")
        val url = "http://$__ROUTERIP/goform/getQos"
        val body = FormBody.Builder()
            .add("modules", "onlineList")
            .build()
        val request = Request.Builder()
            .url(url)
            .headers(buildHeader())
            .post(body)
            .build()
        val client = OkHttpClient()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")
            return JSONObject(response.body!!.string())
        }
    }

    fun getDeviceStatistics(): JSONObject {
        if (!checkSesion()) return JSONObject("{}")
        val url = "http://$__ROUTERIP/goform/getStatus"
        val body = FormBody.Builder()
            .add("modules", "deviceStatistics")
            .build()
        val request = Request.Builder()
            .url(url)
            .headers(buildHeader())
            .post(body)
            .build()
        val client = OkHttpClient()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")
            return JSONObject(response.body!!.string())
        }
    }

    private fun buildHeader(): Headers {
        val header = Headers.Builder()
            .add("Cookie", "bLanguage=es;$__COOKIE")
            .add("Host", __ROUTERIP!!)
            .add("Referer", "http://$__ROUTERIP/index.html")
            .build()
        return header
    }

    @Synchronized
    fun checkSesion(): Boolean {
        try {
            if (__COOKIE!!.equals("")) {
                wait()
            }
        } catch (e: Exception) {
        }
        if (__ROUTERIP.isNullOrEmpty()) {
            Log.e(__TAG, "Router session isn't setted up yet!")
            return false
        }
        return true
    }

    private fun getRouterIP() {
        if (__ROUTERIP.isNullOrEmpty()) {
            val manager = __ACTIVITY?.baseContext
                ?.getSystemService(Context.WIFI_SERVICE) as WifiManager
            __ROUTERIP = Formatter.formatIpAddress(manager.dhcpInfo.gateway)
        }
    }

    private fun enconde(string: String): String {
        val data: ByteArray = string.toByteArray(StandardCharsets.UTF_8)
        val base64: String = Base64.encodeToString(data, Base64.DEFAULT)
        return base64
    }
}