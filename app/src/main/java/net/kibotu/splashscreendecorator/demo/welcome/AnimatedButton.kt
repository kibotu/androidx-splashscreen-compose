package net.kibotu.splashscreendecorator.demo.welcome

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AnimatedButton(isVisible: Boolean) {
    val scale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "buttonScale"
    )

    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(1000, delayMillis = 2500),
        label = "buttonAlpha"
    )

    Button(
        onClick = { /* Handle click */ },
        modifier = Modifier.Companion
            .scale(scale)
            .alpha(alpha)
            .height(56.dp)
            .widthIn(min = 200.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Companion.Transparent
        ),
        shape = RoundedCornerShape(28.dp)
    ) {
        Box(
            modifier = Modifier.Companion
                .fillMaxSize()
                .background(
                    brush = Brush.Companion.horizontalGradient(
                        colors = listOf(
                            Color(0xFF00D4FF),
                            Color(0xFF4ECDC4),
                            Color(0xFF44A08D)
                        )
                    ),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(28.dp)
                ),
            contentAlignment = Alignment.Companion.Center
        ) {
            Text(
                text = "Get Started",
                color = Color.Companion.White,
                fontWeight = FontWeight.Companion.Bold,
                fontSize = 16.sp
            )
        }
    }
}