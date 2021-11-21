package com.deliveryhero.whetstone

import android.app.Application
import android.util.Log
import com.deliveryhero.whetstone.component.ApplicationComponent
import com.deliveryhero.whetstone.component.ApplicationComponentOwner
import com.deliveryhero.whetstone.injector.ContributesInjector
import com.deliveryhero.whetstone.scope.ApplicationScope
import javax.inject.Inject

@ContributesInjector(ApplicationScope::class)
public class MainApplication : Application(), ApplicationComponentOwner {

    @Inject
    internal lateinit var dependency: MainDependency

    override val applicationComponent: ApplicationComponent =
        DaggerGeneratedApplicationComponent.factory().create(this)

    override fun onCreate() {
        Whetstone.inject(this)
        super.onCreate()
        Log.d("App", dependency.getMessage("Application"))
    }
}
