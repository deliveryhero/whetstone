package com.deliveryhero.whetstone

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.deliveryhero.whetstone.injector.ContributesInjector
import com.deliveryhero.whetstone.scope.ServiceScope
import javax.inject.Inject

@ContributesInjector(ServiceScope::class)
class MainService : Service() {

    @Inject
    lateinit var dependency: MainDependency

    override fun onCreate() {
        Whetstone.inject(this)
        super.onCreate()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("${this.javaClass.simpleName} is running")
            .setContentText(dependency.getMessage("Service"))
            .setSmallIcon(R.drawable.ic_baseline_textsms_24)
            .setAutoCancel(true)
            .build()
        startForeground(1, notification)

        return START_NOT_STICKY
    }

    companion object {
        const val NOTIFICATION_CHANNEL_ID = "MainChannel"
    }
}
