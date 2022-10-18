package com.deliveryhero.whetstone.event

import java.lang.IllegalStateException
import kotlin.properties.Delegates

/**
 * This Object has been created to add a listener before and after any field injections in android
 * components(Activity, View, Application, Service) you can use it for any purpose like create trace
 * in your application. For supported one check [com.deliveryhero.whetstone.event.InjectedComponent]
 */
public object GlobalAndroidComponentListener {
    // Consider adding a wrapper around this listener if you need
    // more than one listener at same time
    public var componentInjectionListener: ComponentInjectionListener? by Delegates.observable(null) { _, old, new ->
        if (new != null && old != null) throw IllegalStateException("You are overriding an existing listener")
    }
}
