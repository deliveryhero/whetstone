package com.deliveryhero.injection.fragment

import androidx.fragment.app.Fragment
import javax.inject.Provider

internal typealias FragmentMap = Map<Class<out Fragment>, @JvmSuppressWildcards Provider<Fragment>>
