package net.kibotu.splashscreendecorator.demo.welcome

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import net.kibotu.splashscreendecorator.demo.ui.theme.SplashScreenDecoratorTheme

@Composable
fun AnimatedWelcomeScreen() {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(300) // Small delay to let the screen settle
        isVisible = true
    }

    Box(
        modifier = Modifier.Companion
            .fillMaxSize()
            .background(
                brush = Brush.Companion.verticalGradient(
                    colors = listOf(
                        Color(0xFF1A1A2E),
                        Color(0xFF16213E),
                        Color(0xFF0F3460),
                        Color(0xFF533A7B)
                    )
                )
            )
    ) {
        // Floating geometric elements background
        FloatingElements()

        // Main content
        Column(
            modifier = Modifier.Companion
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.Companion.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Animated logo/icon
            AnimatedLogo(isVisible = isVisible)

            Spacer(modifier = Modifier.Companion.height(48.dp))

            // Welcome text with typewriter effect
            TypewriterText(
                text = "Welcome to the Future",
                isVisible = isVisible,
                delay = 800
            )

            Spacer(modifier = Modifier.Companion.height(16.dp))

            TypewriterText(
                text = "Experience innovation at your fingertips",
                isVisible = isVisible,
                delay = 1500,
                fontSize = 16.sp,
                alpha = 0.8f
            )

            Spacer(modifier = Modifier.Companion.height(64.dp))

            // Call to action button
            AnimatedButton(isVisible = isVisible)
        }

        // Decorative elements
        DecorativeCornerElements()
    }
}


@Preview(showBackground = true)
@Composable
fun AnimatedWelcomeScreenPreview() {
    SplashScreenDecoratorTheme {
        AnimatedWelcomeScreen()
    }
}
