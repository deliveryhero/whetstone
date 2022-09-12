# Whetstone

> "An Anvil forges a Dagger. A Whetstone sharpens it. And when you're not planning on using your Dagger, you may keep it in something that rhymes with kilt." — [Tiago Cunha](https://github.com/laggedHero).

Whetstone provides a simplified way to incorporate Dagger and Anvil into an Android application.

The goals of Whetstone are:
- To simplify Dagger-related infrastructure for Android apps.
- To create a standard set of components and scopes to ease setup, but allowing customizations.

Why would you use Whetstone instead of Hilt?
- No black-magic. No bytecode manipulation.
- No KAPT.
- Extensible by using Dagger's and Anvil's powers.

## Getting Started

First you must set up Dagger and Anvil. Once that is done, you need to add the following build dependencies to your module `build.gradle` file:

```kotlin
dependencies {
    // ...
    implementation("com.deliveryhero.whetstone:whetstone")
    anvil("com.deliveryhero.whetstone:whetstone-compiler")
}
```

## Basic Usage

To use `Whetstone` you must initialized it in your Application class.

```kotlin
class MyApplication : Application(), ApplicationComponentOwner {

    override val applicationComponent by lazy {
        DaggerGeneratedApplicationComponent.factory().create(this)
    }
}
```

After that, you can easily inject into any Android class (see below).

### Guide

Unlike traditional Dagger, you do not need to define or instantiate Dagger components directly. Instead, we offer predefined components that are generated for you. Whetstone comes with a built-in set of components (and corresponding scope annotations) that are automatically integrated to the Android Framework. As normal, a binding in a child component can have dependencies on any binding in an ancestor component.

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

### Application (DONE)

```kotlin
@ContributesInjector(ApplicationScope::class)
class MyApplication : Application(), ApplicationComponentOwner {

    override val applicationComponent by lazy {
        TODO("Create application component.")
    }

    @Inject
    lateinit var dependency: MyDependency

    fun onCreate() {
        Whetstone.inject(application = this)
        super.onCreate()
    }
}
```

### Activity (DONE)

```kotlin
@ContributesInjector(ActivityScope::class)
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var dependency: MyDependency

    // Get the contributed ViewModel
    // We automatically handle process death and saved state handle wiring
    private val viewModel by injectedViewModel<MyViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        Whetstone.inject(activity = this)
        super.onCreate(savedInstanceState)
    }
}
```

### Fragments (DONE)

```kotlin
@ContributesFragment
class MyFragment @Inject constructor(
    private val dependency: MyDependency,
): Fragment() {

    // Get the contributed ViewModel
    // We automatically handle process death and saved state handle wiring
    private val viewModel by injectedViewModel<MyViewModel>()
}
```
**Important:** A Fragment should **NEVER** be scoped. The Android Framework controls the Lifecycle of **ALL** Fragments.

### ViewModels (DONE)

```kotlin
@ContributesViewModel
class MyViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
): ViewModel()
```

**Important:** A ViewModel should **NEVER** be scoped. The Android Framework controls the Lifecycle of **ALL** ViewModels.

### Service (DONE)
Services should be generally avoided when possible. For most cases, workmanager can be a great alternative and 
is highly recommended. See the workmanager section for more details about how to use it with Whetstone

```kotlin
@ContributesInjector(ServiceScope::class)
class MyService : Service() {

    @Inject
    lateinit var dependency: MyDependency

    override fun onCreate() {
        Whetstone.inject(service = this)
        super.onCreate()
    }
}
```

### View (DONE)
**Disclaimer**: View injection should be avoided by all means. This provision is considered legacy and may be 
completely removed in a later version of Whetstone.

```kotlin
@ContributesInjector(ViewScope::class)
class MyView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : View(context, attrs) {

    @Inject
    lateinit var dependency: MainDependency

    init {
        if (!isInEditMode) {
            Whetstone.inject(view = this)
        }
    }
}
```

### Worker / WorkManager (DONE)
To use Whetstone's work manager integration, a separate dependency is required:

```kotlin
implementation("com.deliveryhero.whetstone:whetstone-worker")
```

This will automatically install Whetstone's worker factory (replacing the default factory), so that you can immediately
start taking advantage of injected workers

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

However, you must make sure to install Whetstone's worker factory before the first call to WorkManager.getInstance
to avoid breaking the integration. Whetstone provides an injectable `WorkerFactory` that can be used to configure
the work manager. For example, you can update your application class to implement work manager's `Configuration.Provider` and supply
Whetstone's `WorkerFactory` to the configuration builder
See the official [documentation](https://developer.android.com/topic/libraries/architecture/workmanager/advanced/custom-configuration) for more details

### Compose (DONE)
To use Whetstone's compose integration, a separate dependency is required

```kotlin
implementation("com.deliveryhero.whetstone:whetstone-compose")
```

Currently, this artefact only exposes APIs for injecting ViewModels that have been contributed to Whetstone

```kotlin
@Composable
fun MyScreen(viewModel: MyViewModel = injectedViewModel()) {
    // injectedViewModel takes care of providing the VM instance directly to this function
}
```

## Creators
- [Marcello Galhardo](http://github.com/marcellogalhardo)
- [Kingsley Adio](https://github.com/kingsleyadio)

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
