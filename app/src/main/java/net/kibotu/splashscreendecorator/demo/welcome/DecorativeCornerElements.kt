package net.kibotu.splashscreendecorator.demo.welcome

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun DecorativeCornerElements() {
    // Top left corner decoration
    Box(
        modifier = Modifier.Companion
            .size(100.dp)
            .background(
                brush = Brush.Companion.radialGradient(
                    colors = listOf(
                        Color(0xFF00D4FF).copy(alpha = 0.3f),
                        Color.Companion.Transparent
                    )
                ),
                shape = CircleShape
            )
    )

    // Bottom right corner decoration
    Box(
        modifier = Modifier.Companion
            .fillMaxSize()
            .padding(end = 32.dp, bottom = 32.dp),
        contentAlignment = Alignment.Companion.BottomEnd
    ) {
        Box(
            modifier = Modifier.Companion
                .size(80.dp)
                .background(
                    brush = Brush.Companion.radialGradient(
                        colors = listOf(
                            Color(0xFFFF6B6B).copy(alpha = 0.2f),
                            Color.Companion.Transparent
                        )
                    ),
                    shape = CircleShape
                )
        )
    }
}