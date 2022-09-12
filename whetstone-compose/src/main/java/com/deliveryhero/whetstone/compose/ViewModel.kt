package com.deliveryhero.whetstone.compose

import androidx.compose.runtime.Composable
import androidx.lifecycle.HasDefaultViewModelProviderFactory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.deliveryhero.whetstone.viewmodel.ContributesViewModel

/**
 * Returns an existing [ViewModelProvider.Factory] or creates a new one.
 *
 * The created [ViewModelProvider.Factory] is provided by Whetstone and can be used to create a instance
 * of any [ViewModel] annotated with [ContributesViewModel]
 */
@Composable
public fun injectedViewModelFactory(): ViewModelProvider.Factory {
    return applicationComponent().viewModelFactory
}

/**
 * Returns an existing [ViewModel] or creates a new one associated with the current view model store owner.
 *
 * The created [ViewModel] is provided by Whetstone and will be retained as long as the [LocalViewModelStoreOwner]
 * is alive (e.g. if it is an activity, until it is finished or process is killed).
 */
@Composable
public inline fun <reified VM : ViewModel> injectedViewModel(
    viewModelStoreOwner: ViewModelStoreOwner = requireNotNull(LocalViewModelStoreOwner.current),
    key: String? = null,
    extras: CreationExtras = (viewModelStoreOwner as? HasDefaultViewModelProviderFactory)
        ?.defaultViewModelCreationExtras
        ?: CreationExtras.Empty
): VM {
    val factory = injectedViewModelFactory()
    return viewModel(viewModelStoreOwner, key, factory, extras)
}
