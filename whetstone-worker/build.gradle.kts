plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("com.deliveryhero.whetstone.build")
    id("dev.zacsweers.metro")
    id("com.vanniktech.maven.publish")
}

dependencies {
    implementation(projects.whetstone)
    implementation(libs.metroRuntime)
    api(libs.androidxWorkRuntime)

    testImplementation(kotlin("test-junit"))
    testImplementation(testFixtures(libs.anvilCompilerUtils))
    testImplementation(projects.whetstoneCompiler)
}
android {
    namespace = "com.deliveryhero.whetstone.worker"
}
