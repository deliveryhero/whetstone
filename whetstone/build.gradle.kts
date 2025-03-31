plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("com.deliveryhero.whetstone.build")
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
    implementation(libs.androidxActivity)
    api(libs.androidxFragment)

    testImplementation(kotlin("test-junit"))
    testImplementation(kotlin("reflect"))
    testImplementation(testFixtures(libs.anvilCompilerUtils))
    testImplementation(projects.whetstoneCompiler)
}
android {
    namespace = "com.deliveryhero.whetstone"
}
