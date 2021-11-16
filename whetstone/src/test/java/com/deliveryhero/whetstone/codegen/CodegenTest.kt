package com.deliveryhero.whetstone.codegen

import com.deliveryhero.whetstone.injector.AnvilInjector
import com.squareup.anvil.compiler.internal.testing.compileAnvil
import com.squareup.anvil.compiler.internal.testing.extends
import com.tschuchort.compiletesting.KotlinCompilation
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

internal class CodegenTest {

    @Test
    fun contributesFragment() {
        compileAnvil(
            """
                import com.deliveryhero.whetstone.fragment.ContributesFragment
                import androidx.fragment.app.Fragment

                @ContributesFragment
                class MyFragment : Fragment()
            """.trimIndent()
        ) {
            assertEquals(KotlinCompilation.ExitCode.OK, exitCode)

            val module = classLoader.loadClass("MyFragmentBindingsModule")
            assertNotNull(module.declaredMethods.find { it.name == "binds" })
        }
    }

    @Test
    fun contributesViewModel() {
        compileAnvil(
            """
                import com.deliveryhero.whetstone.viewmodel.ContributesViewModel
                import androidx.lifecycle.ViewModel

                @ContributesViewModel
                class MyViewModel : ViewModel()
            """.trimIndent()
        ) {
            assertEquals(KotlinCompilation.ExitCode.OK, exitCode)

            val module = classLoader.loadClass("MyViewModelBindingsModule")
            assertNotNull(module.declaredMethods.find { it.name == "binds" })
        }
    }

    @Test
    fun contributesInjector() {
        compileAnvil(
            """
                import com.deliveryhero.whetstone.injector.ContributesInjector
                import com.deliveryhero.whetstone.scope.ActivityScope
                import android.app.Activity

                @ContributesInjector(ActivityScope::class)
                class MyActivity: Activity()
            """.trimIndent()
        ) {
            assertEquals(exitCode, KotlinCompilation.ExitCode.OK)

            val module = classLoader.loadClass("MyActivityBindingsModule")
            assertNotNull(module.declaredMethods.find { it.name == "binds" })

            val injector = classLoader.loadClass("MyActivityInjector")
            assertTrue(injector.extends(AnvilInjector::class.java))
        }
    }
}
