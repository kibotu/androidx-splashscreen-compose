package net.kibotu.splashscreen

import android.app.Activity
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.time.Duration.Companion.milliseconds

@RunWith(AndroidJUnit4::class)
class SplashScreenComposeTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun testComposeLifecycle() {
        var composableCreated = false
        var composableDisposed = false

        composeTestRule.runOnUiThread {
            composeTestRule.activity.splash {
                content {
                    DisposableEffect(Unit) {
                        composableCreated = true
                        onDispose {
                            composableDisposed = true
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("splash_box"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Lifecycle Test", modifier = Modifier.testTag("splash_text"))
                    }
                }
            }
        }

        // Verify initial state
        assertTrue("Composable should be created", composableCreated)
        assertFalse("Composable should not be disposed yet", composableDisposed)

        // Trigger disposal
        composeTestRule.runOnUiThread {
            composeTestRule.activity.finish()
        }

        // Wait for disposal
        composeTestRule.waitUntil(timeoutMillis = 1000) {
            composableDisposed
        }

        assertTrue("Composable should be disposed", composableDisposed)
    }

    @Test
    fun testComposeStateManagement() {
        var splashScreen: SplashScreenDecorator? = null
        var stateUpdates = 0

        composeTestRule.runOnUiThread {
            splashScreen = composeTestRule.activity.splash {
                content {
                    val visible by remember { isVisible }
                    
                    LaunchedEffect(visible) {
                        stateUpdates++
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("state_box"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            if (visible) "Visible" else "Hidden",
                            modifier = Modifier.testTag("state_text")
                        )
                    }
                }
            }
        }

        // Initial state
        composeTestRule.onNodeWithTag("state_text")
            .assertExists()
            .assertTextEquals("Visible")
        assertEquals(1, stateUpdates)

        // Trigger state change
        composeTestRule.runOnUiThread {
            splashScreen!!.dismiss()
        }

        // Verify state update
        composeTestRule.waitUntil(timeoutMillis = 1000) {
            stateUpdates > 1
        }
        
        composeTestRule.onNodeWithTag("state_text")
            .assertExists()
            .assertTextEquals("Hidden")
    }

    @Test
    fun testCompositionStrategy() {
        var compositionCount = 0

        composeTestRule.runOnUiThread {
            composeTestRule.activity.splash {
                content {
                    SideEffect {
                        compositionCount++
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("composition_box"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Composition Test", modifier = Modifier.testTag("composition_text"))
                    }
                }
            }
        }

        // Verify initial composition
        assertTrue("Should have at least one composition", compositionCount > 0)
        
        // Force recomposition
        composeTestRule.runOnUiThread {
            composeTestRule.activity.recreate()
        }

        // Verify composition behavior
        composeTestRule.waitUntil(timeoutMillis = 1000) {
            compositionCount > 1
        }
    }

    @Test
    fun testNestedCompose() {
        var outerCompositions = 0
        var innerCompositions = 0

        composeTestRule.runOnUiThread {
            composeTestRule.activity.splash {
                content {
                    SideEffect {
                        outerCompositions++
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("outer_box"),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier.testTag("inner_box")
                        ) {
                            SideEffect {
                                innerCompositions++
                            }
                            Text("Nested Content", modifier = Modifier.testTag("nested_text"))
                        }
                    }
                }
            }
        }

        // Verify composition counts
        assertTrue("Outer composition should occur", outerCompositions > 0)
        assertTrue("Inner composition should occur", innerCompositions > 0)

        // Verify nested content
        composeTestRule.onNodeWithTag("nested_text")
            .assertExists()
            .assertTextEquals("Nested Content")
    }

    @Test
    fun testComposeRecomposition() {
        var recompositionCount = 0
        var splashScreen: SplashScreenDecorator? = null

        composeTestRule.runOnUiThread {
            splashScreen = composeTestRule.activity.splash {
                content {
                    val visible by remember { isVisible }
                    
                    SideEffect {
                        recompositionCount++
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("recomposition_box"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Count: $recompositionCount",
                            modifier = Modifier.testTag("recomposition_text")
                        )
                    }

                    // Trigger exit animation when visibility changes
                    LaunchedEffect(visible) {
                        if (!visible) {
                            startExitAnimation()
                        }
                    }
                }
            }
        }

        // Initial composition
        val initialCount = recompositionCount
        assertTrue("Should have initial composition", initialCount > 0)

        // Trigger recomposition
        composeTestRule.runOnUiThread {
            splashScreen!!.dismiss()
        }

        // Verify recomposition occurred
        composeTestRule.waitUntil(timeoutMillis = 1000) {
            recompositionCount > initialCount
        }
    }

    @Test
    fun testComposeViewDisposal() {
        var disposalCount = 0
        var splashScreen: SplashScreenDecorator? = null

        composeTestRule.runOnUiThread {
            splashScreen = composeTestRule.activity.splash {
                content {
                    DisposableEffect(Unit) {
                        onDispose {
                            disposalCount++
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("disposal_box"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Disposal Test", modifier = Modifier.testTag("disposal_text"))
                    }
                }
            }
        }

        // Initial state
        assertEquals(0, disposalCount)

        // Trigger disposal
        composeTestRule.runOnUiThread {
            splashScreen!!.shouldKeepOnScreen = false
            splashScreen!!.dismiss()
        }

        // Verify disposal
        composeTestRule.waitUntil(timeoutMillis = 1000) {
            disposalCount > 0
        }

        assertEquals(1, disposalCount)
    }
}
