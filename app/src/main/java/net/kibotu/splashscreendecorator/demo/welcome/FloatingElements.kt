package net.kibotu.splashscreendecorator.demo.welcome

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun FloatingElements() {
    val infiniteTransition = rememberInfiniteTransition(label = "floatingElements")

    repeat(12) { index ->
        val animatedOffset by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 8000 + (index * 500),
                    easing = LinearEasing
                ),
                repeatMode = RepeatMode.Restart
            ),
            label = "floatingOffset$index"
        )

        val animatedScale by infiniteTransition.animateFloat(
            initialValue = 0.5f,
            targetValue = 1.5f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 3000 + (index * 200),
                    easing = FastOutSlowInEasing
                ),
                repeatMode = RepeatMode.Reverse
            ),
            label = "floatingScale$index"
        )

        Box(
            modifier = Modifier.Companion
                .fillMaxSize()
                .graphicsLayer {
                    translationX =
                        cos(Math.toRadians(animatedOffset.toDouble())).toFloat() * (50 + index * 15)
                    translationY =
                        sin(Math.toRadians(animatedOffset.toDouble())).toFloat() * (30 + index * 10)
                    scaleX = animatedScale
                    scaleY = animatedScale
                    alpha = 0.1f + (index % 3) * 0.05f
                }
        ) {
            Box(
                modifier = Modifier.Companion
                    .size((8 + index * 2).dp)
                    .background(
                        color = when (index % 4) {
                            0 -> Color(0xFF00D4FF)
                            1 -> Color(0xFFFF6B6B)
                            2 -> Color(0xFF4ECDC4)
                            else -> Color(0xFFFFE66D)
                        },
                        shape = if (index % 2 == 0) CircleShape else RoundedCornerShape(2.dp)
                    )
                    .blur(1.dp)
            )
        }
    }
}