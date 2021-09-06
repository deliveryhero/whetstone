package com.deliveryhero.whetstone.viewmodel

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
 *     @ViewModelKey(MyViewModel::class)
 *     fun binds(target: MyViewModel): ViewModel
 * }
 * ```
 */
public annotation class ContributesViewModel
