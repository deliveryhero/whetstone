package com.deliveryhero.whetstone.viewmodel

import androidx.lifecycle.ViewModel
import javax.inject.Provider

public typealias ViewModelMap = Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>
