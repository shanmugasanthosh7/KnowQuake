package com.sample.knowquake.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.aptus.droidils.utils.isNetworkConneted
import com.sample.knowquake.network.NetworkManager

class ConnectivityReceiver : BroadcastReceiver() {

    companion object {
        var connectivityReceiverListener: ConnectivityReceiverListener? = null
    }

    interface ConnectivityReceiverListener {
        fun onNetworkConnectionChanged(z: Boolean)
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (connectivityReceiverListener != null) {
            connectivityReceiverListener!!.onNetworkConnectionChanged(context.isNetworkConneted())
        }
    }
}