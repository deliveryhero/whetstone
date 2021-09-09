package com.deliveryhero.whetstone.component

import android.view.View
import com.deliveryhero.whetstone.injector.AnvilInjectorMap
import com.deliveryhero.whetstone.scope.ViewScope
import com.squareup.anvil.annotations.ContributesTo
import dagger.BindsInstance
import dagger.Module
import dagger.multibindings.Multibinds

/**
 * A Dagger component that has the lifetime of the [android.view.View].
 */
public interface ViewComponent {
    public fun getAnvilInjectorMap(): AnvilInjectorMap
}

/**
 * Interface for creating an [ViewComponent].
 */
public interface ViewComponentFactory {
    public fun create(@BindsInstance view: View): ViewComponent
}

@Module
@ContributesTo(ViewScope::class)
public interface ViewModule {

    @Multibinds
    public fun anvilInjectors(): AnvilInjectorMap
}
