[![Maven Central](https://img.shields.io/maven-central/v/com.deliveryhero.whetstone/whetstone?label=stable)](https://central.sonatype.com/artifact/com.deliveryhero.whetstone/whetstone)
[![Sonatype Snapshot](https://img.shields.io/nexus/s/com.deliveryhero.whetstone/whetstone?server=https%3A%2F%2Foss.sonatype.org&label=snapshot)](https://oss.sonatype.org/content/repositories/snapshots/com/deliveryhero/whetstone/)

# Whetstone

> "An Anvil forges a Dagger. A Whetstone sharpens it. And when you're not planning on using your Dagger, you may keep it in something that rhymes with kilt." — [Tiago Cunha](https://github.com/laggedHero).

Whetstone provides a simplified way to incorporate [Dagger](https://github.com/google/dagger) and [Anvil](https://github.com/square/anvil) into an Android application.

The goals of Whetstone are:

- To simplify Dagger-related infrastructure for Android apps.
- To create a standard set of components and scopes to ease setup, but allowing customizations.

## Why would you use Whetstone instead of Hilt?

- All generated code is in Kotlin, which can have significant benefits in a Kotlin only codebase
- Whetstone avoids KAPT completely for performance reasons by taking advantage of Anvil compiler.
- Whetstone is extensible by using the powers of Dagger and Anvil.
- Whetstone significantly reduces boiler plate.
- Whetstone doesn't do bytecode manipulation for complementing classes. Hilt does.
- Summarily, while philosophies are similar, whetstone is relatively easier to work with ;). 

## Getting Started

First you must apply whetstone plugin in the `build.gradle` file of any module that requires dependency injection:

```kotlin
plugins {
    id("com.deliveryhero.whetstone").version("<latest version>")
}
```

Or you can use the old way to apply a plugin:

```kotlin
// In root build.gradle.kts
buildscript {
  repositories {
    mavenCentral()
  }
  dependencies {
    classpath("com.deliveryhero.whetstone:whetstone-gradle-plugin:${latest_version}")
  }
}

// In individual modules
apply(plugin = "com.deliveryhero.whetstone")
```

This automatically configures Dagger and Anvil, and also adds the necessary whetstone dependencies for you.

### Using Snapshot Builds

Snapshot builds are published automatically from the `main` branch and contain the latest unreleased changes. These builds are useful for testing new features or bug fixes before they are officially released.

To use snapshot builds, add the Sonatype snapshots repository to your project:

```kotlin
// In root build.gradle.kts or settings.gradle.kts
repositories {
    mavenCentral()
    maven {
        url = uri("https://oss.sonatype.org/content/repositories/snapshots/")
    }
}
```

Then use the snapshot version in your plugin dependency:

```kotlin
plugins {
    id("com.deliveryhero.whetstone").version("<version>-SNAPSHOT")
}
```

For example, if the current stable version is `1.1.4`, the snapshot version would be `1.1.5-SNAPSHOT`.

**Note:** Snapshot versions are development builds and may be unstable. Use them at your own risk and only for testing purposes.

## Basic Usage

To use whetstone, you must initialize it in your Application class.

```kotlin
@ContributesAppInjector(generateAppComponent = true)
class MyApplication : Application(), ApplicationComponentOwner {

    override val applicationComponent by lazy {
        GeneratedApplicationComponent.create(this)
    }
}
```

Note: For more sophisticated use cases, the generated app component might not be sufficient for you. In such scenario, you can disable automatic generation of app component, and create your own instead.
An example may look like this:

```kotlin
@Singleton // Optional. Can be omitted if you never use this annotation
@SingleIn(ApplicationScope::class)
@MergeComponent(ApplicationScope::class)
interface MyApplicationComponent : ApplicationComponent {

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance application: Application, // this is necessary for whetstone to set things up properly
            // ...
        ): MyApplicationComponent
    }
}
```

After that, you can easily inject into any Android class (see below).

### Guide

Unlike traditional Dagger, you do not need to define or instantiate Dagger components directly. Instead, we offer predefined components that are generated for you. Whetstone comes with a built-in set of components (and corresponding scope annotations) that are automatically integrated to the Android Framework. As expected, a binding in a child component can have dependencies on any binding in an ancestor component.

### Component Lifecycle

Component lifetimes are generally bounded by the creation and destruction of a corresponding instance of an important event. The table below lists the scope annotation and bounded lifetime for each component.

| Component            | Scope             | Created At                       | Destroyed At            |
| -------------------- | ----------------- | -------------------------------- | ----------------------- |
| ApplicationComponent | @ApplicationScope | Application#onCreate             | Application#onTerminate |
| ActivityComponent    | @ActivityScope    | Activity#onCreate                | Activity#onDestroy      |
| FragmentComponent    | @FragmentScope    | FragmentFactory#instantiate      | Fragment#onDestroy      |
| ViewModelComponent   | @ViewModelScope   | ViewModelProvider.Factory#create | ViewModel#onCleared     |
| ViewComponent        | @ViewScope        | View#init                        | View#finalize           |

![whetstone-scopes](art/whetstone-scopes.png?raw=true)

### Application
Applications support field/method injection with Whetstone. Constructor injection is not supported here because the instantiation of applications is completely managed by the system

```kotlin
@ContributesAppInjector
class MyApplication : Application(), ApplicationComponentOwner {

    override val applicationComponent by lazy {
        TODO("Create application component.")
    }

    @Inject
    lateinit var dependency: MyDependency

    fun onCreate() {
        Whetstone.inject(this)
        super.onCreate()
    }
}
```

### Activity
Similar to applications, activities only support field/method injection

```kotlin
@ContributesActivityInjector
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var dependency: MyDependency

    // Get the contributed ViewModel
    // We automatically handle process death and saved state handle wiring
    private val viewModel by injectedViewModel<MyViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        Whetstone.inject(this)
        super.onCreate(savedInstanceState)
    }
}
```

### Service
Services should be generally avoided when possible. For most cases, work manager can be a great alternative and is highly recommended. See the [workmanager](#workmanager) section for more details about how to use it with Whetstone

```kotlin
@ContributesServiceInjector
class MyService : Service() {

    @Inject
    lateinit var dependency: MyDependency

    override fun onCreate() {
        Whetstone.inject(this)
        super.onCreate()
    }
}
```

### View
**Disclaimer**: View injection should be avoided by all means. This provision is considered legacy and may be completely removed in a later version of Whetstone.

```kotlin
@ContributesViewInjector
class MyView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : View(context, attrs) {

    @Inject
    lateinit var dependency: MainDependency

    init {
        if (!isInEditMode) {
            Whetstone.inject(this)
        }
    }
}
```

### Fragments
Fragments support only construction injection, exclusively. This is possible because we are able to hook into the system to influence exactly how fragments should be created. To achieve this, the activity hosting the fragment must install Whetstone's fragment factory.

```kotlin
class MyActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        Whetstone.inject(this)
        super.onCreate(savedInstanceState)
    }
}
```

Then you're able to use the injected fragments with as many constructor arguments as necessary, as long as all these dependencies can be satisfied by dependency injection.

```kotlin
@ContributesFragment
class MyFragment @Inject constructor(
    private val dependency: MyDependency,
    private val anotherDependency: AnotherDependency,
): Fragment() {

    // Get the contributed ViewModel
    // We automatically handle process death and saved state handle wiring
    private val viewModel by injectedViewModel<MyViewModel>()
    private val activityViewModel by injectedActivityViewModel<ActivityViewModel>()
}
```

Note that all injected fragments must be created via the fragment manager. For example:

```kotlin
val myFragment = fragmentManager.instantiate<MyFragment>()
```

For fragments that don't require any external dependencies, the simple no-arg constructor can still be used, and we gracefully fallback to the default behavior

**Important:** A Fragment should **NEVER** be scoped. The Android Framework controls the Lifecycle of **ALL** Fragments.

### ViewModels
Like fragments, view models also support full constructor injection

```kotlin
@ContributesViewModel
class MyViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
): ViewModel()
```

**Important:** A ViewModel should **NEVER** be scoped. The Android Framework controls the Lifecycle of **ALL** ViewModels.

### WorkManager
Workmanager integration is an extra add-on, and must be enabled explicitly in your `build.gradle` file before use:

```kotlin
whetstone {
    addOns.workManager.set(true)
}
```

This will automatically install Whetstone's worker factory (replacing the default factory), so that you can immediately start taking advantage of injected workers

```kotlin
@ContributesWorker
class UploadWorker @Inject constructor(
    @ForScope(WorkerScope::class) context: Context,
    workerParameters: WorkerParameters,
    private val dependency: MyDependency,
): Worker(appContext, workerParameters)
```

To disable automatic initialization, you can remove the initializer from your AndroidManifest.xml

```xml
<provider
   android:name="androidx.startup.InitializationProvider"
   android:authorities="${applicationId}.androidx-startup"
   android:exported="false"
   tools:node="merge">
   <meta-data
       android:name="com.deliveryhero.whetstone.worker.WhetstoneWorkerInitializer"
       android:value="androidx.startup"
       tools:node="remove" />
</provider>
```

However, you must make sure to install Whetstone's worker factory before the first call to `WorkManager#getInstance` to avoid breaking the integration. Whetstone provides an injectable `WorkerFactory` that can be used to configure the work manager. For example, you can update your application class to implement work manager's `Configuration.Provider` and supply Whetstone's `WorkerFactory` to the configuration builder
See the official [documentation](https://developer.android.com/topic/libraries/architecture/workmanager/advanced/custom-configuration) for more details

### Compose
Compose integration is an extra add-on, and must be enabled explicitly in your `build.gradle` file before use:

```kotlin
whetstone {
    addOns.compose.set(true)
}
```

Currently, this artefact only exposes APIs for injecting ViewModels that have been contributed to Whetstone

```kotlin
@Composable
fun MyScreen(viewModel: MyViewModel = injectedViewModel()) {
    // injectedViewModel takes care of providing the VM instance directly to this function
}
```

## License
```
Copyright 2021 Delivery Hero, GmbH.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
