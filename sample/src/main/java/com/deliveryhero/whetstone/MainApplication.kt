package com.deliveryhero.whetstone

import android.app.Application
import android.util.Log
import com.deliveryhero.whetstone.component.ApplicationComponent
import com.deliveryhero.whetstone.component.ApplicationComponentOwner
import javax.inject.Inject

@ContributesAndroidBinding
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
