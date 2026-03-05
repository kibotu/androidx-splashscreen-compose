package net.kibotu.splashscreendecorator.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
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

    private var splashScreen: SplashScreenDecorator? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        showSplash()
        splashScreen?.shouldKeepOnScreen = false

        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            SplashScreenDecoratorTheme {
                AnimatedWelcomeScreen()
            }
        }

        lifecycleScope.launch {
            delay(1.seconds)
            delay(3.seconds)
            splashScreen?.dismiss()
        }
    }

    private fun showSplash() {
        val exitDuration = 800L
        val splashBg = Color(ContextCompat.getColor(this, R.color.splash_background))

        splashScreen = splash {
            exitAnimationDuration = exitDuration
            composeViewFadeDurationOffset = 200
            backgroundColor = splashBg
            content {
                SplashScreenDecoratorTheme {
                    HeartBeatAnimation(
                        backgroundColor = splashBg,
                        isVisible = isVisible.value,
                        exitAnimationDuration = exitDuration.milliseconds,
                        onStartExitAnimation = { startExitAnimation() }
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        splashScreen?.dismiss()
        splashScreen = null
        super.onDestroy()
    }
}
