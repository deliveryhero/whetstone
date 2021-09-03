package com.deliveryhero.injection.component

import androidx.lifecycle.SavedStateHandle
import com.deliveryhero.injection.SingleIn
import com.deliveryhero.injection.viewmodel.ViewModelMap
import com.deliveryhero.injection.scope.ViewModelScope
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
