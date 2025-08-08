package net.kibotu.splashscreen

import androidx.compose.runtime.Composable
import org.junit.Assert.*
import org.junit.Test
import kotlin.time.Duration.Companion.milliseconds

class SplashScreenConfigTest {

    @Test
    fun `test builder creates valid config with default values`() {
        val builder = SplashScreenConfigBuilder().apply {
            content { }
        }
        val config = builder.build()

        assertEquals(600L, config.exitAnimationDuration)
        assertEquals(200L, config.composeViewFadeDurationOffset)
    }

    @Test
    fun `test builder creates valid config with custom values`() {
        val builder = SplashScreenConfigBuilder().apply {
            exitAnimationDuration = 1000L
            composeViewFadeDurationOffset = 500L
            content { }
        }
        val config = builder.build()

        assertEquals(1000L, config.exitAnimationDuration)
        assertEquals(500L, config.composeViewFadeDurationOffset)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `test builder throws when negative exitAnimationDuration`() {
        SplashScreenConfigBuilder().apply {
            exitAnimationDuration = -1L
            content { }
        }.build()
    }

    @Test(expected = IllegalArgumentException::class)
    fun `test builder throws when negative composeViewFadeDurationOffset`() {
        SplashScreenConfigBuilder().apply {
            composeViewFadeDurationOffset = -1L
            content { }
        }.build()
    }

    @Test(expected = IllegalArgumentException::class)
    fun `test builder throws when no content provided`() {
        SplashScreenConfigBuilder().build()
    }

    @Test
    fun `test builder accepts milliseconds duration`() {
        val builder = SplashScreenConfigBuilder().apply {
            exitAnimationDuration = 1000.milliseconds.inWholeMilliseconds
            composeViewFadeDurationOffset = 500.milliseconds.inWholeMilliseconds
            content { }
        }
        val config = builder.build()

        assertEquals(1000L, config.exitAnimationDuration)
        assertEquals(500L, config.composeViewFadeDurationOffset)
    }

    @Test
    fun `test content is preserved`() {
        @Composable
        fun testContent(controller: SplashScreenController) {
            // Test content
        }

        val builder = SplashScreenConfigBuilder().apply {
            content { testContent(this) }
        }
        val config = builder.build()

        assertNotNull(config.content)
    }
}
