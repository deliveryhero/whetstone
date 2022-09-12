plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("com.squareup.anvil")
}

anvil {
    generateDaggerFactories.set(true)
}

dependencies {
    implementation(projects.whetstone)
    implementation(libs.dagger)
    api(libs.androidxWorkRuntime)

    testImplementation(kotlin("test-junit"))
    testImplementation(testFixtures(libs.anvilCompilerUtils))
    testImplementation(projects.whetstoneCompiler)
}
