package com.deliveryhero.whetstone

import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Test
import java.io.File
import java.util.zip.ZipFile
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Functional tests for Whetstone Gradle plugin configuration cache compatibility.
 *
 * These tests verify that the plugin properly supports Gradle's configuration cache,
 * ensuring builds can be cached and reused for improved performance.
 *
 * These tests run against the actual sample-library project in the repository.
 */
class ConfigurationCacheTest {

    private val projectRoot = File(System.getProperty("user.dir")).parentFile

    @Test
    fun `whetstone plugin is configuration cache compatible`() {
        // First build - should store or reuse configuration cache
        // Note: Not using :clean to avoid race conditions with parallel CI tasks
        val firstResult = GradleRunner.create()
            .withProjectDir(projectRoot)
            .withArguments(
                ":sample-library:compileMetroReleaseKotlin",
                "--configuration-cache"
            )
            .forwardOutput()
            .build()

        val firstOutput = firstResult.output
        val hasConfigCache = firstOutput.contains("Configuration cache entry stored") ||
                firstOutput.contains("Reusing configuration cache")

        assertTrue(
            hasConfigCache,
            "First build should either store or reuse configuration cache"
        )

        val firstOutcome = firstResult.task(":sample-library:compileMetroReleaseKotlin")?.outcome
        assertTrue(
            firstOutcome == TaskOutcome.SUCCESS || firstOutcome == TaskOutcome.UP_TO_DATE,
            "Kotlin compilation should succeed or be up-to-date (got: $firstOutcome)"
        )

        // Second build - should reuse configuration cache
        val secondResult = GradleRunner.create()
            .withProjectDir(projectRoot)
            .withArguments(
                ":sample-library:compileMetroReleaseKotlin",
                "--configuration-cache"
            )
            .forwardOutput()
            .build()

        assertTrue(
            secondResult.output.contains("Reusing configuration cache"),
            "Second build should reuse configuration cache"
        )

        val secondOutcome = secondResult.task(":sample-library:compileMetroReleaseKotlin")?.outcome
        assertTrue(
            secondOutcome == TaskOutcome.SUCCESS || secondOutcome == TaskOutcome.UP_TO_DATE,
            "Second build should succeed or be up-to-date (got: $secondOutcome)"
        )
    }

    @Test
    fun `proguard files are packaged in AAR with configuration cache`() {
        // Build AAR with configuration cache
        // Note: Not using :clean to avoid race conditions with parallel CI tasks
        val result = GradleRunner.create()
            .withProjectDir(projectRoot)
            .withArguments(
                ":sample-library:bundleMetroReleaseAar",
                "--configuration-cache"
            )
            .forwardOutput()
            .build()

        val outcome = result.task(":sample-library:bundleMetroReleaseAar")?.outcome
        assertTrue(
            outcome == TaskOutcome.SUCCESS || outcome == TaskOutcome.UP_TO_DATE,
            "AAR bundling should succeed with configuration cache (got: $outcome)"
        )

        // Verify proguard files exist in kotlin-classes directory
        val kotlinClassesProguard = File(
            projectRoot,
            "sample-library/build/tmp/kotlin-classes/metroRelease/META-INF/proguard"
        )

        assertTrue(
            kotlinClassesProguard.exists() && kotlinClassesProguard.isDirectory,
            "META-INF/proguard directory should exist in kotlin-classes output"
        )

        val proguardFiles = kotlinClassesProguard.listFiles { file -> file.extension == "pro" }
        assertTrue(
            proguardFiles != null && proguardFiles.isNotEmpty(),
            "Should have at least one .pro file copied to kotlin-classes"
        )

        // Verify AAR contains proguard.txt
        val aarFile = File(
            projectRoot,
            "sample-library/build/outputs/aar/sample-library-metro-release.aar"
        )

        assertTrue(aarFile.exists(), "AAR file should exist")

        // Verify proguard.txt is in the AAR using ZipFile API for platform independence
        val proguardContent = ZipFile(aarFile).use { zipFile ->
            val entry = zipFile.getEntry("proguard.txt")
            assertTrue(entry != null, "Should be able to extract proguard.txt from AAR")
            zipFile.getInputStream(entry!!).bufferedReader().readText()
        }

        assertTrue(
            proguardContent.contains("-keep"),
            "Proguard file should contain keep rules. Content: $proguardContent"
        )
    }
}
