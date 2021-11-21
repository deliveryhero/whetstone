package com.deliveryhero.whetstone

import kotlin.reflect.KClass

/**
 * Marker annotation signalling that the compiler should generate complementary
 * bindings for the annotated type.
 *
 * The compiler is smart enough to figure out what kind of binding to generate together
 * with all the necessary parameters for a set of known direct supertypes.
 *
 * For simple bindings, the following direct super types are known to the compiler
 * - Fragment (and alternatives: DialogFragment)
 * - ViewModel
 * - ListenableWorker (and alternatives: Worker) // TODO(Kingsley): Support workmanager
 *
 * For cases where we cannot directly provide an instance of a particular type, we can
 * fallback to field and method injection. The following known types qualify for this
 * behavior:
 * - Application
 * - Activity (and alternatives: ComponentActivity, AppCompatActivity, FragmentActivity)
 * - Service (and alternatives: IntentService) // TODO(Kingsley): Support services
 * - View (and alternatives: ViewGroup)
 *
 * To illustrate with an example. Given this annotated fragment
 * ```
 * @ContributesAndroidBinding
 * class MyFragment @Inject constructor() : Fragment()
 * ```
 * a complementary module will be generated
 * ```
 * @Module
 * @ContributesTo(FragmentScope::class)
 * interface MyFragmentModule {
 *     @Binds
 *     @IntoMap
 *     @ClassKey(MyFragment::class)
 *     fun binds(target: MyFragment): Fragment
 * }
 * ```
 * In cases where the direct supertype is not any of the ones mentioned above,
 * the annotation exposes a `scope` that can be used to directly specify the scope
 * into which the binding should be generated, and the compiler can figure out the
 * remaining details.
 *
 * For example:
 * Given this other fragment which extends from a custom BaseFragment
 * ```
 * @ContributesAndroidBinding(FragmentScope::class)
 * class AnotherFragment @Inject constructor() : BaseFragment()
 * ```
 * a similar complementary module will be generated
 * ```
 * @Module
 * @ContributesTo(FragmentScope::class)
 * interface AnotherFragmentModule {
 *     @Binds
 *     @IntoMap
 *     @ClassKey(AnotherFragment::class)
 *     fun binds(target: AnotherFragment): Fragment
 * }
 * ```
 * In an event where neither of these approaches result in a known supertype,
 * the compiler throws an error and execution is aborted
 */
@Target(AnnotationTarget.CLASS)
public annotation class ContributesAndroidBinding(val scope: KClass<*> = Unit::class)
