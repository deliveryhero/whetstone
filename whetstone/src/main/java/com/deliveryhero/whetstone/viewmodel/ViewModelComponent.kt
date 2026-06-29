package com.deliveryhero.whetstone.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.deliveryhero.whetstone.SingleIn
import com.deliveryhero.whetstone.app.ApplicationScope
import com.squareup.anvil.annotations.ContributesTo
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Multibinds
import dev.zacsweers.metro.Provides
import javax.inject.Provider
import kotlin.reflect.KClass

/**
 * A Metro component that has the lifetime of the [androidx.lifecycle.ViewModel].
 */
@GraphExtension
public interface ViewModelComponent {
    public val viewModelMap: Map<KClass<*>, Provider<ViewModel>>

    @Multibinds(allowEmpty = true)
    public fun provideViewModel(): Map<KClass<*>, ViewModel>

    /**
     * Interface for creating an [ViewModelComponent].
     */
    @GraphExtension.Factory
    public interface Factory {
        public fun create(@Provides savedStateHandle: SavedStateHandle): ViewModelComponent
    }

    @ContributesTo(ApplicationScope::class)
    public interface ParentComponent {
        public fun getViewModelComponentFactory(): Factory
    }
}
