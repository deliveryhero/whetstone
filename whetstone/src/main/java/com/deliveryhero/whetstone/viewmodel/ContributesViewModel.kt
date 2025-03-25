package com.deliveryhero.whetstone.viewmodel

import androidx.lifecycle.ViewModel
import com.deliveryhero.whetstone.InternalWhetstoneApi
import com.deliveryhero.whetstone.meta.AutoInstanceBinding

/**
 * Marker annotation signalling that the compiler should generate necessary instance
 * bindings for the annotated view model.
 *
 * For example:
 * Given this annotated view model
 * ```
 * @ContributesViewModel
 * class MyViewModel @Inject constructor() : ViewModel()
 * ```
 * a complementary module will be generated
 * ```
 * @Module
 * @ContributesTo(ViewModelScope::class)
 * interface MyViewModelModule {
 *     @Binds
 *     @IntoMap
 *     @LazyClassKey(MyViewModel::class)
 *     fun binds(target: MyViewModel): ViewModel
 * }
 * ```
 */
@OptIn(InternalWhetstoneApi::class)
@AutoInstanceBinding(base = ViewModel::class, scope = ViewModelScope::class)
public annotation class ContributesViewModel
