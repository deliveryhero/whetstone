package com.deliveryhero.whetstone.activity

import android.app.Activity
import androidx.fragment.app.FragmentFactory
import com.deliveryhero.whetstone.SingleIn
import com.deliveryhero.whetstone.app.ApplicationScope
import com.deliveryhero.whetstone.injector.MembersInjectorMap
import com.squareup.anvil.annotations.ContributesTo
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Provides

/**
 * A Metro component that has the lifetime of the [android.app.Activity].
 */
@GraphExtension
public interface ActivityComponent {
    public val fragmentFactory: FragmentFactory
    public val membersInjectorMap: MembersInjectorMap

    /**
     * Interface for creating an [ActivityComponent].
     */
    @GraphExtension.Factory
    public interface Factory {
        public fun create(@Provides activity: Activity): ActivityComponent
    }

    @ContributesTo(ApplicationScope::class)
    public interface ParentComponent {
        public fun getActivityComponentFactory(): Factory
    }
}
