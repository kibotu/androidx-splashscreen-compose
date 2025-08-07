package net.kibotu.splashscreen

import android.animation.ObjectAnimator
import android.app.Activity
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.splashscreen.SplashScreenViewProvider
import kotlin.time.Duration.Companion.milliseconds

/**
 * AndroidX SplashScreen doesn't support Compose content natively.
 * This decorator injects custom Composables into the splash flow because users expect
 * animated branding during app startup, not static images.
 */
class SplashScreenDecorator private constructor(
    activity: Activity,
    private val config: SplashScreenConfig
) {

    companion object {
        /** Factory method because constructors with builders feel awkward. */
        fun create(
            activity: Activity,
            builder: SplashScreenConfigBuilder.() -> Unit
        ): SplashScreenDecorator = SplashScreenDecorator(
            activity,
            SplashScreenConfigBuilder()
                .apply(builder)
                .build()
        )
    }

    /** Composables need to know when to start their exit animations. */
    val isVisible: MutableState<Boolean> = mutableStateOf(true)

    /** AndroidX requires this callback to control dismissal timing. */
    var shouldKeepOnScreen: Boolean = true

    @Suppress("UNUSED")
    private val splashScreen: SplashScreen = activity.installSplashScreen().apply {
        setOnExitAnimationListener(::handleExitAnimation)
        setKeepOnScreenCondition { shouldKeepOnScreen }
    }

    /** System splash ends → inject our Compose content for custom animations. */
    private fun handleExitAnimation(splashScreenViewProvider: SplashScreenViewProvider) {
        val parentViewGroup = splashScreenViewProvider.view.parent as ViewGroup
        val composeView = createComposeView(splashScreenViewProvider, parentViewGroup)
        parentViewGroup.addView(composeView)
    }

    /** Wraps user's Composable in a View because AndroidX works with View hierarchy. */
    private fun createComposeView(
        splashScreenViewProvider: SplashScreenViewProvider,
        parentViewGroup: ViewGroup
    ): ComposeView = ComposeView(splashScreenViewProvider.view.context).apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindow)

        setContent {
            config.content(
                SplashScreenController(
                    decorator = this@SplashScreenDecorator,
                    onStartExitAnimation = {
                        performExitAnimation(
                            systemSplashView = splashScreenViewProvider.view,
                            composeView = this@apply,
                            parentViewGroup = parentViewGroup,
                            splashScreenViewProvider = splashScreenViewProvider
                        )
                    }
                )
            )
        }
    }

    /**
     * Fades both system and custom views simultaneously.
     * Staggered timing prevents jarring visual jumps during transition.
     */
    private fun performExitAnimation(
        systemSplashView: View,
        composeView: ComposeView,
        parentViewGroup: ViewGroup,
        splashScreenViewProvider: SplashScreenViewProvider
    ) {
        val baseDuration = config.exitAnimationDuration

        // Fade out the system splash screen
        ObjectAnimator.ofFloat(systemSplashView, View.ALPHA, 0f).apply {
            duration = baseDuration
            doOnEnd { parentViewGroup.removeView(systemSplashView) }
            start()
        }

        // Fade out the compose view with configurable offset
        ObjectAnimator.ofFloat(composeView, View.ALPHA, 0f).apply {
            duration = baseDuration + config.composeViewFadeDurationOffset
            doOnEnd {
                parentViewGroup.removeView(composeView)
                splashScreenViewProvider.remove()
            }
            start()
        }
    }

    /** Signals Composables to start exit animations. They control timing, not us.
     * Note: This only updates the state. The actual exit animation must be triggered
     * by the Composable content when ready.
     */
    fun dismiss() {
        isVisible.value = false
    }
}

/** API for Composables to observe state and trigger animations. */
data class SplashScreenController(
    private val decorator: SplashScreenDecorator,
    private val onStartExitAnimation: () -> Unit
) {
    /** For triggering custom Compose animations when splash should exit. */
    val isVisible: MutableState<Boolean> get() = decorator.isVisible

    /** Call this when your Composable animations finish. */
    fun startExitAnimation() = onStartExitAnimation()
}

/** Configuration for splash screen content and animation timing. */
data class SplashScreenConfig(
    val content: @Composable (SplashScreenController) -> Unit,
    val exitAnimationDuration: Long,
    val composeViewFadeDurationOffset: Long
)

/** DSL builder because configuration objects with 7+ fields become unwieldy. */
class SplashScreenConfigBuilder {
    private var content: (@Composable (SplashScreenController) -> Unit)? = null

    /** Default duration for exit animations in milliseconds */
    var exitAnimationDuration: Long = 600.milliseconds.inWholeMilliseconds

    /** Additional duration for compose view fade out in milliseconds */
    var composeViewFadeDurationOffset: Long = 200.milliseconds.inWholeMilliseconds

    /** Your custom Composable with access to animation triggers. */
    fun content(block: @Composable SplashScreenController.() -> Unit) {
        this.content = block
    }

    /** Validates and creates the final config. */
    internal fun build(): SplashScreenConfig {
        require(exitAnimationDuration >= 0) { "Exit animation duration must be positive" }
        require(composeViewFadeDurationOffset >= 0) { "Compose view fade duration offset cannot be negative" }
        return SplashScreenConfig(
            content = requireNotNull(content) { "Composable content must be provided for splash screen" },
            exitAnimationDuration = exitAnimationDuration,
            composeViewFadeDurationOffset = composeViewFadeDurationOffset
        )
    }
}

/**
 * Create splash screens with custom Compose content.
 *
 * ```kotlin
 * val exitAnimationDuration = 600L
 * val composeViewFadeDurationOffset = 200L
 * splashScreen = splash {
 *     this.exitAnimationDuration = exitAnimationDuration
 *     this.composeViewFadeDurationOffset = composeViewFadeDurationOffset
 *     content {
 *         HeartBeatAnimation(
 *             isVisible = isVisible.value,
 *             exitAnimationDuration = exitAnimationDuration.milliseconds,
 *             onStartExitAnimation = { startExitAnimation() }
 *         )
 *     }
 * }
 *
 * // Later, when ready to dismiss
 * splashScreen.shouldKeepOnScreen = false
 * splashScreen.dismiss()
 */
fun Activity.splash(
    builder: SplashScreenConfigBuilder.() -> Unit
): SplashScreenDecorator = SplashScreenDecorator.create(this, builder)


