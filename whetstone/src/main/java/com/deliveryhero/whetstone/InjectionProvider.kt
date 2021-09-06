package com.deliveryhero.whetstone

import android.app.Activity
import android.app.Application
import android.view.View
import androidx.annotation.IdRes
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.Lifecycle
import com.deliveryhero.injection.R
import com.deliveryhero.whetstone.component.ActivityComponent
import com.deliveryhero.whetstone.component.ApplicationComponent
import com.deliveryhero.whetstone.component.ApplicationComponentProvider
import com.deliveryhero.whetstone.injector.AnvilInjector

/**
 * Static utility methods for dealing with injection in standard Android components.
 */
@Suppress("UNCHECKED_CAST")
public object InjectionProvider {

    public fun <T : Any> fromApplication(application: Application): T {
        require(application is ApplicationComponentProvider) {
            "Application must implement ${ApplicationComponentProvider::class.java.name} to use this Injector"
        }
        return application.getApplicationComponent() as T
    }

    /**
     * Returns the component interface from an [activity].
     */
    public fun <T : Any> fromActivity(activity: Activity): T {
        val contentView = activity.findViewById<View>(android.R.id.content)
        return contentView.getTagOrSet(R.id.activityComponentId) {
            fromApplication<ApplicationComponent>(activity.application)
                .getActivityComponentFactory()
                .create(activity)
        } as T
    }

    /**
     * A helper that let you inject default dependencies into the fields and methods of an [Activity].
     *
     * For example:
     * ```
     * @ContributesInjector(ActivityScope::class)
     * class CustomActivity: AppCompatActivity() {
     *
     *     @Inject lateinit var someDep: SomeDep
     *
     *     override fun onCreate(savedInstanceState: Bundle?) {
     *         Injector.injectActivity(this)
     *         super.onCreate(savedInstanceState)
     *     }
     * }
     * ```
     *
     * It also installs a default [FragmentFactory] if the [activity] is a [FragmentActivity].
     * @see [installFragmentFactory]
     *
     * Activities that use this method must have the [ContributesInjector] annotation,
     * and they must have at least 1 `@Inject` field or method. Otherwise, calling this method
     * will result in an [IllegalStateException]
     */
    public fun injectActivity(activity: Activity) {
        if (activity is FragmentActivity) {
            installFragmentFactory(activity)
        }

        val anvilInjector = fromActivity<ActivityComponent>(activity)
            .getAnvilInjectorMap()
            .getOrElse(activity.javaClass) {
                error("Activity must be annotated with @ContributesInjector and have at least 1 @Inject field/method")
            } as AnvilInjector<Activity>

        anvilInjector.inject(activity)
    }

    /**
     * Installs a default multi-binding [FragmentFactory] into the [activity]'s [FragmentFactory].
     *
     * Once called, the [FragmentFactory] will be used to create new instances from this point onward.
     *
     * **Note**: This method must be invoked before the super [Activity.onCreate] is called.
     * Any invocation thereafter will result in an [IllegalStateException].
     */
    public fun installFragmentFactory(activity: FragmentActivity) {
        check(activity.lifecycle.currentState == Lifecycle.State.INITIALIZED) {
            "installFragmentFactory must be called before activity's super.onCreate."
        }
        val activityComponent = fromActivity<ActivityComponent>(activity)
        activity.supportFragmentManager.fragmentFactory = activityComponent.getFragmentFactory()
    }
}

@Suppress("UNCHECKED_CAST")
private fun <V> View.getTagOrSet(@IdRes key: Int, defaultValue: () -> V): V {
    val value = getTag(key) as? V
    return if (value == null) {
        val answer = defaultValue()
        setTag(key, answer)
        answer
    } else {
        value
    }
}
