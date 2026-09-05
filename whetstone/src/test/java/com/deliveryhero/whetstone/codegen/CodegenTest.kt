package com.deliveryhero.whetstone.codegen

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import com.deliveryhero.whetstone.activity.ActivityScope
import com.deliveryhero.whetstone.app.ApplicationScope
import com.deliveryhero.whetstone.fragment.FragmentScope
import com.deliveryhero.whetstone.viewmodel.ViewModelScope
import com.squareup.anvil.compiler.internal.testing.ComponentProcessingMode
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.junit.Test

@OptIn(ExperimentalCompilerApi::class)
internal class CodegenTest {

    @Test
    fun contributesFragment() {
        compileWhetstone(
            """
                package foo

                import com.deliveryhero.whetstone.fragment.ContributesFragment
                import androidx.fragment.app.Fragment

                @ContributesFragment
                class MyFragment : Fragment()
            """.trimIndent(),
            generateDaggerFactories = true,
        ) {
            validateInstanceBinding("foo.MyFragment", Fragment::class, FragmentScope::class)
            validateLazyBindingKey("foo.MyFragment")
        }
    }

    @Test
    fun contributesViewModel() {
        compileWhetstone(
            """
                package foo

                import com.deliveryhero.whetstone.viewmodel.ContributesViewModel
                import androidx.lifecycle.ViewModel

                @ContributesViewModel
                class MyViewModel : ViewModel()
            """.trimIndent(),
            generateDaggerFactories = true,
        ) {
            validateInstanceBinding("foo.MyViewModel", ViewModel::class, ViewModelScope::class)
            validateLazyBindingKey("foo.MyViewModel")
        }
    }

    @Test
    fun contributesViewModel_noLazyBinding() {
        compileWhetstone(
            """
                package foo

                import com.deliveryhero.whetstone.viewmodel.ContributesViewModel
                import androidx.lifecycle.ViewModel

                @ContributesViewModel
                class MyViewModel : ViewModel()
            """.trimIndent(),
            generateDaggerFactories = false,
        ) {
            validateInstanceBinding("foo.MyViewModel", ViewModel::class, ViewModelScope::class)
            validateNoLazyBindingKey("foo.MyViewModel")
        }
    }

    @Test
    fun contributesInjector() {
        compileWhetstone(
            """
                package foo

                import com.deliveryhero.whetstone.injector.ContributesInjector
                import com.deliveryhero.whetstone.activity.ActivityScope
                import android.app.Activity

                @ContributesInjector(ActivityScope::class)
                class MyActivity: Activity()
            """.trimIndent(),
            generateDaggerFactories = true,
        ) {
            validateInjectorBinding("foo.MyActivity", ActivityScope::class)
            validateLazyBindingKey("foo.MyActivity")
        }
    }

    @Test
    fun contributesActivityInjector() {
        compileWhetstone(
            """
                package foo

                import com.deliveryhero.whetstone.activity.ContributesActivityInjector
                import android.app.Activity

                @ContributesActivityInjector
                class MyActivity: Activity()
            """.trimIndent(),
            generateDaggerFactories = true,
        ) {
            validateInjectorBinding("foo.MyActivity", ActivityScope::class)
            validateLazyBindingKey("foo.MyActivity")
        }
    }

    @Test
    fun contributesAppInjector() {
        compileWhetstone(
            """
                package foo

                import com.deliveryhero.whetstone.app.ContributesAppInjector
                import android.app.Application

                @ContributesAppInjector(generateAppComponent = false)
                class MyApplication: Application()
            """.trimIndent(),
            generateDaggerFactories = true,
        ) {
            validateInjectorBinding("foo.MyApplication", ApplicationScope::class)
            validateLazyBindingKey("foo.MyApplication")
        }
    }

    @Test
    fun contributesAppInjector_generatesAppComponent() {
        compileWhetstone(
            """
                package foo

                import com.deliveryhero.whetstone.app.ContributesAppInjector
                import android.app.Application

                @ContributesAppInjector(generateAppComponent = true)
                class MyApplication: Application()
            """.trimIndent(),
            // App modules never set generateDaggerFactories=true (see WhetstonePlugin.configureAnvil):
            // Dagger's own processor (enabled here via componentProcessingMode=KSP) generates the
            // component/factories instead, and Anvil rejects combining both on the same module.
            generateDaggerFactories = false,
            componentProcessingMode = ComponentProcessingMode.KSP,
        ) {
            validateAppComponent(packageName = "foo")
        }
    }
}
