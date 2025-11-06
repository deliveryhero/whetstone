package com.deliveryhero.whetstone.codegen

import com.squareup.anvil.compiler.internal.testing.compileAnvil
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.junit.Test
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Integration tests for Whetstone proguard file generation.
 *
 * These tests verify that the Whetstone code generator creates proper proguard files
 * with correct content and location when processing annotated classes.
 */
@OptIn(ExperimentalCompilerApi::class)
internal class ProguardIntegrationTest {

    @Test
    fun `ContributesViewModel generates proguard file with generateDaggerFactories enabled`() {
        compileAnvil(
            """
                package com.test

                import com.deliveryhero.whetstone.viewmodel.ContributesViewModel
                import androidx.lifecycle.ViewModel

                @ContributesViewModel
                class MainViewModel : ViewModel()
            """.trimIndent(),
            generateDaggerFactories = true
        ) {
            // Verify proguard file exists in correct location
            val anvilFolder = File(outputDirectory.parentFile, "build/anvil/META-INF/proguard")
            val proguardFile = File(anvilFolder, "com_test_MainViewModelBindingsModule_LazyClassKeys.pro")

            assertTrue(proguardFile.exists(), "Proguard file should exist at ${proguardFile.absolutePath}")

            // Verify content
            val expectedContent = "-keep,allowobfuscation,allowshrinking class com.test.MainViewModel"
            val actualContent = proguardFile.readText()
            assertEquals(expectedContent, actualContent, "Proguard content should match expected format")
        }
    }

    @Test
    fun `ContributesViewModel does not generate proguard file with generateDaggerFactories disabled`() {
        compileAnvil(
            """
                package com.test

                import com.deliveryhero.whetstone.viewmodel.ContributesViewModel
                import androidx.lifecycle.ViewModel

                @ContributesViewModel
                class MainViewModel : ViewModel()
            """.trimIndent(),
            generateDaggerFactories = false
        ) {
            // Verify no proguard file is generated when generateDaggerFactories is false
            val anvilFolder = File(outputDirectory.parentFile, "build/anvil/META-INF/proguard")
            val proguardFile = File(anvilFolder, "com_test_MainViewModelBindingsModule_LazyClassKeys.pro")

            assertFalse(proguardFile.exists(), "Proguard file should not exist when generateDaggerFactories is false")
        }
    }

    @Test
    fun `multiple ViewModels generate separate proguard files`() {
        compileAnvil(
            """
                package com.test

                import com.deliveryhero.whetstone.viewmodel.ContributesViewModel
                import androidx.lifecycle.ViewModel

                @ContributesViewModel
                class FirstViewModel : ViewModel()

                @ContributesViewModel
                class SecondViewModel : ViewModel()

                @ContributesViewModel
                class ThirdViewModel : ViewModel()
            """.trimIndent(),
            generateDaggerFactories = true
        ) {
            val anvilFolder = File(outputDirectory.parentFile, "build/anvil/META-INF/proguard")

            // Verify all three proguard files exist
            val firstProFile = File(anvilFolder, "com_test_FirstViewModelBindingsModule_LazyClassKeys.pro")
            val secondProFile = File(anvilFolder, "com_test_SecondViewModelBindingsModule_LazyClassKeys.pro")
            val thirdProFile = File(anvilFolder, "com_test_ThirdViewModelBindingsModule_LazyClassKeys.pro")

            assertTrue(firstProFile.exists(), "FirstViewModel proguard file should exist")
            assertTrue(secondProFile.exists(), "SecondViewModel proguard file should exist")
            assertTrue(thirdProFile.exists(), "ThirdViewModel proguard file should exist")

            // Verify content of each file
            assertEquals("-keep,allowobfuscation,allowshrinking class com.test.FirstViewModel", firstProFile.readText())
            assertEquals("-keep,allowobfuscation,allowshrinking class com.test.SecondViewModel", secondProFile.readText())
            assertEquals("-keep,allowobfuscation,allowshrinking class com.test.ThirdViewModel", thirdProFile.readText())
        }
    }

    @Test
    fun `ContributesFragment generates proguard file`() {
        compileAnvil(
            """
                package com.test

                import com.deliveryhero.whetstone.fragment.ContributesFragment
                import androidx.fragment.app.Fragment

                @ContributesFragment
                class MainFragment : Fragment()
            """.trimIndent(),
            generateDaggerFactories = true
        ) {
            val anvilFolder = File(outputDirectory.parentFile, "build/anvil/META-INF/proguard")
            val proguardFile = File(anvilFolder, "com_test_MainFragmentBindingsModule_LazyClassKeys.pro")

            assertTrue(proguardFile.exists(), "Fragment proguard file should exist")

            val expectedContent = "-keep,allowobfuscation,allowshrinking class com.test.MainFragment"
            assertEquals(expectedContent, proguardFile.readText())
        }
    }

