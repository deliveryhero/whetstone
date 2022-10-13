plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.deliveryhero.whetstone")
}

whetstone.addOns {
    compose.set(true)
    workManager.set(true)
}

android {
    defaultConfig {
        versionCode = 1
        versionName = "1.0"
        applicationId = "com.deliveryhero.whetstone.sample"
    }
    buildFeatures.compose = true
    buildFeatures.viewBinding = true
    composeOptions.kotlinCompilerExtensionVersion = libs.versions.androidxComposeCompiler.get()
}

dependencies {

    implementation(libs.androidxActivity)
    implementation(libs.androidxCore)
    implementation(libs.androidxAppCompat)
    implementation(libs.androidxComposeMaterial)
    implementation(libs.androidxComposeUi)
    implementation("com.google.android.material:material:1.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
}
