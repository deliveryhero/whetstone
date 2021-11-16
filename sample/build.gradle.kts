plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.squareup.anvil")
    id("kotlin-kapt")
}

android {
    defaultConfig {
        applicationId = "com.deliveryhero.whetstone.sample"
    }
}

dependencies {
    implementation(libs.dagger)
    kapt(libs.daggerCompiler)
    implementation(project(":whetstone"))
    anvil(project(":whetstone-compiler"))

    implementation(libs.androidxActivity)
    implementation(libs.androidxCore)
    implementation(libs.androidxAppCompat)
    implementation("com.google.android.material:material:1.4.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
}
