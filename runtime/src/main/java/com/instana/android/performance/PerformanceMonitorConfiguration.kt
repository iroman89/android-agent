package com.instana.android.performance

class PerformanceMonitorConfiguration
@JvmOverloads constructor(
    /**
     * A `ANR` alert will be triggered whenever the app is unresponsive above the threshold.
     *
     * Defined in milliseconds
     */
    val anrThreshold: Long = 3000L,

    /**
     * A `frameDip` alert will be triggered whenever the app goes below the threshold.
     *
     * Defined in Frames per second
     */
    val frameRateDipThreshold: Int = 20
)
