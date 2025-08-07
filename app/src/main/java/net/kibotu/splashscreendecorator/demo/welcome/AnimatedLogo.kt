package net.kibotu.splashscreendecorator.demo.welcome

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AnimatedLogo(isVisible: Boolean) {
    val scale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "logoScale"
    )

    val rotation by animateFloatAsState(
        targetValue = if (isVisible) 0f else -180f,
        animationSpec = tween(1000, easing = FastOutSlowInEasing),
        label = "logoRotation"
    )

    Box(
        modifier = Modifier.Companion
            .size(120.dp)
            .scale(scale)
            .graphicsLayer { rotationZ = rotation }
            .background(
                brush = Brush.Companion.radialGradient(
                    colors = listOf(
                        Color(0xFF00D4FF),
                        Color(0xFF0099CC),
                        Color(0xFF006699)
                    )
                ),
                shape = CircleShape
            ),
        contentAlignment = Alignment.Companion.Center
    ) {
        Box(
            modifier = Modifier.Companion
                .size(80.dp)
                .background(
                    color = Color.Companion.White.copy(alpha = 0.9f),
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