package net.kibotu.splashscreendecorator.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.kibotu.splashscreen.SplashScreenDecorator
import net.kibotu.splashscreen.splash
import net.kibotu.splashscreendecorator.demo.ui.theme.SplashScreenDecoratorTheme
import net.kibotu.splashscreendecorator.demo.welcome.AnimatedWelcomeScreen
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

class MainActivity : ComponentActivity() {

    var splashScreen: SplashScreenDecorator? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        showSplash()

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            SplashScreenDecoratorTheme {
                AnimatedWelcomeScreen()
            }
        }
    }

    private fun showSplash() {

        val exitAnimationDuration = 600L
        val composeViewFadeDurationOffset = 200L

        splashScreen = splash {
            this.exitAnimationDuration = exitAnimationDuration
            this.composeViewFadeDurationOffset = composeViewFadeDurationOffset
            content {
                SplashScreenDecoratorTheme {
                    HeartBeatAnimation(
                        isVisible = isVisible.value,
                        exitAnimationDuration = exitAnimationDuration.milliseconds,
                        onStartExitAnimation = { startExitAnimation() }
                    )
                }
            }
        }

        // delay os splash screen for demonstration purposes
        lifecycleScope.launch {
            delay(1.seconds)
            splashScreen?.shouldKeepOnScreen = false
            delay(3.seconds)
            splashScreen?.dismiss()
        }
    }

    override fun onDestroy() {
        splashScreen = null
        super.onDestroy()
    }
}
