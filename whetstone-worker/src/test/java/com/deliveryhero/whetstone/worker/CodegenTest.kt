package com.deliveryhero.whetstone.worker

import com.squareup.anvil.compiler.internal.testing.compileAnvil
import com.tschuchort.compiletesting.KotlinCompilation
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@OptIn(ExperimentalCompilerApi::class)
internal class CodegenTest {

    @Test
    fun contributesWorker() {
        compileAnvil(
            """
                import android.content.Context
                import androidx.work.Worker
                import androidx.work.WorkerParameters
                import com.deliveryhero.whetstone.worker.ContributesWorker
                import com.deliveryhero.whetstone.worker.WorkerScope
                import com.squareup.anvil.annotations.optional.ForScope
                import javax.inject.Inject

                @ContributesWorker
                class MyWorker @Inject constructor(
                    @ForScope(WorkerScope::class) context: Context, 
                    parameters: WorkerParameters
                ) : Worker(context, parameters) {
                    override fun doWork() = TODO("not implemented")
                }
            """.trimIndent()
        ) {
            assertEquals(KotlinCompilation.ExitCode.OK, exitCode)

            val module = classLoader.loadClass("MyWorkerBindingsModule")
            assertNotNull(module.declaredMethods.find { it.name == "binds" })
        }
    }
}
