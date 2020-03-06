package com.instana.android.core

import com.instana.android.performance.PerformanceMonitorConfiguration
import com.instana.android.instrumentation.HTTPCaptureConfig

class InstanaConfig
@JvmOverloads constructor(
    val reportingURL: String,
    val key: String,
    var httpCaptureConfig: HTTPCaptureConfig = HTTPCaptureConfig.AUTO,

    var suspendReportingReporting: SuspendReportingType = SuspendReportingType.LOW_BATTERY_AND_CELLULAR_CONNECTION,
    var enableCrashReporting: Boolean = false,
    var performanceMonitor: PerformanceMonitorConfiguration = PerformanceMonitorConfiguration(),
    var eventsBufferSize: Int = 1, // TODO find good value
    var breadcrumbsBufferSize: Int = 20,
    var initialBeaconDelay: Long = 5
)