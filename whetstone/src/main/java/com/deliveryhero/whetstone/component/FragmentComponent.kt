package com.deliveryhero.whetstone.component

import com.deliveryhero.whetstone.fragment.FragmentMap

/**
 * A Dagger component that has the lifetime of the [androidx.fragment.app.Fragment].
 */
public interface FragmentComponent {
    public fun getFragmentMap(): FragmentMap
}

/**
 * Interface for creating an [FragmentComponent].
 */
public interface FragmentComponentFactory {
    public fun create(): FragmentComponent
}
