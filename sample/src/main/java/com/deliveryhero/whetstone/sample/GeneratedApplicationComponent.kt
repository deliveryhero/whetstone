package com.deliveryhero.whetstone.sample

import android.app.Application
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.ViewModel
import com.deliveryhero.whetstone.SingleIn
import com.deliveryhero.whetstone.activity.ActivityComponent
import com.deliveryhero.whetstone.app.ApplicationComponent
import com.deliveryhero.whetstone.app.ApplicationScope
import com.deliveryhero.whetstone.fragment.FragmentComponent
import com.deliveryhero.whetstone.service.ServiceComponent
import com.deliveryhero.whetstone.view.ViewComponent
import com.deliveryhero.whetstone.viewmodel.ViewModelComponent
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import javax.inject.Singleton
import kotlin.reflect.KClass

@DependencyGraph(scope = ApplicationScope::class)
@SingleIn(ApplicationScope::class)
@Singleton
public interface GeneratedApplicationComponent : ApplicationComponent {

    public fun getActivityComponentFactory(): ActivityComponent.Factory
    public fun getFragmentComponentFactory(): FragmentComponent.Factory
    public fun getServiceComponentFactory(): ServiceComponent.Factory
    public fun getViewComponentFactory(): ViewComponent.Factory
    public fun getViewModelComponentFactory(): ViewModelComponent.Factory

    @DependencyGraph.Factory
    public interface GraphFactory : ApplicationComponent.Factory {
        override fun create(@Provides application: Application): GeneratedApplicationComponent
    }
}

@BindingContainer
@ContributesTo(ApplicationScope::class)
public interface SampleAppMetroModule {
    public companion object {
        @Provides
        public fun provideFragmentFactory(
            fragmentComponentFactory: FragmentComponent.Factory
        ): FragmentFactory {
            return com.deliveryhero.whetstone.fragment.MultibindingFragmentFactory(fragmentComponentFactory)
        }

        @Provides
        public fun provideViewModelMap(): Map<KClass<*>, ViewModel> {
            return emptyMap()
        }

        @Provides
        public fun provideFragmentMap(): Map<KClass<*>, Fragment> {
            return emptyMap()
        }
    }
}
