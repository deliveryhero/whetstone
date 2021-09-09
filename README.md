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

    val applicationComponent by lazy {
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

### Application (TODO)

```kotlin
@ContributesInjector(ApplicationScope::class)
class MyApplication: Application(), ApplicationComponentOwner {

    val applicationComponent by lazy {
        TODO("Create application component.")
    }

    @Inject
    public lateinit var dependency: MyDependency

    fun onCreate() {
        Whetstone.inject(application = this)
        super.onCreate()
    }
}
```

### Service (TODO)

```kotlin
@ContributesInjector(ServiceScope::class)
public class MyService : Service() {

    @Inject
    public lateinit var dependency: MyDependency

    override fun onCreate() {
        Whetstone.inject(service = this)
        super.onCreate()
    }
}
```

### Activity (DONE)

```kotlin
@ContributesInjector(ActivityScope::class)
public class MainActivity : AppCompatActivity() {

    @Inject
    public lateinit var dependency: MyDependency

    override fun onCreate(savedInstanceState: Bundle?) {
        Whetstone.inject(activity = this)
        super.onCreate(savedInstanceState)
    }
}
```

### View (DONE)

```kotlin
@ContributesInjector(ViewScope::class)
public class MyView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : View(context, attrs) {

    @Inject
    public lateinit var dependency: MainDependency

    init {
        if (!isInEditMode) {
            Whetstone.inject(view = this)
        }
    }
}
```

### Worker / WorkManager (TODO)

```kotlin
@ContributesWorker
class UploadWorker @Inject constructor(
    @ForScope(WorkerScope::class) context: Context,
    workerParameters: WorkerParameters,
): Worker(appContext, workerParameters)
```

### Fragments (DONE)

```kotlin
@ContributesFragment
class MyFragment @Inject constructor(
    private val viewModelFactoryProvider: ViewModelFactoryProvider,
): Fragment() {

    // Get the contributed ViewModel
    val viewModel by viewModels<MyViewModel> {
        // We automatically handle process death and saved state handle wiring
        viewModelFactoryProvider.getViewModelFactory(this)
    }
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