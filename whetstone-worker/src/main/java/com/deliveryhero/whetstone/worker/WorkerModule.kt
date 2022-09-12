package com.deliveryhero.whetstone.worker

import androidx.work.ListenableWorker
import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.multibindings.Multibinds

@Module
@ContributesTo(WorkerScope::class)
public interface WorkerModule {

    @Multibinds
    public fun provideWorkers(): Map<Class<*>, ListenableWorker>
}
