package com.deliveryhero.whetstone.worker

import android.app.Application
import android.content.Context
import androidx.startup.Initializer
import androidx.work.Configuration
import androidx.work.WorkManager
import com.deliveryhero.whetstone.Whetstone
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

public class WhetstoneWorkerInitializer : Initializer<WorkManager> {

    override fun create(context: Context): WorkManager {
        val application = context.applicationContext as Application
        Whetstone.installWorkerFactory(application)

        return WorkManager.getInstance(context)
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> = arrayListOf()
}


private fun Whetstone.installWorkerFactory(application: Application) {
    val configuration = runBlocking(Dispatchers.IO) {
        val parentComponent = fromApplication<WorkerComponent.ParentComponent>(application)
        return@runBlocking Configuration.Builder()
            .setWorkerFactory(parentComponent.getWorkerFactory())
            .build()
    }
    WorkManager.initialize(application, configuration)
}
