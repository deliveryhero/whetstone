plugins {
    alias(libs.plugins.androidLib)
    alias(libs.plugins.kotlinAndroid)
    id("com.deliveryhero.whetstone.build")
    id("com.deliveryhero.whetstone")
}

whetstone {
    generateDaggerFactories = true
    syncGeneratedSources = false
}

android {
    namespace = "com.deliveryhero.whetstone.sample.library"
    compileSdk = 35

    defaultConfig {
        minSdk = 21
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    val dimensionName = "di_dimension"
    flavorDimensions += dimensionName
    productFlavors {
        register("hilt") {
            dimension = dimensionName
        }
        register("metro") {
            dimension = dimensionName
        }
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
    }
}

dependencies {

    implementation(libs.androidxCore)
    implementation(libs.androidxAppCompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
