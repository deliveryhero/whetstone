package com.deliveryhero.whetstone.worker

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.deliveryhero.whetstone.ForScope
import com.deliveryhero.whetstone.SingleIn
import com.deliveryhero.whetstone.app.ApplicationScope
import com.squareup.anvil.annotations.ContributesTo
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Multibinds
import dev.zacsweers.metro.Provides
import javax.inject.Provider
import kotlin.reflect.KClass

/**
 * A Metro component that has the lifetime of the [androidx.work.ListenableWorker].
 */
@GraphExtension
public interface WorkerComponent {
    public val workerMap: Map<KClass<*>, Provider<ListenableWorker>>

    @Multibinds(allowEmpty = true)
    public fun provideWorkers(): Map<KClass<*>, ListenableWorker>

    /**
     * Interface for creating an [WorkerComponent].
     */
    @GraphExtension.Factory
    public interface Factory {
        public fun create(
            @Provides @ForScope(WorkerScope::class) appContext: Context,
            @Provides parameters: WorkerParameters
        ): WorkerComponent
    }

    @ContributesTo(ApplicationScope::class)
    public interface ParentComponent {
        public fun getWorkerComponentFactory(): Factory
        public fun getWorkerFactory(): WorkerFactory
    }
}
