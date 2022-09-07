package com.deliveryhero.whetstone.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.deliveryhero.whetstone.SingleIn
import com.deliveryhero.whetstone.app.ApplicationScope
import com.squareup.anvil.annotations.ContributesSubcomponent
import com.squareup.anvil.annotations.ContributesTo
import dagger.BindsInstance
import javax.inject.Provider

/**
 * A Dagger component that has the lifetime of the [androidx.lifecycle.ViewModel].
 */
@ContributesSubcomponent(scope = ViewModelScope::class, parentScope = ApplicationScope::class)
@SingleIn(ViewModelScope::class)
public interface ViewModelComponent {
    public fun getViewModelMap(): Map<Class<*>, Provider<ViewModel>>

    /**
     * Interface for creating an [ViewModelComponent].
     */
    @ContributesSubcomponent.Factory
    public interface Factory {
        public fun create(@BindsInstance savedStateHandle: SavedStateHandle): ViewModelComponent
    }

    @ContributesTo(ApplicationScope::class)
    public interface ParentComponent {
        public fun getViewModelComponentFactory(): Factory
    }
}
