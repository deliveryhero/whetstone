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
                ":sample-library:compileReleaseKotlin",
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

        val firstOutcome = firstResult.task(":sample-library:compileReleaseKotlin")?.outcome
        assertTrue(
            firstOutcome == TaskOutcome.SUCCESS || firstOutcome == TaskOutcome.UP_TO_DATE,
            "Kotlin compilation should succeed or be up-to-date (got: $firstOutcome)"
        )

        // Second build - should reuse configuration cache
        val secondResult = GradleRunner.create()
            .withProjectDir(projectRoot)
            .withArguments(
                ":sample-library:compileReleaseKotlin",
                "--configuration-cache"
            )
            .forwardOutput()
            .build()

        assertTrue(
            secondResult.output.contains("Reusing configuration cache"),
            "Second build should reuse configuration cache"
        )

        val secondOutcome = secondResult.task(":sample-library:compileReleaseKotlin")?.outcome
        assertTrue(
            secondOutcome == TaskOutcome.SUCCESS || secondOutcome == TaskOutcome.UP_TO_DATE,
            "Second build should succeed or be up-to-date (got: $secondOutcome)"
        )
    }
}
