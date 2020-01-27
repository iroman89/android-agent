package com.instana.android.instrumentation

import android.content.Context
import android.net.ConnectivityManager
import android.telephony.TelephonyManager
import com.instana.android.core.InstanaConfiguration
import com.instana.android.core.InstanaWorkManager
import com.instana.android.core.event.EventFactory
import com.instana.android.core.util.ConstantsAndUtil.TYPE_ERROR
import com.instana.android.core.util.ConstantsAndUtil.TYPE_SUCCESS


class InstrumentationService(
        context: Context,
        private val manager: InstanaWorkManager,
        private val configuration: InstanaConfiguration
) {

    private val tagSet = hashSetOf<String>()
    private val cm: ConnectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private val tm: TelephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

    fun getConnectionManager(): ConnectivityManager {
        return cm
    }

    fun getTelephonyManager(): TelephonyManager {
        return tm
    }

    fun setType(type: InstrumentationType) {
        // change instrumentation type
        configuration.remoteCallInstrumentationType = type.type
    }

    fun markCall(url: String, method: String): RemoteCallMarker = RemoteCallMarker(url, method, manager)

    fun markCall(url: String): RemoteCallMarker = RemoteCallMarker(url, manager = manager)

    fun reportCall(url: String, method: String, startTime: Long, duration: Long, error: String) {
        val event = EventFactory.createRemoteCall(url, method, TYPE_ERROR, startTime, duration, error)
        manager.send(event)
    }

    fun reportCall(url: String, method: String, startTime: Long, duration: Long, responseCode: Int) {
        val event = EventFactory.createRemoteCall(url, method, TYPE_SUCCESS, startTime, duration, responseCode)
        manager.send(event)
    }

    fun hasTag(header: String): Boolean = tagSet.contains(header)

    fun addTag(tag: String) {
        tagSet.add(tag)
    }

    fun removeTag(tag: String) {
        tagSet.remove(tag)
    }
}
