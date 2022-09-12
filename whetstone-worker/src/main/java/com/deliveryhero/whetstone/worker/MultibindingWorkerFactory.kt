package com.deliveryhero.whetstone.worker

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.deliveryhero.whetstone.app.ApplicationScope
import com.squareup.anvil.annotations.ContributesBinding
import dagger.Reusable
import javax.inject.Inject

@Reusable
@ContributesBinding(ApplicationScope::class)
public class MultibindingWorkerFactory @Inject constructor(
    private val workerComponentFactory: WorkerComponent.Factory
) : WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        val workerComponent = workerComponentFactory.create(appContext, workerParameters)
        val workerClass = loadClass(appContext.classLoader, workerClassName)

        return workerComponent.workerMap[workerClass]?.get()
    }

    private companion object {

        // Implementation adapted from androidx.fragment.app.FragmentFactory#loadClass
        private val classCache: MutableMap<ClassLoader, MutableMap<String, Class<out ListenableWorker>>> = hashMapOf()

        @Throws(ClassNotFoundException::class)
        private fun loadClass(classLoader: ClassLoader, className: String): Class<out ListenableWorker> {
            val classMap = classCache.getOrPut(classLoader) { hashMapOf() }
            return classMap.getOrPut(className) {
                val rawClass = Class.forName(className, false, classLoader)
                rawClass.asSubclass(ListenableWorker::class.java)
            }
        }
    }
}
