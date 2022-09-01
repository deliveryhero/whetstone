package com.deliveryhero.whetstone.viewmodel

import androidx.lifecycle.ViewModelProvider
import com.deliveryhero.whetstone.scope.ApplicationScope
import com.squareup.anvil.annotations.ContributesBinding
import dagger.Reusable
import javax.inject.Inject

@Reusable
@ContributesBinding(ApplicationScope::class)
@Deprecated("Legacy API. Please use the new injectedViewModel API instead")
public class MultibindingViewModelFactoryProducer @Inject constructor(
    private val viewModelFactory: ViewModelProvider.Factory
) : ViewModelFactoryProducer {

    override fun createViewModelFactory(): ViewModelProvider.Factory {
        return viewModelFactory
    }
}
