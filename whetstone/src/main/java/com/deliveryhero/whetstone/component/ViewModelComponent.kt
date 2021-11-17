package com.deliveryhero.whetstone.component

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.deliveryhero.whetstone.SingleIn
import com.deliveryhero.whetstone.scope.ApplicationScope
import com.deliveryhero.whetstone.scope.ViewModelScope
import com.squareup.anvil.annotations.ContributesSubcomponent
import com.squareup.anvil.annotations.ContributesTo
import dagger.BindsInstance
import javax.inject.Provider

/**
 * A Dagger component that has the lifetime of the [androidx.lifecycle.ViewModel].
 */
@ContributesSubcomponent(
    scope = ViewModelScope::class,
    parentScope = ApplicationScope::class
)
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
        public fun createViewModelFactory(): Factory
    }
}
