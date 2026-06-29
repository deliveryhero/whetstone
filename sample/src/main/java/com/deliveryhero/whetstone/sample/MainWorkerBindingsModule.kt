package com.deliveryhero.whetstone.sample

import androidx.work.ListenableWorker
import com.deliveryhero.whetstone.worker.WorkerScope
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.ClassKey
import dev.zacsweers.metro.IntoMap

@BindingContainer
@ContributesTo(WorkerScope::class)
public interface MainWorkerBindingsModule {
    @Binds
    @IntoMap
    @ClassKey(MainWorker::class)
    public fun binds(target: MainWorker): ListenableWorker
}
