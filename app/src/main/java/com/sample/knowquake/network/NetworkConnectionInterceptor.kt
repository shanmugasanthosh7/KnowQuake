package com.sample.knowquake.network

import android.content.Context

import java.io.IOException
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

import javax.inject.Inject

class NetworkConnectionInterceptor @Inject
constructor(private val context: Context) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        if (!NetworkManager.isConnected(context)) throw NoNetworkException()
        return chain.proceed(request)
    }
}