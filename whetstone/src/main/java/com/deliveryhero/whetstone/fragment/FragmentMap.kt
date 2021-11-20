package com.deliveryhero.whetstone.fragment

import androidx.fragment.app.Fragment
import javax.inject.Provider

public typealias FragmentMap = Map<Class<*>, @JvmSuppressWildcards Provider<Fragment>>
