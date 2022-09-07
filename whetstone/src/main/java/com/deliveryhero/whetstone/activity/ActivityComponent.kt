package com.deliveryhero.whetstone.activity

import android.app.Activity
import androidx.fragment.app.FragmentFactory
import com.deliveryhero.whetstone.SingleIn
import com.deliveryhero.whetstone.app.ApplicationScope
import com.deliveryhero.whetstone.injector.MembersInjectorMap
import com.squareup.anvil.annotations.ContributesSubcomponent
import com.squareup.anvil.annotations.ContributesTo
import dagger.BindsInstance
import dagger.Module
import dagger.multibindings.Multibinds

/**
 * A Dagger component that has the lifetime of the [android.app.Activity].
 */
@ContributesSubcomponent(scope = ActivityScope::class, parentScope = ApplicationScope::class)
@SingleIn(ActivityScope::class)
public interface ActivityComponent {
    public fun getFragmentFactory(): FragmentFactory
    public fun getMembersInjectorMap(): MembersInjectorMap

    /**
     * Interface for creating an [ActivityComponent].
     */
    @ContributesSubcomponent.Factory
    public interface Factory {
        public fun create(@BindsInstance activity: Activity): ActivityComponent
    }

    @ContributesTo(ApplicationScope::class)
    public interface ParentComponent {
        public fun getActivityComponentFactory(): Factory
    }
}

@Module
@ContributesTo(ActivityScope::class)
public interface ActivityModule {

    @Multibinds
    public fun membersInjectors(): MembersInjectorMap
}
