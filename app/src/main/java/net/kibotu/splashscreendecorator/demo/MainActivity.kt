package net.kibotu.splashscreendecorator.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
                        isVisible = this@content.isVisible.value,
                        exitAnimationDuration = exitAnimationDuration.milliseconds,
                        onStartExitAnimation = { this@content.startExitAnimation() }
                    )
                }
            }
        }

        // delay os splash screen for demonstration purposes
        lifecycleScope.launch {
            delay(2.seconds)
            splashScreen?.shouldKeepOnScreen = false
            delay(5.seconds)
            splashScreen?.dismiss()
        }
    }

    override fun onDestroy() {
        splashScreen = null
        super.onDestroy()
    }
}
