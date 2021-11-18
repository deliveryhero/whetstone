package com.deliveryhero.whetstone.codegen

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import com.deliveryhero.whetstone.fragment.FragmentKey
import com.deliveryhero.whetstone.scope.ActivityScope
import com.deliveryhero.whetstone.scope.FragmentScope
import com.deliveryhero.whetstone.scope.ViewModelScope
import com.deliveryhero.whetstone.viewmodel.ViewModelKey
import org.junit.Test

internal class CodegenTest {

    @Test
    fun contributesFragment() = compileWhetstone(
        """
            import com.deliveryhero.whetstone.fragment.ContributesFragment
            import androidx.fragment.app.Fragment

            @ContributesFragment
            class MyFragment : Fragment()
        """.trimIndent(),
        """
            import com.deliveryhero.whetstone.ContributesAndroidBinding
            import androidx.fragment.app.Fragment

            @ContributesAndroidBinding
            class MyFragment : Fragment()
        """.trimIndent()
    ) {
        validateInstanceBinding(
            "MyFragment",
            Fragment::class,
            FragmentScope::class,
            FragmentKey::class
        )
    }

    @Test
    fun contributesViewModel() = compileWhetstone(
        """
            import com.deliveryhero.whetstone.viewmodel.ContributesViewModel
            import androidx.lifecycle.ViewModel

            @ContributesViewModel
            class MyViewModel : ViewModel()
        """.trimIndent(),
        """
            import com.deliveryhero.whetstone.ContributesAndroidBinding
            import androidx.lifecycle.ViewModel

            @ContributesAndroidBinding
            class MyViewModel : ViewModel()
        """.trimIndent()
    ) {
        validateInstanceBinding(
            "MyViewModel",
            ViewModel::class,
            ViewModelScope::class,
            ViewModelKey::class
        )
    }

    @Test
    fun contributesInjector() = compileWhetstone(
        """
            import com.deliveryhero.whetstone.injector.ContributesInjector
            import com.deliveryhero.whetstone.scope.ActivityScope
            import android.app.Activity

            @ContributesInjector(ActivityScope::class)
            class MyActivity: Activity()
        """.trimIndent(),
        """
            import com.deliveryhero.whetstone.ContributesAndroidBinding
            import com.deliveryhero.whetstone.scope.ActivityScope
            import android.app.Activity

            @ContributesAndroidBinding
            class MyActivity: Activity()
        """.trimIndent()
    ) {
        validateInjectorBinding("MyActivity", ActivityScope::class)
    }
}
