package com.deliveryhero.whetstone.viewmodel

import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.savedstate.SavedStateRegistryOwner
import com.deliveryhero.whetstone.InternalInjectApi
import com.deliveryhero.whetstone.component.ViewModelComponent
import com.deliveryhero.whetstone.scope.ApplicationScope
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

@OptIn(InternalInjectApi::class)
@ContributesBinding(ApplicationScope::class)
public class MultibindingViewModelFactoryProducer @Inject constructor(
    private val viewModelComponentFactory: ViewModelComponent.Factory,
) : ViewModelFactoryProducer {

    override fun createViewModelFactory(
        owner: SavedStateRegistryOwner,
        defaultArgs: Bundle?,
    ): ViewModelProvider.Factory {
        return object : AbstractSavedStateViewModelFactory(owner, defaultArgs) {

            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                key: String,
                modelClass: Class<T>,
                handle: SavedStateHandle
            ): T {
                val viewModelComponent = viewModelComponentFactory.create(handle)
                val viewModelMap = viewModelComponent.getViewModelMap()

                return try {
                    viewModelMap[modelClass]?.get() as T
                } catch (e: Throwable) {
                    error("ViewModel '${modelClass.name}' cannot be instantiated. Did you miss to contribute it? Ensure the ViewModel class is annotated with '${ContributesViewModel::class.java.name}' and has a constructor annotated with '${Inject::class.java.name}'.")
                }
            }
        }
    }
}
