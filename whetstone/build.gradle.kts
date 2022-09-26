plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("com.squareup.anvil")
    id("com.vanniktech.maven.publish")
}

anvil {
    generateDaggerFactories.set(true)
}

dependencies {
    api(libs.dagger)

    implementation(libs.androidxLifecycleRuntime)
    implementation(libs.androidxLifecycleProcess)
    implementation(libs.androidxLifecycleViewModel)
    implementation(libs.androidxLifecycleSavedState)

    implementation(libs.androidxCore)
    implementation(libs.androidxAppCompat)
    implementation(libs.androidxActivity)
    implementation(libs.androidxFragment)

    testImplementation(kotlin("test-junit"))
    testImplementation(kotlin("reflect"))
    testImplementation(testFixtures(libs.anvilCompilerUtils))
    testImplementation(projects.whetstoneCompiler)
}
