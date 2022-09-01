package com.deliveryhero.whetstone.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import com.deliveryhero.whetstone.component.ViewModelComponent
import com.deliveryhero.whetstone.scope.ApplicationScope
import com.squareup.anvil.annotations.ContributesBinding
import dagger.Reusable
import javax.inject.Inject

@Reusable
@ContributesBinding(ApplicationScope::class)
public class MultibindingViewModelFactory @Inject constructor(
    private val viewModelComponentFactory: ViewModelComponent.Factory,
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    public override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        val handle = extras.createSavedStateHandle()
        val viewModelComponent = viewModelComponentFactory.create(handle)
        val viewModelMap = viewModelComponent.getViewModelMap()

        val viewModelProvider = viewModelMap.getOrElse(modelClass) {
            error(
                "${modelClass.name} could not be instantiated. Did you forget to contribute it? Ensure the " +
                        "view model class is annotated with '${ContributesViewModel::class.java.name}' " +
                        "and has an '@Inject constructor'"
            )
        }
        return viewModelProvider.get() as T
    }
}
