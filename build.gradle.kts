plugins {
    alias(libs.plugins.kotlinCompose).apply(false)
    alias(libs.plugins.androidApp).apply(false)
    alias(libs.plugins.androidLib).apply(false)
    alias(libs.plugins.kotlinJvm).apply(false)
    alias(libs.plugins.kotlinKapt).apply(false)
    alias(libs.plugins.anvil).apply(false)
    alias(libs.plugins.mavenPublish).apply(false)
    alias(libs.plugins.binaryValidator)
}

apiValidation {
    ignoredProjects.addAll(listOf("sample", "whetstone-compiler"))
}

tasks.register<Delete>("clean") {
    delete(rootProject.layout.buildDirectory)
}
