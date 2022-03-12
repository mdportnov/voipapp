package ru.mephi.voip.call.abto

import android.content.IntentFilter
import androidx.compose.animation.ExperimentalAnimationApi
import org.abtollc.sdk.AbtoApplication
import org.abtollc.sdk.AbtoPhone
import org.koin.android.ext.koin.androidContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import ru.mephi.shared.di.initKoin
import ru.mephi.shared.di.repositoryModule
import ru.mephi.voip.di.koinModule
import ru.mephi.voip.di.viewModels

open class AbtoApp : AbtoApplication(), KoinComponent {
    private val callEventsReceiver: CallEventsReceiver by inject()

    private var appInBackgroundHandler: AppInBackgroundHandler? = null
    override fun onCreate() {
        super.onCreate()

        initKoin {
            androidContext(this@AbtoApp)
            modules(koinModule, viewModels)
        }

        registerReceiver(callEventsReceiver, IntentFilter(AbtoPhone.ACTION_ABTO_CALL_EVENT))
        appInBackgroundHandler = AppInBackgroundHandler()
        registerActivityLifecycleCallbacks(appInBackgroundHandler)
    }

    override fun onTerminate() {
        super.onTerminate()
        unregisterReceiver(callEventsReceiver)
        unregisterActivityLifecycleCallbacks(appInBackgroundHandler)
    }

    val isAppInBackground: Boolean
        get() = appInBackgroundHandler!!.isAppInBackground
}