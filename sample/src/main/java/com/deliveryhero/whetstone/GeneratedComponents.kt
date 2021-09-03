package com.deliveryhero.whetstone

import com.deliveryhero.injection.SingleIn
import com.deliveryhero.injection.component.*
import com.deliveryhero.injection.scope.ActivityScope
import com.deliveryhero.injection.scope.ApplicationScope
import com.deliveryhero.injection.scope.FragmentScope
import com.deliveryhero.injection.scope.ViewModelScope
import com.squareup.anvil.annotations.ContributesTo
import com.squareup.anvil.annotations.MergeSubcomponent
import dagger.Binds
import dagger.Subcomponent


// DO NOT CHANGE THIS FILE.
//
// We plan to make it to be "auto-generated" during compilation.
// Therefore, please do not modify it manually.

@MergeSubcomponent(ApplicationScope::class)
@SingleIn(ApplicationScope::class)
public interface GeneratedApplicationComponent : ApplicationComponent {

    @Subcomponent.Factory
    public interface Factory : ApplicationComponentFactory

    @dagger.Module(subcomponents = [GeneratedApplicationComponent::class])
    public interface Module {

        @Binds
        public fun bindComponent(target: Factory): ApplicationComponentFactory
    }
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
