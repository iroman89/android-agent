package com.instana.android.session

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.telephony.TelephonyManager
import androidx.annotation.RestrictTo
import com.instana.android.Instana
import com.instana.android.core.InstanaWorkManager
import com.instana.android.core.event.models.Beacon
import com.instana.android.core.event.models.ConnectionProfile
import com.instana.android.core.util.ConstantsAndUtil.getCarrierName
import com.instana.android.core.util.ConstantsAndUtil.getCellularConnectionType2
import com.instana.android.core.util.ConstantsAndUtil.getConnectionType2
import java.util.*

@RestrictTo(RestrictTo.Scope.LIBRARY)
class SessionService(
    app: Application,
    manager: InstanaWorkManager
) {

    /**
     * Send session to backend upon each fresh start of the app
     */
    init {
        val sessionId = UUID.randomUUID().toString()
        val tm = (app.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager)
        val cm = (app.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager)
        val connectionProfile = ConnectionProfile(
            carrierName = if (cm != null && tm != null) getCarrierName(cm, tm) else null,
            connectionType = if (cm != null) getConnectionType2(cm) else null,
            effectiveConnectionType = if (cm != null && tm != null) getCellularConnectionType2(cm, tm) else null
        )
        val session = Beacon.newSessionStart(
            appKey = Instana.configuration.key,
            appProfile = Instana.appProfile,
            deviceProfile = Instana.deviceProfile,
            connectionProfile = connectionProfile,
            sessionId = sessionId
        )

        Instana.currentSessionId = sessionId

        manager.send(session)
    }
}
