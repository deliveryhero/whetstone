package com.deliveryhero.whetstone.worker

import android.content.Context
import androidx.annotation.RestrictTo
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
        val workerClass = loadClass(appContext.classLoader, workerClassName) ?: return null

        return workerComponent.workerMap[workerClass]?.get()
    }

    internal companion object {

        // Implementation adapted from androidx.fragment.app.FragmentFactory#loadClass
        private val classCache: MutableMap<ClassLoader, MutableMap<String, Class<out ListenableWorker>>> =
            hashMapOf()

        @RestrictTo(RestrictTo.Scope.LIBRARY)
        internal fun loadClass(
            classLoader: ClassLoader,
            className: String,
        ): Class<out ListenableWorker>? {
            val classMap = classCache.getOrPut(classLoader) { hashMapOf() }
            return classMap.getOrPut(className) {
                try {
                    Class.forName(className, false, classLoader)
                        .asSubclass(ListenableWorker::class.java)
                } catch (e: ClassNotFoundException) {
                    return null
                } catch (e: ClassCastException) {
                    return null
                }
            }
        }
    }
}
