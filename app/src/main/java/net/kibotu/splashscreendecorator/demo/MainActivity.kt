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

        // delay for demonstration purposes
        lifecycleScope.launch {
            // delay os splash screen
            delay(1.seconds)
            splashScreen?.shouldKeepOnScreen = false
            // delay custom splash screen
            delay(3.seconds)
            splashScreen?.dismiss()
        }
    }

    private fun showSplash() {
        splashScreen = splash {
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
    }

    override fun onDestroy() {
        splashScreen = null
        super.onDestroy()
    }
}
