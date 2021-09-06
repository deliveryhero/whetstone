package com.deliveryhero.whetstone.component

import androidx.lifecycle.SavedStateHandle
import com.deliveryhero.whetstone.SingleIn
import com.deliveryhero.whetstone.viewmodel.ViewModelMap
import com.deliveryhero.whetstone.scope.ViewModelScope
import com.squareup.anvil.annotations.MergeSubcomponent
import dagger.BindsInstance

/**
 * A Dagger component that has the lifetime of the [androidx.lifecycle.ViewModel].
 */
@MergeSubcomponent(ViewModelScope::class)
@SingleIn(ViewModelScope::class)
public interface ViewModelComponent {
    public fun getViewModelMap(): ViewModelMap
}

/**
 * Interface for creating an [ViewModelComponent].
 */
public interface ViewModelComponentFactory {
    public fun create(@BindsInstance savedStateHandle: SavedStateHandle): ViewModelComponent
}
