plugins {
    id("java-gradle-plugin")
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.kotlinSam)
    alias(libs.plugins.detekt)
}

samWithReceiver {
    annotation("org.gradle.api.HasImplicitReceiver")
}

gradlePlugin {
    plugins.register("buildPlugin") {
        id = "com.deliveryhero.whetstone.build"
        implementationClass = "com.deliveryhero.whetstone.build.BuildPlugin"
    }
}

kotlin.compilerOptions {
    optIn.add("kotlin.ExperimentalStdlibApi")
    // gradleKotlinDsl() pulls in the kotlin-stdlib bundled with the Gradle
    // distribution itself (newer than this project's pinned Kotlin compiler,
    // which is capped to stay compatible with Square Anvil). The stdlib ABI
    // used here is unaffected, so skip the forward-metadata-version check.
    freeCompilerArgs.addAll("-Xjvm-default=all", "-Xskip-metadata-version-check")
}

dependencies {
    implementation(gradleKotlinDsl())
    compileOnly(libs.kotlinGradle)
    compileOnly(libs.androidGradle)
    implementation(libs.detekt.gradle)
}
