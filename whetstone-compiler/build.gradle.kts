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
    compileOnly(libs.anvilCompiler)
    compileOnly(libs.anvilCompilerUtils)
    compileOnly(libs.anvilAnnotations)
    compileOnly(libs.dagger)
    compileOnly(libs.autoServiceAnnotations)
    kapt(libs.autoServiceCompiler)
}
