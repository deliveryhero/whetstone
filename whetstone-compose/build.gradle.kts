plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("com.vanniktech.maven.publish")
}

android {
    buildFeatures.compose = true
    composeOptions.kotlinCompilerExtensionVersion = libs.versions.androidxComposeCompiler.get()
}

dependencies {
    implementation(projects.whetstone)
    implementation(libs.androidxComposeRuntime)
    implementation(libs.androidxLifecycleViewModelCompose)
}
