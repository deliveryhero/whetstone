plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.vanniktech.maven.publish")
}

android {
    buildFeatures.compose = true
    namespace = "com.deliveryhero.whetstone.compose"
}

dependencies {
    implementation(projects.whetstone)
    implementation(libs.androidxComposeRuntime)
    implementation(libs.androidxLifecycleViewModelCompose)
}
