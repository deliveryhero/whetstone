package com.deliveryhero.whetstone.viewmodel

import androidx.lifecycle.ViewModel
import com.deliveryhero.whetstone.InternalWhetstoneApi
import com.deliveryhero.whetstone.meta.ContributesInstanceMeta

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
 *     @ClassKey(MyViewModel::class)
 *     fun binds(target: MyViewModel): ViewModel
 * }
 * ```
 */
@OptIn(InternalWhetstoneApi::class)
@ContributesInstanceMeta(base = ViewModel::class, scope = ViewModelScope::class)
public annotation class ContributesViewModel
