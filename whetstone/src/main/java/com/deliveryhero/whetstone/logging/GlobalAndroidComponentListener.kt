package com.deliveryhero.whetstone.logging

import java.lang.IllegalStateException

public object GlobalAndroidComponentListener {
    public var componentInjectionListener: ComponentInjectionListener? = null
        set(value) {
            if (componentInjectionListener != null && value != null) {
                // Consider adding a wrapper around this callback if you need
                // more than callback at same time
                throw IllegalStateException("You are overriding an existing callback")
            }
            field = value
        }
}
