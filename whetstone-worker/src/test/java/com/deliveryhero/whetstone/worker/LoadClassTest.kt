package com.deliveryhero.whetstone.worker

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

internal class LoadClassTest {
    @Test
    fun `loadClass returns null for non-existing class`() {
        val result = MultibindingWorkerFactory.loadClass(
            this.javaClass.classLoader!!,
            "non.existent.worker.Class"
        )
        assertNull(result)
    }

    @Test
    fun `loadClass returns null for class which is not ListenableWorker subclass`() {
        val result = MultibindingWorkerFactory.loadClass(
            this.javaClass.classLoader!!,
            "com.deliveryhero.whetstone.worker.LoadClassTest\$TestClass"
        )
        assertNull(result)
    }

    @Test
    fun `loadClass returns class for existing classname`() {
        val result = MultibindingWorkerFactory.loadClass(
            this.javaClass.classLoader!!,
            "com.deliveryhero.whetstone.worker.LoadClassTest\$TestWorker"
        )
        assertEquals(TestWorker::class.java, result)
    }

    private class TestWorker(
        context: Context,
        workerParams: WorkerParameters,
    ) : Worker(context, workerParams) {
        override fun doWork(): Result {
            TODO("Not implemented")
        }
    }

    @Suppress("unused")
    private class TestClass
}
