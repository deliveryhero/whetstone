@file:JvmName("ViewModelSupport")

package com.deliveryhero.whetstone.viewmodel

import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.CreationExtras
import com.deliveryhero.whetstone.Whetstone
import com.deliveryhero.whetstone.component.ApplicationComponent

/**
 * Returns a [Lazy] delegate to access the ComponentActivity's ViewModel.
 *
 * ```
 * class MyComponentActivity : ComponentActivity() {
 *     val viewModel: MyViewModel by injectedViewModel()
 * }
 * ```
 *
 * This method always uses Whetstone's [ViewModelProvider.Factory] to create a new [ViewModel].
 *
 * This property can be accessed only after the Activity is attached to the Application,
 * and access prior to that will result in IllegalArgumentException.
 */
public inline fun <reified VM : ViewModel> ComponentActivity.injectedViewModel(
    noinline extrasProducer: (() -> CreationExtras)? = null,
): Lazy<VM> = viewModels(extrasProducer) {
    val appComponent = Whetstone.fromApplication<ApplicationComponent>(application)
    appComponent.getViewModelFactory()
}

/**
 * Returns a [Lazy] delegate to access [ViewModel] scoped by **default** to this [Fragment].
 *
 * ```
 * class MyFragment : Fragment() {
 *     val viewModel: MyViewModel by injectedViewModel()
 * }
 * ```
 *
 * This method always uses Whetstone's [ViewModelProvider.Factory] to create a new [ViewModel].
 *
 * Default scope may be overridden with parameter [ownerProducer]:
 * ```
 * class MyFragment : Fragment() {
 *     val viewModel: MyViewModel by injectedViewModel ({requireParentFragment()})
 * }
 * ```
 *
 * This property can be accessed only after this Fragment is attached i.e., after
 * [Fragment.onAttach()], and access prior to that will result in IllegalArgumentException.
 */
public inline fun <reified VM : ViewModel> Fragment.injectedViewModel(
    noinline ownerProducer: () -> ViewModelStoreOwner = { this },
    noinline extrasProducer: (() -> CreationExtras)? = null,
): Lazy<VM> = viewModels(ownerProducer, extrasProducer) {
    val app = requireActivity().application
    val appComponent = Whetstone.fromApplication<ApplicationComponent>(app)
    appComponent.getViewModelFactory()
}
