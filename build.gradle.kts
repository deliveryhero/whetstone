import com.android.build.gradle.BaseExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    repositories {
        google()
        mavenCentral()
    }

    dependencies {
        classpath(libs.androidGradle)
        classpath(libs.kotlinGradle)
        classpath(libs.anvilGradle)
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}

subprojects {
    afterEvaluate {
        tasks.withType<KotlinCompile> {
            configureKotlinCompile(this)
        }
        extensions.findByType<BaseExtension>()?.apply {
            configureAndroidBaseExtension(this)
        }
    }
}

fun configureKotlinCompile(target: KotlinCompile) = with(target) {
    kotlinOptions {
        freeCompilerArgs = freeCompilerArgs + listOf(
            "-Xexplicit-api=strict",
            "-Xassertions=jvm",
            "-Xopt-in=kotlin.RequiresOptIn",
            "-Xuse-experimental=com.squareup.anvil.annotations.ExperimentalAnvilApi",
        )
    }
}

fun configureAndroidBaseExtension(extension: BaseExtension) = with(extension) {
    compileSdkVersion(31)

    defaultConfig {
        minSdk = 21
        targetSdk = 31
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}
