plugins {
    kotlin("jvm")
    alias(libs.plugins.ksp)
    id("com.deliveryhero.whetstone.build")
    id("com.vanniktech.maven.publish")
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    compileOnly(libs.anvilCompiler)
    compileOnly(libs.anvilAnnotations)
    compileOnly(libs.dagger)
    compileOnly(libs.kspApi)
    implementation(libs.kotlinpoetKsp)
    implementation(libs.autoServiceAnnotations)
    ksp(libs.autoServiceKsp)
}
