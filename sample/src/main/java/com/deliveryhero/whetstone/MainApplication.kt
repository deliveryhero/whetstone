package com.deliveryhero.whetstone

import android.app.Application
import com.deliveryhero.injection.component.ApplicationComponent
import com.deliveryhero.injection.component.ApplicationComponentProvider

public class MainApplication : Application(), ApplicationComponentProvider {

    private val applicationComponent = DaggerGeneratedApplicationComponent.factory().create(this)

    override fun getApplicationComponent(): ApplicationComponent = applicationComponent
}