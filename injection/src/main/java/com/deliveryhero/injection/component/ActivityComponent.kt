package com.deliveryhero.injection.component

import android.app.Activity
import androidx.fragment.app.FragmentFactory
import com.deliveryhero.injection.injector.AnvilInjectorMap
import com.deliveryhero.injection.scope.ActivityScope
import com.deliveryhero.injection.viewmodel.ViewModelFactoryProducer
import com.squareup.anvil.annotations.ContributesTo
import dagger.BindsInstance
import dagger.Module
import dagger.multibindings.Multibinds

/**
 * A Dagger component that has the lifetime of the [android.app.Activity].
 */
public interface ActivityComponent {
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
