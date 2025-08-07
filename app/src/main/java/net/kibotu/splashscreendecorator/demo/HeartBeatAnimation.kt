package net.kibotu.splashscreendecorator.demo

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.InfiniteTransition
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import net.kibotu.splashscreendecorator.demo.ui.theme.SplashScreenDecoratorTheme
import kotlin.math.sqrt
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.DurationUnit


private val blueCatalina: Color = Color(0xFF063773)

/**
 * A heartbeat animation composable that displays ripple effects with an optional exit animation.
 *
 * @param modifier Modifier to be applied to the animation container
 * @param isVisible Whether the animation should be visible and running
 * @param exitAnimationDuration Duration for the exit animation
 * @param onStartExitAnimation Callback invoked when the exit animation starts
 */
@Composable
fun HeartBeatAnimation(
    modifier: Modifier = Modifier,
    isVisible: Boolean = true,
    exitAnimationDuration: Duration = Duration.ZERO,
    onStartExitAnimation: () -> Unit = {}
) {
    // Animation constants
    val rippleCount = 4
    val rippleDurationMs = 3313
    val rippleDelayMs = rippleDurationMs / 8
    val baseSize = 144.dp
    val containerSize = 288.dp

    // Track exit animation state
    var isExitAnimationStarted by remember { mutableStateOf(false) }

    // Trigger exit animation when visibility changes
    LaunchedEffect(isVisible) {
        if (!isVisible && !isExitAnimationStarted) {
            isExitAnimationStarted = true
            onStartExitAnimation()
        }
    }

    // Calculate screen diagonal for exit animation scaling
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    val screenHeight = configuration.screenHeightDp
    val screenDiagonal = sqrt((screenWidth * screenWidth + screenHeight * screenHeight).toFloat())

    // Exit animation scale with snappy easing
    val snappyEasing = CubicBezierEasing(0.2f, 0.0f, 0.2f, 1.0f)
    val exitAnimationScale by animateFloatAsState(
        targetValue = if (isExitAnimationStarted) screenDiagonal / baseSize.value else 0f,
        animationSpec = tween(
            durationMillis = exitAnimationDuration.toInt(DurationUnit.MILLISECONDS),
            easing = snappyEasing
        ),
        label = "exitScale"
    )

    // Infinite ripple animation transition
    val infiniteTransition = rememberInfiniteTransition(label = "heartbeatTransition")

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Only show ripples when visible and not exiting
        if (isVisible && !isExitAnimationStarted) {
            Box(
                modifier = Modifier.size(containerSize),
                contentAlignment = Alignment.Center
            ) {
                // Create ripple circles with staggered animations
                repeat(rippleCount) { index ->
                    RippleCircle(
                        infiniteTransition = infiniteTransition,
                        index = index,
                        rippleDurationMs = rippleDurationMs,
                        rippleDelayMs = rippleDelayMs,
                        baseSize = baseSize
                    )
                }
            }
        }

        // Exit animation circle
        if (isExitAnimationStarted) {
            Box(
                modifier = Modifier
                    .size(baseSize)
                    .graphicsLayer {
                        scaleX = exitAnimationScale
                        scaleY = exitAnimationScale
                    }
                    .background(
                        color = blueCatalina,
                        shape = CircleShape
                    )
            )
        }
    }
}

/**
 * Individual ripple circle component with staggered animation
 */
@Composable
private fun RippleCircle(
    infiniteTransition: InfiniteTransition,
    index: Int,
    rippleDurationMs: Int,
    rippleDelayMs: Int,
    baseSize: Dp
) {
    val totalDuration = rippleDurationMs + (rippleDelayMs * index)
    val easing = CubicBezierEasing(0.4f, 0.0f, 0.2f, 1.0f)

    // Animate scale from 1f to 4f
    val animatedScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 4f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = totalDuration,
                delayMillis = rippleDelayMs * index,
                easing = easing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "rippleScale$index"
    )

    // Animate alpha from 0.25f to 0f
    val animatedAlpha by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = totalDuration,
                delayMillis = rippleDelayMs * index,
                easing = easing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "rippleAlpha$index"
    )

    Box(
        modifier = Modifier
            .size(baseSize)
            .graphicsLayer {
                scaleX = animatedScale
                scaleY = animatedScale
                alpha = animatedAlpha
            }
            .background(
                color = blueCatalina,
                shape = CircleShape
            )
    )
}


@Preview(showBackground = true)
@Composable
fun HeartBeatAnimationPreview() {
    SplashScreenDecoratorTheme {
        HeartBeatAnimation(
            isVisible = true,
            exitAnimationDuration = 600L.milliseconds,
            onStartExitAnimation = { }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RippleCirclePreview() {
    SplashScreenDecoratorTheme {
        RippleCircle(
            infiniteTransition = rememberInfiniteTransition(label = "heartbeatTransition"),
            index = 0,
            rippleDurationMs = 3313 / 4,
            rippleDelayMs = (3313 / 4) / 8,
            baseSize = 144.dp
        )
    }
}