package com.instana.android

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import android.telephony.TelephonyManager
import com.instana.android.alerts.AlertService
import com.instana.android.core.InstanaConfig
import com.instana.android.core.InstanaLifeCycle
import com.instana.android.core.InstanaWorkManager
import com.instana.android.core.event.CustomEventService
import com.instana.android.core.event.models.AppProfile
import com.instana.android.core.event.models.DeviceProfile
import com.instana.android.core.event.models.Platform
import com.instana.android.core.event.models.UserProfile
import com.instana.android.core.util.ConstantsAndUtil
import com.instana.android.core.util.Logger
import com.instana.android.core.util.MaxCapacityMap
import com.instana.android.core.util.RootCheck
import com.instana.android.crash.CrashEventStore
import com.instana.android.crash.CrashService
import com.instana.android.instrumentation.HTTPMarker
import com.instana.android.instrumentation.InstrumentationService
import com.instana.android.session.SessionService
import com.instana.android.view.ViewChangeService
import java.util.*
import kotlin.properties.Delegates

/**
 * Singleton object that provides all functionality
 *
 * Also implements component callbacks to stop alerting when application is in background
 */
object Instana {

    private lateinit var app: Application
    private lateinit var sessionService: SessionService
    private var lifeCycle: InstanaLifeCycle? = null

    internal lateinit var appProfile: AppProfile
    internal lateinit var deviceProfile: DeviceProfile
    internal val userProfile = UserProfile(null, null, null)
    internal var firstView: String? = null

    private var alertService: AlertService? = null
    private var viewChangeService: ViewChangeService? = null
    internal var instrumentationService: InstrumentationService? = null

    @JvmField
    var customEvents: CustomEventService? = null


    @JvmField
    var crashReporting: CrashService? = null

    /**
     * List of URLs which will not be tracked by Instana, defined by a list of Regex(Kotlin) or Pattern(Java)
     */
    val ignoreURLs = mutableListOf<Regex>()

    /**
     * Map of  ID which all new beacons will be associated with
     */
    val meta = MaxCapacityMap<String, String>(50)

    /**
     * User ID which all new beacons will be associated with
     */
    var userId: String?
        get() = userProfile.userId
        set(value) {
            userProfile.userId = value
        }

    /**
     * User name which all new beacons will be associated with
     */
    var userName: String?
        get() = userProfile.userName
        set(value) {
            userProfile.userName = value
        }

    /**
     * User email which all new beacons will be associated with
     */
    var userEmail: String?
        get() = userProfile.userEmail
        set(value) {
            userProfile.userEmail = value
        }

    /**
     * Indicate whether Google Play Services are missing or not, so this information is associated with all new beacons
     */
    var googlePlayServicesMissing: Boolean?
        get() = deviceProfile.googlePlayServicesMissing
        set(value) {
            deviceProfile.googlePlayServicesMissing = value
        }


    /**
     * Human-readable name of logical view to which beacons will be associated
     */
    var view by Delegates.observable<String?>(null) { _, oldValue, newValue ->
        if (firstView == null && newValue != null) {
            firstView = newValue
        } else if (oldValue != newValue && newValue != null) {
            viewChangeService?.sendViewChange(newValue)
        }
    }

    /**
     * Unique ID assigned by Instana to current session
     */
    var sessionId: String? = null
        internal set

    /**
     * Instana configuration object
     */
    @JvmStatic
    lateinit var config: InstanaConfig
        internal set

    /**
     * Mark the start of a Http Request
     */
    @JvmStatic
    @JvmOverloads
    fun startCapture(url: String, viewName: String? = view): HTTPMarker? =
        instrumentationService?.markCall(url, viewName)

    /**
     * Initialize Instana
     */
    @JvmStatic
    fun setup(app: Application, config: InstanaConfig) {
        initProfiles(app)
        initStoreAndLifecycle(app)
        this.config = config
        Logger.i("Starting Instana agent")
        initWorkManager(Instana.config)
    }

    private fun initStoreAndLifecycle(app: Application) {
        CrashEventStore.init(app)
        this.app = app
        if (lifeCycle == null) {
            lifeCycle = InstanaLifeCycle(app)
        }
    }

    private fun initProfiles(app: Application) {
        val appAndBuildVersion = ConstantsAndUtil.getAppVersionNameAndVersionCode(app)
        val viewportWidthAndHeight = ConstantsAndUtil.getViewportWidthAndHeight(app)
        deviceProfile = DeviceProfile(
            platform = Platform.ANDROID,
            osVersion = "${Build.VERSION.RELEASE} (${Build.VERSION.SDK_INT})",
            deviceManufacturer = Build.MANUFACTURER ?: ConstantsAndUtil.EMPTY_STR,
            deviceModel = Build.MODEL ?: ConstantsAndUtil.EMPTY_STR,
            deviceHardware = Build.HARDWARE,
            rooted = RootCheck.isDeviceRooted(),
            locale = Locale.getDefault(),
            viewportWidth = viewportWidthAndHeight.first,
            viewportHeight = viewportWidthAndHeight.second
        )
        appProfile = AppProfile(
            appVersion = appAndBuildVersion.first,
            appBuild = appAndBuildVersion.second
        )
    }

    private fun initWorkManager(config: InstanaConfig) {
        InstanaWorkManager(config).also {
            crashReporting = CrashService(app, it, config)
            sessionService = SessionService(app, it)
            customEvents = CustomEventService(
                manager = it,
                cm = (app.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager)!!, //TODO don't force-cast
                tm = (app.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager)!!
            ) //TODO don't force-cast
            instrumentationService = InstrumentationService(app, it, config)
            alertService = AlertService(app, config.alerts, lifeCycle!!) //TODO don't force-cast
            viewChangeService = ViewChangeService(app, it)
        }
    }
}
