package net.kibotu.splashscreendecorator.demo.welcome

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AnimatedLogo(isVisible: Boolean) {
    val infiniteTransition = rememberInfiniteTransition(label = "gradientRotation")
    val gradientRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 3000,
                easing = LinearEasing
            )
        ),
        label = "gradientRotation"
    )

    val scale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "logoScale"
    )

    val rotation by animateFloatAsState(
        targetValue = if (isVisible) 360f else 0f,
        animationSpec = tween(1500, easing = FastOutSlowInEasing),
        label = "logoRotation"
    )

    Box(
        modifier = Modifier.Companion
            .size(120.dp)
            .scale(scale)
            .graphicsLayer { 
                rotationY = rotation
            },
        contentAlignment = Alignment.Companion.Center
    ) {
        // Rotating gradient background
        Box(
            modifier = Modifier.Companion
                .size(120.dp)
                .graphicsLayer { rotationZ = gradientRotation }
                .background(
                    brush = Brush.Companion.sweepGradient(
                        colors = listOf(
                            Color(0xFFFF0000),  // Red
                            Color(0xFFFF8000),  // Orange
                            Color(0xFFFFFF00),  // Yellow
                            Color(0xFF00FF00),  // Green
                            Color(0xFF00FFFF),  // Cyan
                            Color(0xFF0000FF),  // Blue
                            Color(0xFF8000FF),  // Purple
                            Color(0xFFFF0000),  // Red again for seamless transition
                        ),
                        center = Offset(0f, 0f)
                    ),
                    shape = CircleShape
                )
        )
        Box(
            modifier = Modifier.Companion
                .size(80.dp)
                .background(
                    color = Color.Companion.White.copy(alpha = 0.85f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Companion.Center
        ) {
            Text(
                text = "✨",
                fontSize = 40.sp
            )
        }
    }
}