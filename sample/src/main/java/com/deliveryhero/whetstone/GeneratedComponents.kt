package com.deliveryhero.whetstone

import com.deliveryhero.whetstone.component.ActivityComponent
import com.deliveryhero.whetstone.component.ActivityComponentFactory
import com.deliveryhero.whetstone.component.ApplicationComponent
import com.deliveryhero.whetstone.component.ApplicationComponentFactory
import com.deliveryhero.whetstone.component.FragmentComponent
import com.deliveryhero.whetstone.component.FragmentComponentFactory
import com.deliveryhero.whetstone.component.ViewComponent
import com.deliveryhero.whetstone.component.ViewComponentFactory
import com.deliveryhero.whetstone.component.ViewModelComponent
import com.deliveryhero.whetstone.component.ViewModelComponentFactory
import com.deliveryhero.whetstone.scope.ActivityScope
import com.deliveryhero.whetstone.scope.ApplicationScope
import com.deliveryhero.whetstone.scope.FragmentScope
import com.deliveryhero.whetstone.scope.ViewModelScope
import com.deliveryhero.whetstone.scope.ViewScope
import com.squareup.anvil.annotations.ContributesTo
import com.squareup.anvil.annotations.MergeComponent
import com.squareup.anvil.annotations.MergeSubcomponent
import dagger.Binds
import dagger.Component
import dagger.Subcomponent


// DO NOT CHANGE THIS FILE.
//
// We plan to make it to be "auto-generated" during compilation.
// Therefore, please do not modify it manually.

@MergeComponent(ApplicationScope::class)
@SingleIn(ApplicationScope::class)
public interface GeneratedApplicationComponent : ApplicationComponent {

    @Component.Factory
    public interface Factory : ApplicationComponentFactory
}

@MergeSubcomponent(ViewModelScope::class)
@SingleIn(ViewModelScope::class)
public interface GeneratedViewModelComponent : ViewModelComponent {

    @Subcomponent.Factory
    public interface Factory : ViewModelComponentFactory

    @ContributesTo(ApplicationScope::class)
    @dagger.Module(subcomponents = [GeneratedViewModelComponent::class])
    public interface Module {

        @Binds
        public fun bindComponent(target: Factory): ViewModelComponentFactory
    }
}

@MergeSubcomponent(ActivityScope::class)
@SingleIn(ActivityScope::class)
public interface GeneratedActivityComponent : ActivityComponent {

    @Subcomponent.Factory
    public interface Factory : ActivityComponentFactory

    @ContributesTo(ApplicationScope::class)
    @dagger.Module(subcomponents = [GeneratedActivityComponent::class])
    public interface Module {

        @Binds
        public fun bindComponent(target: Factory): ActivityComponentFactory
    }
}

@MergeSubcomponent(ViewScope::class)
@SingleIn(ViewScope::class)
public interface GeneratedViewComponent : ViewComponent {

    @Subcomponent.Factory
    public interface Factory : ViewComponentFactory

    @ContributesTo(ActivityScope::class)
    @dagger.Module(subcomponents = [GeneratedViewComponent::class])
    public interface Module {

        @Binds
        public fun bindComponent(target: Factory): ViewComponentFactory
    }
}

@MergeSubcomponent(FragmentScope::class)
@SingleIn(FragmentScope::class)
public interface GeneratedFragmentComponent : FragmentComponent {

    @Subcomponent.Factory
    public interface Factory : FragmentComponentFactory

    @ContributesTo(ActivityScope::class)
    @dagger.Module(subcomponents = [GeneratedFragmentComponent::class])
    public interface Module {

        @Binds
        public fun bindComponent(target: Factory): FragmentComponentFactory
    }
}
