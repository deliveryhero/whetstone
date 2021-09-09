package com.deliveryhero.whetstone

import android.app.Application
import com.deliveryhero.whetstone.component.ApplicationComponent
import com.deliveryhero.whetstone.component.ApplicationComponentOwner

public class MainApplication : Application(), ApplicationComponentOwner {

    override val applicationComponent: ApplicationComponent =
        DaggerGeneratedApplicationComponent.factory().create(this)
}
