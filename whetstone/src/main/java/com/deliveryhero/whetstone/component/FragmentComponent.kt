package com.deliveryhero.whetstone.component

import androidx.fragment.app.Fragment
import javax.inject.Provider

/**
 * A Dagger component that has the lifetime of the [androidx.fragment.app.Fragment].
 */
public interface FragmentComponent {
    public fun getFragmentMap(): Map<Class<*>, Provider<Fragment>>
}

/**
 * Interface for creating an [FragmentComponent].
 */
public interface FragmentComponentFactory {
    public fun create(): FragmentComponent
}
