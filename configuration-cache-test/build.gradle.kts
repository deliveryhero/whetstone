plugins {
    id("java")
    kotlin("jvm")
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
