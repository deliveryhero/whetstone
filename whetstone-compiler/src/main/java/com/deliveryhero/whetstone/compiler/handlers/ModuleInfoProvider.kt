package com.deliveryhero.whetstone.compiler.handlers

import com.squareup.anvil.compiler.internal.reference.AnnotationReference
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.TypeName
import org.jetbrains.kotlin.name.FqName

/**
 * Provides all the information necessary to generate a bindings module for any class
 * annotated with its [supportedAnnotation].
 *
 * Given a class
 * ```
 * @<supportedAnnotation>
 * class <annotatedClass>
 * ```
 *
 * The structure of the generated module is defined below:
 * ```
 * @Module
 * @ContributesTo(<getScope>)
 * class <annotatedClass>BindingsModule {
 *   @Binds
 *   @IntoMap
 *   @<getMultibindingKey>(<annotatedClass>)
 *   fun binds(target: <getTarget>): <getOutput>
 * }
 * ```
 *
 * Note that this interface itself is not responsible for the actual code generation.
 * See [BindingsModuleHandler] for more details on that logic
 */
internal interface ModuleInfoProvider {
    /**
     * Supplies the [FqName] of the annotation that this [ModuleInfoProvider] is expected to work with.
     *
     * Other methods of this interface will only be called if the [supportedAnnotation] is
     * present in the class being processed.
     */
    val supportedAnnotation: FqName

    /**
     * Supplies the scope [ClassName] to which the generated module will be contributed.
     *
     * The [AnnotationReference] of the [supportedAnnotation] is provided here in case such key
     * can be extracted directly from it.
     */
    fun getScope(annotation: AnnotationReference): ClassName

    /**
     * Supplies the target [TypeName] whose type will be bound to [getOutput]'s result.
     *
     * Note that the result of this method should be assignable to [getOutput] otherwise,
     * the binding is considered invalid and compilation may be aborted.
     */
    fun getTarget(annotatedClass: ClassName): TypeName

    /**
     * Supplies the output [TypeName] that is being bound to, given [getTarget]'s value.
     *
     * @see [getTarget]
     */
    fun getOutput(annotatedClass: ClassName): TypeName
}
