package com.deliveryhero.injection.injector

import dagger.MembersInjector

public interface AnvilInjector<T> {

    public val membersInjector: MembersInjector<T>

    public fun inject(instance: T) {
        membersInjector.injectMembers(instance)
    }
}

internal typealias AnvilInjectorMap = Map<Class<*>, AnvilInjector<*>>
