package com.deliveryhero.whetstone.logging

public interface ComponentInjectionListener {
    public fun onInjectStart(injectedComponent: InjectedComponent)
    public fun onInjectFinish(injectedComponent: InjectedComponent)
}
