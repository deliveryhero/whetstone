plugins {
    id("java")
    kotlin("jvm")
}

kotlin.compilerOptions {
    // gradleTestKit() pulls in the kotlin-stdlib bundled with this Gradle
    // distribution (newer than this project's pinned Kotlin compiler, which
    // is capped to stay compatible with Square Anvil).
    freeCompilerArgs.add("-Xskip-metadata-version-check")
}

dependencies {
    testImplementation(kotlin("test"))
    testImplementation(libs.junit)
    testImplementation(gradleTestKit())
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
        showStandardStreams = true
    }
}
