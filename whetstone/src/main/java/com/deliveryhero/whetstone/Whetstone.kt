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
import com.deliveryhero.whetstone.activity.ActivityComponent
import com.deliveryhero.whetstone.activity.ContributesActivityInjector
import com.deliveryhero.whetstone.app.ApplicationComponent
import com.deliveryhero.whetstone.app.ApplicationComponentOwner
import com.deliveryhero.whetstone.app.ContributesAppInjector
import com.deliveryhero.whetstone.fragment.ContributesFragment
import com.deliveryhero.whetstone.event.GlobalAndroidComponentListener
import com.deliveryhero.whetstone.event.InjectedComponent
import com.deliveryhero.whetstone.service.ContributesServiceInjector
import com.deliveryhero.whetstone.service.ServiceComponent
import com.deliveryhero.whetstone.view.ViewComponent
import dagger.MembersInjector
import java.util.concurrent.atomic.AtomicReference

/**
 * Static utility methods for dealing with injection in standard Android components.
 */
@Suppress("UNCHECKED_CAST")
public object Whetstone {

    private val root = AtomicReference<ApplicationComponent>()

    @SuppressLint("NewApi")
    @InternalWhetstoneApi // This method path is not used yet
    public fun initialize(initializer: () -> ApplicationComponent) {
        root.updateAndGet { component -> component ?: initializer() }
    }

    /**
     * Retrieves the component interface from an [application].
     */
    @Suppress("MemberVisibilityCanBePrivate")
    public fun <T : Any> fromApplication(application: Application): T {
        require(application is ApplicationComponentOwner) {
            "Application must implement ${ApplicationComponentOwner::class.java.name} to use this Injector"
        }
        return application.applicationComponent as T
    }

    /**
     * Retrieves the component interface from an [activity].
     *
     * If one is not already existing for this activity, a new one will be created and returned
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
     * Injects dependencies into the fields and methods of an [Application].
     *
     * When injecting an application, the injected fields and methods must be annotated with `@Inject`
     * and the application itself must be annotated with `@ContributesAppInjector`
     * Otherwise, calling this method will result in an [IllegalArgumentException]
     * @see [ContributesAppInjector]
     */
    public fun inject(application: Application) {
        GlobalAndroidComponentListener.componentInjectionListener
            ?.onInjectStart(InjectedComponent.Application(application))
        val injector = fromApplication<ApplicationComponent>(application)
            .membersInjectorMap[application.javaClass] as? MembersInjector<Application>

        requireNotNull(injector).injectMembers(application)
        GlobalAndroidComponentListener.componentInjectionListener
            ?.onInjectFinish(InjectedComponent.Application(application))
    }

    /**
     * Injects dependencies into the fields and methods of the given [Activity].
     *
     * For example:
     * ```
     * @ContributesActivityInjector
     * class CustomActivity: Activity() {
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
     * When injecting an activity, the injected fields and methods must be annotated with `@Inject`
     * and the activity itself must be annotated with `@ContributesActivityInjector`
     * Otherwise, those fields will be ignored, which may lead to runtime exception.
     * @see [ContributesActivityInjector]
     */
    public fun inject(activity: Activity) {
        GlobalAndroidComponentListener.componentInjectionListener
            ?.onInjectStart(InjectedComponent.Activity(activity))
        if (activity is FragmentActivity) {
            installFragmentFactory(activity)
        }

        val injector = fromActivity<ActivityComponent>(activity)
            .membersInjectorMap[activity.javaClass] as? MembersInjector<Activity>

        injector?.injectMembers(activity)

        GlobalAndroidComponentListener.componentInjectionListener
            ?.onInjectFinish(InjectedComponent.Activity(activity))
    }

    /**
     * Injects dependencies into the fields and methods of the given [service].
     *
     * For example:
     * ```
     * @ContributesServiceInjector
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
     * When injecting a service, the injected fields and methods must be annotated with `@Inject`
     * and the service itself must be annotated with `@ContributesServiceInjector`
     * Otherwise, calling this method will result in an [IllegalArgumentException]
     * @see [ContributesServiceInjector]
     */
    public fun inject(service: Service) {
        GlobalAndroidComponentListener.componentInjectionListener
            ?.onInjectStart(InjectedComponent.Service(service))
        val app = service.application
        val injector = fromApplication<ServiceComponent.ParentComponent>(app)
            .getServiceComponentFactory()
            .create(service)
            .membersInjectorMap[service.javaClass] as? MembersInjector<Service>

        requireNotNull(injector).injectMembers(service)
        GlobalAndroidComponentListener.componentInjectionListener
            ?.onInjectFinish(InjectedComponent.Service(service))
    }

    public fun inject(view: View) {
        GlobalAndroidComponentListener.componentInjectionListener
            ?.onInjectStart(InjectedComponent.View(view))
        val activity = view.findActivity()
        val injector = fromActivity<ViewComponent.ParentComponent>(activity)
            .getViewComponentFactory()
            .create(view)
            .membersInjectorMap[view.javaClass] as? MembersInjector<View>

        requireNotNull(injector).injectMembers(view)
        GlobalAndroidComponentListener.componentInjectionListener
            ?.onInjectFinish(InjectedComponent.View(view))
    }

    /**
     * Installs Whetstone's multi-binding [FragmentFactory] into the [activity]'s fragment manager.
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
        activity.supportFragmentManager.fragmentFactory = activityComponent.fragmentFactory
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
