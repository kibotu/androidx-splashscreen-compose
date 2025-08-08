package net.kibotu.splashscreen

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
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
class SplashScreenAnimationTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun testAnimationStateTransitions() {
        val activity = composeTestRule.activity
        var splashScreen: SplashScreenDecorator? = null
        var animationCompleted = false

        composeTestRule.runOnUiThread {
            splashScreen = activity.splash {
                exitAnimationDuration = 300L
                composeViewFadeDurationOffset = 100L
                content {
                    val transition = updateTransition(
                        targetState = isVisible.value,
                        label = "splash_transition"
                    )
                    val alpha by transition.animateFloat(label = "alpha") { visible ->
                        if (visible) 1f else 0f
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .alpha(alpha)
                            .testTag("animated_box"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Animated Splash", modifier = Modifier.testTag("splash_text"))
                    }

                    // When animation reaches end state, trigger exit
                    if (!isVisible.value && alpha == 0f) {
                        startExitAnimation()
                        animationCompleted = true
                    }
                }
            }
        }

        // Initial state check
        assertTrue(splashScreen!!.isVisible.value)
        assertFalse(animationCompleted)

        // Trigger animation
        composeTestRule.runOnUiThread {
            splashScreen!!.shouldKeepOnScreen = false
            splashScreen!!.dismiss()
        }

        // Wait for animation
        composeTestRule.waitUntil(timeoutMillis = 1000) {
            animationCompleted
        }

        // Verify final state
        assertFalse(splashScreen!!.isVisible.value)
        assertTrue(animationCompleted)
    }

    @Test
    fun testParallelAnimations() {
        val activity = composeTestRule.activity
        var splashScreen: SplashScreenDecorator? = null
        var systemViewAlpha = 1f
        var composeViewAlpha = 1f

        composeTestRule.runOnUiThread {
            splashScreen = activity.splash {
                exitAnimationDuration = 300L
                composeViewFadeDurationOffset = 100L
                content {
                    val transition = updateTransition(
                        targetState = isVisible.value,
                        label = "splash_transition"
                    )
                    val alpha by transition.animateFloat(label = "alpha") { visible ->
                        if (visible) 1f else 0f
                    }

                    composeViewAlpha = alpha

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .alpha(alpha)
                            .testTag("animated_box"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Animated Splash", modifier = Modifier.testTag("splash_text"))
                    }

                    if (!isVisible.value && alpha == 0f) {
                        startExitAnimation()
                    }
                }
            }
        }

        // Initial state check
        assertEquals(1f, systemViewAlpha, 0.01f)
        assertEquals(1f, composeViewAlpha, 0.01f)

        // Trigger animations
        composeTestRule.runOnUiThread {
            splashScreen!!.shouldKeepOnScreen = false
            splashScreen!!.dismiss()
        }

        // Wait for animations to complete
        composeTestRule.waitUntil(timeoutMillis = 1000) {
            composeViewAlpha == 0f
        }

        // Verify final state
        assertEquals(0f, composeViewAlpha, 0.01f)
    }

    @Test
    fun testAnimationTimings() {
        val activity = composeTestRule.activity
        var splashScreen: SplashScreenDecorator? = null
        val exitDuration = 500L
        val fadeOffset = 200L
        var animationStartTime = 0L
        var animationEndTime = 0L

        composeTestRule.runOnUiThread {
            splashScreen = activity.splash {
                exitAnimationDuration = exitDuration
                composeViewFadeDurationOffset = fadeOffset
                content {
                    val transition = updateTransition(
                        targetState = isVisible.value,
                        label = "splash_transition"
                    )
                    val alpha by transition.animateFloat(label = "alpha") { visible ->
                        if (visible) 1f else 0f
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .alpha(alpha)
                            .testTag("animated_box"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Animated Splash", modifier = Modifier.testTag("splash_text"))
                    }

                    if (!isVisible.value && animationStartTime == 0L) {
                        animationStartTime = System.currentTimeMillis()
                    }

                    if (!isVisible.value && alpha == 0f) {
                        animationEndTime = System.currentTimeMillis()
                        startExitAnimation()
                    }
                }
            }
        }

        // Trigger animation
        composeTestRule.runOnUiThread {
            splashScreen!!.shouldKeepOnScreen = false
            splashScreen!!.dismiss()
        }

        // Wait for animation to complete
        composeTestRule.waitUntil(timeoutMillis = 2000) {
            animationEndTime > 0
        }

        // Verify animation duration is within expected range
        val actualDuration = animationEndTime - animationStartTime
        assertTrue(
            "Animation duration ($actualDuration) should be close to expected duration ($exitDuration)",
            actualDuration >= exitDuration && actualDuration <= exitDuration + fadeOffset + 100 // Allow some buffer
        )
    }

    @Test
    fun testCustomAnimationBehavior() {
        val activity = composeTestRule.activity
        var splashScreen: SplashScreenDecorator? = null
        var customAnimationTriggered = false
        var customAnimationCompleted = false

        composeTestRule.runOnUiThread {
            splashScreen = activity.splash {
                exitAnimationDuration = 300L
                composeViewFadeDurationOffset = 100L
                content {
                    val transition = updateTransition(
                        targetState = isVisible.value,
                        label = "custom_transition"
                    )
                    val scale by transition.animateFloat(label = "scale") { visible ->
                        if (visible) 1f else 0f
                    }
                    val alpha by transition.animateFloat(label = "alpha") { visible ->
                        if (visible) 1f else 0f
                    }

                    if (!isVisible.value && !customAnimationTriggered) {
                        customAnimationTriggered = true
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .alpha(alpha)
                            .testTag("custom_animated_box"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Custom Animation", modifier = Modifier.testTag("custom_text"))
                    }

                    if (!isVisible.value && alpha == 0f && scale == 0f) {
                        customAnimationCompleted = true
                        startExitAnimation()
                    }
                }
            }
        }

        // Initial state
        assertFalse(customAnimationTriggered)
        assertFalse(customAnimationCompleted)

        // Trigger animation
        composeTestRule.runOnUiThread {
            splashScreen!!.shouldKeepOnScreen = false
            splashScreen!!.dismiss()
        }

        // Wait for custom animation to complete
        composeTestRule.waitUntil(timeoutMillis = 1000) {
            customAnimationCompleted
        }

        // Verify animation states
        assertTrue(customAnimationTriggered)
        assertTrue(customAnimationCompleted)
    }
}
