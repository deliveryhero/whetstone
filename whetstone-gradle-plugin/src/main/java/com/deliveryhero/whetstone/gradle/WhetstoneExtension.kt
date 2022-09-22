package com.deliveryhero.whetstone.gradle

import org.gradle.api.Action
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.newInstance
import org.gradle.kotlin.dsl.property
import javax.inject.Inject

public abstract class WhetstoneExtension @Inject constructor(objects: ObjectFactory) {
    /**
     * Turns on Factory classes generation via Anvil, that would usually be done with Dagger's
     * annotation processor for @Provides methods, @Inject constructors and @Inject fields.
     *
     * The benefit of this feature is that you don't need to enable the Dagger annotation processor
     * in this module. That often means you can skip KAPT and the stub generating task. In addition
     * Anvil generates Kotlin instead of Java code, which allows Gradle to skip the Java compilation
     * task. The result is faster builds.
     *
     * This feature can only be enabled in Gradle modules that don't compile any Dagger component.
     * Since we only processes Kotlin code, you shouldn't enable it in modules with mixed Kotlin /
     * Java sources either.
     *
     * By default this feature is disabled for application modules, and enabled for others
     * Turning this feature on in application modules should be avoided since it will conflict with
     * Dagger's own codegen. For non-application modules however, this feature allows you to completely
     * skip Dagger/KAPT
     */
    public abstract val generateDaggerFactories: Property<Boolean>

    /**
     * Adds the generated source directories to sourceSets in Gradle for indexing visibility in the
     * IDE. This feature is enabled by default for application modules, and disabled for others.
     * This feature is enabled in application modules to allow referencing the generated application
     * component. However, if the auto-generation of app component is not desirable, then the feature
     * can be safely disabled
     */
    public abstract val syncGeneratedSources: Property<Boolean>

    /**
     * Allows configuring extra Whetstone add-ons.
     *
     * Currently, this only includes turning on/off Jetpack Compose and/or Workmanager support.
     */
    public val addOns: AddOnsHandler = objects.newInstance()

    /**
     * DSL function to help configure extra Whetstone add-ons
     */
    public fun addOns(action: Action<AddOnsHandler>): Unit = action.execute(addOns)
}

public abstract class AddOnsHandler @Inject constructor(objects: ObjectFactory) {
    /**
     * Turns on/off Whetstone's Jetpack Compose integration.
     *
     * When enabled, `whetstone-compose` will be automatically added to the project's dependencies.
     */
    public val compose: Property<Boolean> = objects.property<Boolean>().convention(false)

    /**
     * Turns on/off Whetstone's work manager integration.
     *
     * When enabled, `whetstone-worker` will be automatically added to the project's dependencies.
     */
    public val workManager: Property<Boolean> = objects.property<Boolean>().convention(false)
}
