package com.deliveryhero.whetstone.component

import android.view.View
import com.deliveryhero.whetstone.SingleIn
import com.deliveryhero.whetstone.injector.MembersInjectorMap
import com.deliveryhero.whetstone.scope.ActivityScope
import com.deliveryhero.whetstone.scope.ViewScope
import com.squareup.anvil.annotations.ContributesSubcomponent
import com.squareup.anvil.annotations.ContributesTo
import dagger.BindsInstance
import dagger.Module
import dagger.multibindings.Multibinds

/**
 * A Dagger component that has the lifetime of the [android.view.View].
 */
@ContributesSubcomponent(scope = ViewScope::class, parentScope = ActivityScope::class)
@SingleIn(ViewScope::class)
public interface ViewComponent {
    public fun getMembersInjectorMap(): MembersInjectorMap

    /**
     * Interface for creating an [ViewComponent].
     */
    @ContributesSubcomponent.Factory
    public interface Factory {
        public fun create(@BindsInstance view: View): ViewComponent
    }

    @ContributesTo(ActivityScope::class)
    public interface ParentComponent {
        public fun getViewComponentFactory(): Factory
    }
}

@Module
@ContributesTo(ViewScope::class)
public interface ViewModule {

    @Multibinds
    public fun membersInjectors(): MembersInjectorMap
}
