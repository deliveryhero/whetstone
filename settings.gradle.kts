enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "pd-whetstone"

pluginManagement {
    repositories {
        mavenLocal()
        gradlePluginPortal()
        google()
        mavenCentral()
    }

    includeBuild("whetstone-gradle-plugin")
    includeBuild("build-logic")
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenLocal()
        google()
        mavenCentral()
    }
}

include(":sample")
include(":whetstone")
include(":whetstone-compiler")
include(":whetstone-compose")
include(":whetstone-worker")
include(":sample-library")
include(":configuration-cache-test")
