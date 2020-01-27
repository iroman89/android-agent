package com.instana.android.alerts.anr

import com.instana.android.alerts.AlertsConfiguration
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * A class supervising the UI thread for ANR errors. Use
 * [.start] and [.stop] to control
 * when the UI thread is supervised
 */
class AnrSupervisor
internal constructor(
        alertsConfiguration: AlertsConfiguration,
        callback: AnrCallback
) {

    /**
     * The [AnrSupervisorRunnable] running on a separate thread
     */
    private val supervisor: AnrSupervisorRunnable = AnrSupervisorRunnable(alertsConfiguration, callback)

    /**
     * The [ExecutorService] checking the UI thread
     */
    private val executor = Executors.newSingleThreadExecutor()

    internal interface AnrCallback {
        fun onAppNotResponding(anrThread: AnrException, duration: Long)
    }

    /**
     * Starts the supervision
     */
    @Synchronized
    internal fun start() {
        synchronized(this.supervisor) {
            if (this.supervisor.isStopped) {
                this.executor.execute(this.supervisor)
            } else {
                this.supervisor.unStop()
            }
        }
    }

    /**
     * Stops the supervision. The stop is delayed, so if start() is called right after stop(),
     * both methods will have no effect. There will be at least one
     * more ANR check before the supervision is stopped.
     */
    @Synchronized
    internal fun stop() {
        this.supervisor.stop()
    }
}