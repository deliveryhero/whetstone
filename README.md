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
class MyApplication : Application() {

    fun onCreate() {
        Whetstone.initialize { DaggerGeneratedApplicationComponent.factory().create(this) }
        super.onCreate()
    }
}
```

After that, you can easily inject any Android class by annotating it with `@ContributesInjector` and calling `Whetstone.inject`.

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