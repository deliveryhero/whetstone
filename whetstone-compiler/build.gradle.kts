plugins {
    kotlin("jvm")
    kotlin("kapt")
    id("com.vanniktech.maven.publish")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of("11"))
    }
}

dependencies {
    implementation(libs.anvilCompiler)
    implementation(libs.anvilCompilerUtils)
    implementation(libs.anvilAnnotations)
    implementation(libs.dagger)
    implementation(libs.autoServiceAnnotations)
    kapt(libs.autoServiceCompiler)
}
