package com.deliveryhero.whetstone.component

import android.app.Activity
import androidx.fragment.app.FragmentFactory
import com.deliveryhero.whetstone.injector.AnvilInjectorMap
import com.deliveryhero.whetstone.scope.ActivityScope
import com.deliveryhero.whetstone.viewmodel.ViewModelFactoryProducer
import com.squareup.anvil.annotations.ContributesTo
import dagger.BindsInstance
import dagger.Module
import dagger.multibindings.Multibinds

/**
 * A Dagger component that has the lifetime of the [android.app.Activity].
 */
public interface ActivityComponent {
    public fun getViewComponentFactory(): ViewComponentFactory
    public fun getViewModelFactoryProducer(): ViewModelFactoryProducer
    public fun getFragmentFactory(): FragmentFactory
    public fun getAnvilInjectorMap(): AnvilInjectorMap
}

/**
 * Interface for creating an [ActivityComponent].
 */
public interface ActivityComponentFactory {
    public fun create(@BindsInstance activity: Activity): ActivityComponent
}

@Module
@ContributesTo(ActivityScope::class)
public interface ActivityModule {

    @Multibinds
    public fun anvilInjectors(): AnvilInjectorMap
}
