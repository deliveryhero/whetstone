package com.deliveryhero.injection.component

import android.app.Activity
import android.app.Application
import dagger.BindsInstance

/**
 * A Dagger component that has the lifetime of the [android.app.Application].
 */
public interface ApplicationComponent {
    public fun getActivityComponentFactory(): ActivityComponentFactory
}

/**
 * Interface for creating an [ApplicationComponent].
 */
public interface ApplicationComponentFactory {
    public fun create(@BindsInstance application: Application): ApplicationComponent
}
