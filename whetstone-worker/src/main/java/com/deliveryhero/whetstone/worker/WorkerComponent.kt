package com.deliveryhero.whetstone.worker

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.deliveryhero.whetstone.app.ApplicationScope
import com.squareup.anvil.annotations.ContributesSubcomponent
import com.squareup.anvil.annotations.ContributesTo
import com.squareup.anvil.annotations.optional.ForScope
import com.squareup.anvil.annotations.optional.SingleIn
import dagger.BindsInstance
import javax.inject.Provider

/**
 * A Dagger component that has the lifetime of the [androidx.work.ListenableWorker].
 */
@ContributesSubcomponent(scope = WorkerScope::class, parentScope = ApplicationScope::class)
@SingleIn(WorkerScope::class)
public interface WorkerComponent {
    public val workerMap: Map<Class<*>, Provider<ListenableWorker>>

    /**
     * Interface for creating an [WorkerComponent].
     */
    @ContributesSubcomponent.Factory
    public interface Factory {
        public fun create(
            @BindsInstance @ForScope(WorkerScope::class) appContext: Context,
            @BindsInstance parameters: WorkerParameters
        ): WorkerComponent
    }

    @ContributesTo(ApplicationScope::class)
    public interface ParentComponent {
        public fun getWorkerComponentFactory(): Factory
        public fun getWorkerFactory(): WorkerFactory
    }
}
