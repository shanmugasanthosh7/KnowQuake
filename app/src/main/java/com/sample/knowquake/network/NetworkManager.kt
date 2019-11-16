package com.sample.knowquake.network


import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo

object NetworkManager {

    /**
     * This function is used to check for network connectivity. if network available it will return true.
     * else it will return false.
     */
    fun isConnected(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return try {
            val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
            activeNetwork?.isConnectedOrConnecting == true
        } catch (e: Exception) {
            false
        }

    }
}
