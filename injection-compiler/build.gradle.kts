plugins {
    id("org.jetbrains.kotlin.jvm")
    id("org.jetbrains.kotlin.kapt")
}

kotlin {
    explicitApi()
    sourceSets.all {
        languageSettings {
            useExperimentalAnnotation("com.squareup.anvil.annotations.ExperimentalAnvilApi")
        }
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