    @Test
    fun `ContributesActivityInjector generates proguard file`() {
        compileAnvil(
            """
                package com.test

                import com.deliveryhero.whetstone.activity.ContributesActivityInjector
                import android.app.Activity

                @ContributesActivityInjector
                class MainActivity : Activity()
            """.trimIndent(),
            generateDaggerFactories = true
        ) {
            val anvilFolder = File(outputDirectory.parentFile, "build/anvil/META-INF/proguard")
            val proguardFile = File(anvilFolder, "com_test_MainActivityBindingsModule_LazyClassKeys.pro")

            assertTrue(proguardFile.exists(), "Activity proguard file should exist")

            val expectedContent = "-keep,allowobfuscation,allowshrinking class com.test.MainActivity"
            assertEquals(expectedContent, proguardFile.readText())
        }
    }

    @Test
    fun `proguard file handles qualified package names correctly`() {
        compileAnvil(
            """
                package com.example.app.feature.auth.viewmodel

                import com.deliveryhero.whetstone.viewmodel.ContributesViewModel
                import androidx.lifecycle.ViewModel

                @ContributesViewModel
                class LoginViewModel : ViewModel()
            """.trimIndent(),
            generateDaggerFactories = true
        ) {
            val anvilFolder = File(outputDirectory.parentFile, "build/anvil/META-INF/proguard")
            val proguardFile = File(
                anvilFolder,
                "com_example_app_feature_auth_viewmodel_LoginViewModelBindingsModule_LazyClassKeys.pro"
            )

            assertTrue(proguardFile.exists(), "Proguard file with qualified package should exist")

            val expectedContent = "-keep,allowobfuscation,allowshrinking class com.example.app.feature.auth.viewmodel.LoginViewModel"
            assertEquals(expectedContent, proguardFile.readText())
        }
    }

    @Test
    fun `mixed annotations generate multiple proguard files`() {
        compileAnvil(
            """
                package com.test

                import com.deliveryhero.whetstone.viewmodel.ContributesViewModel
                import com.deliveryhero.whetstone.fragment.ContributesFragment
                import androidx.lifecycle.ViewModel
                import androidx.fragment.app.Fragment

                @ContributesViewModel
                class MyViewModel : ViewModel()

                @ContributesFragment
                class MyFragment : Fragment()
            """.trimIndent(),
            generateDaggerFactories = true
        ) {
            val anvilFolder = File(outputDirectory.parentFile, "build/anvil/META-INF/proguard")

            val vmProFile = File(anvilFolder, "com_test_MyViewModelBindingsModule_LazyClassKeys.pro")
            val fragmentProFile = File(anvilFolder, "com_test_MyFragmentBindingsModule_LazyClassKeys.pro")

            assertTrue(vmProFile.exists(), "ViewModel proguard file should exist")
            assertTrue(fragmentProFile.exists(), "Fragment proguard file should exist")

            assertEquals("-keep,allowobfuscation,allowshrinking class com.test.MyViewModel", vmProFile.readText())
            assertEquals("-keep,allowobfuscation,allowshrinking class com.test.MyFragment", fragmentProFile.readText())
        }
    }

    @Test
    fun `proguard files are placed in correct directory structure`() {
        compileAnvil(
            """
                package com.test

                import com.deliveryhero.whetstone.viewmodel.ContributesViewModel
                import androidx.lifecycle.ViewModel

                @ContributesViewModel
                class TestViewModel : ViewModel()
            """.trimIndent(),
            generateDaggerFactories = true
        ) {
            // Verify the directory structure
            val buildDir = File(outputDirectory.parentFile, "build")
            assertTrue(buildDir.exists(), "build directory should exist")

            val anvilDir = File(buildDir, "anvil")
            assertTrue(anvilDir.exists(), "anvil directory should exist")

            val metaInfDir = File(anvilDir, "META-INF")
            assertTrue(metaInfDir.exists(), "META-INF directory should exist")

            val proguardDir = File(metaInfDir, "proguard")
            assertTrue(proguardDir.exists(), "proguard directory should exist")
            assertTrue(proguardDir.isDirectory, "proguard should be a directory")

            // Verify the file is in this directory
            val proguardFiles = proguardDir.listFiles { file -> file.extension == "pro" } ?: emptyArray()
            assertTrue(proguardFiles.isNotEmpty(), "Should have at least one .pro file")
        }
    }
}
