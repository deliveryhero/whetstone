plugins {
    alias(libs.plugins.androidApp)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.kotlinCompose)
    id("com.deliveryhero.whetstone.build")
    id("com.deliveryhero.whetstone")
}

whetstone {
    generateDaggerFactories = false
    addOns {
        compose.set(true)
        workManager.set(true)
    }
}

android {
    namespace = "com.deliveryhero.whetstone.sample"
    defaultConfig {
        versionCode = 1
        versionName = "1.0"
        applicationId = "com.deliveryhero.whetstone.sample"
        targetSdk = 35
    }

    buildTypes {
        getByName("release") {
            isDefault = true
            isDebuggable = false
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("debug")
            matchingFallbacks += "hilt"
        }
        getByName("debug") {
            matchingFallbacks += "hilt"
        }
    }

    buildFeatures {
        compose = true
        viewBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }

    val dimension = "di_dimension"
    flavorDimensions += dimension
    productFlavors {
        register("hilt") {
            this.dimension = dimension
        }
        register("metro") {
            this.dimension = dimension
        }
    }
}

kapt {
    javacOptions {
        option("-Adagger.fastInit=enabled")
    }
}

dependencies {
    implementation(projects.sampleLibrary)

    implementation(libs.androidxActivity)
    implementation(libs.androidxCore)
    implementation(libs.androidxAppCompat)
    implementation(libs.androidxComposeMaterial)
    implementation(libs.androidxComposeUi)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
