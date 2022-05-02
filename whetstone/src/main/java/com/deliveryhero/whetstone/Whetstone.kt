package com.deliveryhero.whetstone

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.app.Service
import android.content.ContextWrapper
import android.view.View
import androidx.annotation.IdRes
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.Lifecycle
import com.deliveryhero.whetstone.component.ActivityComponent
import com.deliveryhero.whetstone.component.ServiceComponent
import com.deliveryhero.whetstone.component.ApplicationComponent
import com.deliveryhero.whetstone.component.ApplicationComponentOwner
import com.deliveryhero.whetstone.component.ViewComponent
import com.deliveryhero.whetstone.injector.ContributesInjector
import dagger.MembersInjector
import java.util.concurrent.atomic.AtomicReference

/**
 * Static utility methods for dealing with injection in standard Android components.
 */
@Suppress("UNCHECKED_CAST")
public object Whetstone {

    private val root = AtomicReference<ApplicationComponent>()

    @SuppressLint("NewApi")
    @InternalInjectApi // This method path is not used yet
    public fun initialize(initializer: () -> ApplicationComponent) {
        root.updateAndGet { component -> component ?: initializer() }
    }

    @Suppress("MemberVisibilityCanBePrivate")
    public fun <T : Any> fromApplication(application: Application): T {
        require(application is ApplicationComponentOwner) {
            "Application must implement ${ApplicationComponentOwner::class.java.name} to use this Injector"
        }
        return application.applicationComponent as T
    }

    /**
     * Returns the component interface from an [activity].
     */
    @Suppress("MemberVisibilityCanBePrivate")
    public fun <T : Any> fromActivity(activity: Activity): T {
        val contentView = activity.findViewById<View>(android.R.id.content)
        return contentView.getTagOrSet(R.id.activityComponentId) {
            fromApplication<ActivityComponent.ParentComponent>(activity.application)
                .getActivityComponentFactory()
                .create(activity)
        } as T
    }

    /**
     * Returns the component interface from a [service].
     */

    private fun <T : Any> fromService(service: Service): T {
        return fromApplication<ServiceComponent.ParentComponent>(service.application)
            .getServiceComponentFactory()
            .create(service) as T
    }

    /**
     * A helper that let you inject dependencies into the fields and methods of an [Application].
     *
     * Applications that use this method must have the [ContributesInjector] annotation,
     * and they must have at least 1 `@Inject` field or method. Otherwise, calling this method
     * will result in an [IllegalArgumentException]
     */
    public fun inject(application: Application) {
        val injector = fromApplication<ApplicationComponent>(application)
            .getMembersInjectorMap()[application.javaClass] as? MembersInjector<Application>

        requireNotNull(injector).injectMembers(application)
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
     *         Whetstone.inject(this)
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
    public fun inject(activity: FragmentActivity) {
        installFragmentFactory(activity)

        val injector = fromActivity<ActivityComponent>(activity)
            .getMembersInjectorMap()[activity.javaClass] as? MembersInjector<Activity>

        injector?.injectMembers(activity)
    }

    /**
     * A helper that let you inject default dependencies into the fields and methods of a [Service].
     *
     * For example:
     * ```
     * @ContributesInjector(ServiceScope::class)
     * class CustomService: Service() {
     *
     *     @Inject lateinit var someDep: SomeDep
     *
     *     override fun onCreate() {
     *         Whetstone.inject(this)
     *         super.onCreate()
     *     }
     * }
     * ```
     *
     * Services that use this method must have the [ContributesInjector] annotation,
     * and they must have at least 1 `@Inject` field or method. Otherwise, calling this method
     * will result in an [IllegalStateException]
     */
    public fun inject(service: Service) {
        val injector = fromService<ServiceComponent>(service)
            .getMembersInjectorMap()[service.javaClass] as? MembersInjector<Service>

        requireNotNull(injector).injectMembers(service)
    }

    public fun inject(view: View) {
        val activity = view.findActivity()
        val injector = fromActivity<ViewComponent.ParentComponent>(activity)
            .getViewComponentFactory()
            .create(view)
            .getMembersInjectorMap()[view.javaClass] as? MembersInjector<View>

        requireNotNull(injector).injectMembers(view)
    }

    /**
     * Installs a default multi-binding [FragmentFactory] into the [activity]'s [FragmentFactory].
     *
     * Once called, the [FragmentFactory] will be used to create new instances from this point onward.
     *
     * **Note**: This method must be invoked before the super [Activity.onCreate] is called.
     * Any invocation thereafter will result in an [IllegalStateException].
     */
    private fun installFragmentFactory(activity: FragmentActivity) {
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

private fun View.findActivity(): Activity = requireNotNull(findActivityOrNull())

private fun View.findActivityOrNull(): Activity? {
    var context = context
    while (context is ContextWrapper) {
        if (context is Activity) {
            return context
        }
        context = context.baseContext
    }
    return null
}
