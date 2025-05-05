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
    freeCompilerArgs.add("-Xjvm-default=all")
}

dependencies {
    implementation(gradleKotlinDsl())
    compileOnly(libs.kotlinGradle)
    compileOnly(libs.androidGradle)
    implementation(libs.detekt.gradle)
}
