package com.deliveryhero.whetstone.codegen

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import com.deliveryhero.whetstone.scope.ActivityScope
import com.deliveryhero.whetstone.scope.FragmentScope
import com.deliveryhero.whetstone.scope.ViewModelScope
import com.squareup.anvil.compiler.internal.testing.compileAnvil
import org.junit.Test

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
            validateInstanceBinding("MyFragment", Fragment::class, FragmentScope::class)
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
            validateInstanceBinding("MyViewModel", ViewModel::class, ViewModelScope::class)
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
            validateInjectorBinding("MyActivity", ActivityScope::class)
        }
    }
}
