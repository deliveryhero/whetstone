package com.deliveryhero.whetstone.logging

import java.lang.IllegalStateException
import kotlin.properties.Delegates

public object GlobalAndroidComponentListener {
    // Consider adding a wrapper around this listener if you need
    // more than one listener at same time
    public var componentInjectionListener: ComponentInjectionListener? by Delegates.observable(null) { _, old, new ->
        if (new != null && old != null) throw IllegalStateException("You are overriding an existing listener")
    }
}
