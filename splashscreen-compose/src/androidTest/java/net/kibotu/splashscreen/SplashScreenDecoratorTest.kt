package net.kibotu.splashscreen

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.time.Duration.Companion.milliseconds

@RunWith(AndroidJUnit4::class)
class SplashScreenDecoratorTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun testSplashScreenCreation() {
        val activity = composeTestRule.activity
        var splashScreen: SplashScreenDecorator? = null

        composeTestRule.runOnUiThread {
            splashScreen = activity.splash {
                content {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Test Splash", modifier = Modifier.testTag("splash_text"))
                    }
                }
            }
        }

        assertNotNull(splashScreen)
        assertTrue(splashScreen!!.isVisible.value)
        assertTrue(splashScreen!!.shouldKeepOnScreen)
    }

    @Test
    fun testSplashScreenDismissal() {
        val activity = composeTestRule.activity
        var splashScreen: SplashScreenDecorator? = null

        composeTestRule.runOnUiThread {
            splashScreen = activity.splash {
                exitAnimationDuration = 100L
                composeViewFadeDurationOffset = 50L
                content {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Test Splash", modifier = Modifier.testTag("splash_text"))
                    }
                }
            }
        }

        // Initial state
        assertTrue(splashScreen!!.isVisible.value)
        assertTrue(splashScreen!!.shouldKeepOnScreen)

        // Trigger dismissal
        composeTestRule.runOnUiThread {
            splashScreen!!.shouldKeepOnScreen = false
            splashScreen!!.dismiss()
        }

        // Verify state changes
        assertFalse(splashScreen!!.isVisible.value)
        assertFalse(splashScreen!!.shouldKeepOnScreen)
    }

    @Test
    fun testCustomAnimationDurations() {
        val activity = composeTestRule.activity
        var splashScreen: SplashScreenDecorator? = null
        val exitDuration = 1500L
        val fadeOffset = 500L

        composeTestRule.runOnUiThread {
            splashScreen = activity.splash {
                exitAnimationDuration = exitDuration
                composeViewFadeDurationOffset = fadeOffset
                content {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Test Splash", modifier = Modifier.testTag("splash_text"))
                    }
                }
            }
        }

        // Verify durations are set correctly
        val config = splashScreen!!::class.java
            .getDeclaredField("config")
            .apply { isAccessible = true }
            .get(splashScreen) as SplashScreenConfig

        assertEquals(exitDuration, config.exitAnimationDuration)
        assertEquals(fadeOffset, config.composeViewFadeDurationOffset)
    }

    @Test
    fun testSplashScreenController() {
        val activity = composeTestRule.activity
        var controllerFromContent: SplashScreenController? = null

        composeTestRule.runOnUiThread {
            activity.splash {
                content {
                    controllerFromContent = this
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Test Splash", modifier = Modifier.testTag("splash_text"))
                    }
                }
            }
        }

        assertNotNull(controllerFromContent)
        assertTrue(controllerFromContent!!.isVisible.value)
    }

    @Test
    fun testComposeContentRendering() {
        val testTag = "splash_text"
        val testText = "Test Splash"

        composeTestRule.runOnUiThread {
            composeTestRule.activity.splash {
                content {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(testText, modifier = Modifier.testTag(testTag))
                    }
                }
            }
        }

        // Verify the Compose content is rendered
        composeTestRule
            .onNodeWithTag(testTag)
            .assertExists()
            .assertTextEquals(testText)
    }

    @Test
    fun testViewHierarchyManipulation() {
        val activity = composeTestRule.activity
        var splashScreen: SplashScreenDecorator? = null

        composeTestRule.runOnUiThread {
            splashScreen = activity.splash {
                content {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Test Splash", modifier = Modifier.testTag("splash_text"))
                    }
                }
            }
        }

        // Get the root view
        val rootView = activity.window.decorView as ViewGroup
        
        // Verify ComposeView is added to the view hierarchy
        var foundComposeView = false
        for (i in 0 until rootView.childCount) {
            if (rootView.getChildAt(i).javaClass.name.contains("ComposeView")) {
                foundComposeView = true
                break
            }
        }
        assertTrue("ComposeView should be added to view hierarchy", foundComposeView)
    }
}
