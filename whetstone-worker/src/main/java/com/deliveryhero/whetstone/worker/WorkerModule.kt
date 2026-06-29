package com.deliveryhero.whetstone.worker

import androidx.work.ListenableWorker
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Multibinds
import kotlin.reflect.KClass

@BindingContainer
@ContributesTo(WorkerScope::class)
public interface WorkerModule {

    @Multibinds(allowEmpty = true)
    public fun provideWorkers(): Map<KClass<*>, ListenableWorker>
}
