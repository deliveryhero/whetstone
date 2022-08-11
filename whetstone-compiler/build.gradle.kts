plugins {
    kotlin("jvm")
    kotlin("kapt")
}

dependencies {
    implementation(libs.anvilCompiler)
    implementation(libs.anvilCompilerUtils)
    implementation(libs.anvilAnnotations)
    implementation(libs.dagger)
    implementation(libs.autoServiceAnnotations)
    implementation(libs.daggerSpi)
    kapt(libs.autoServiceCompiler)
}

apply(from = rootProject.file("gradle/release-java.gradle"))
