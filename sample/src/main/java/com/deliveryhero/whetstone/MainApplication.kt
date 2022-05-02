package com.deliveryhero.whetstone

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_DEFAULT
import android.content.Context
import android.os.Build
import android.util.Log
import com.deliveryhero.whetstone.MainService.Companion.NOTIFICATION_CHANNEL_ID
import com.deliveryhero.whetstone.component.ApplicationComponent
import com.deliveryhero.whetstone.component.ApplicationComponentOwner
import com.deliveryhero.whetstone.injector.ContributesInjector
import com.deliveryhero.whetstone.scope.ApplicationScope
import javax.inject.Inject

@ContributesInjector(ApplicationScope::class)
public class MainApplication : Application(), ApplicationComponentOwner {

    @Inject
    internal lateinit var dependency: MainDependency

    override val applicationComponent: ApplicationComponent =
        GeneratedApplicationComponent.create(this)

    override fun onCreate() {
        Whetstone.inject(this)
        super.onCreate()
        Log.d("App", dependency.getMessage("Application"))
        registerNotificationChannel()
    }

    private fun registerNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).apply {
                createNotificationChannel(
                    NotificationChannel(NOTIFICATION_CHANNEL_ID, "Main Channel", IMPORTANCE_DEFAULT)
                )
            }
        }
    }
}
