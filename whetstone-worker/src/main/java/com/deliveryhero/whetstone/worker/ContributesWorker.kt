package com.deliveryhero.whetstone.worker

import androidx.work.ListenableWorker
import com.deliveryhero.whetstone.InternalWhetstoneApi
import com.deliveryhero.whetstone.meta.AutoInstanceBinding

/**
 * Marker annotation signalling that the compiler should generate necessary instance
 * bindings for the annotated worker.
 *
 * For example:
 * Given this annotated worker
 * ```
 * @ContributesWorker
 * class MyWorker @Inject constructor(parameters: WorkerParameters) : Worker()
 * ```
 * a complementary module will be generated
 * ```
 * @Module
 * @ContributesTo(WorkerScope::class)
 * interface MyWorkerModule {
 *     @Binds
 *     @IntoMap
 *     @LazyClassKey(MyWorker::class)
 *     fun binds(target: MyWorker): ListenableWorker
 * }
 * ```
 */
@OptIn(InternalWhetstoneApi::class)
@AutoInstanceBinding(base = ListenableWorker::class, scope = WorkerScope::class)
public annotation class ContributesWorker
