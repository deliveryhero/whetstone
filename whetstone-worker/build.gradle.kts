plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("com.deliveryhero.whetstone.build")
    id("dev.zacsweers.anvil")
    id("com.vanniktech.maven.publish")
}

anvil {
    generateDaggerFactories.set(true)
    useKsp(contributesAndFactoryGeneration = true, componentMerging = false)
}

dependencies {
    implementation(projects.whetstone)
    implementation(libs.dagger)
    api(libs.androidxWorkRuntime)

    testImplementation(kotlin("test-junit"))
    testImplementation(testFixtures(libs.anvilCompilerUtils))
    testImplementation(projects.whetstoneCompiler)
}
android {
    namespace = "com.deliveryhero.whetstone.worker"
}
