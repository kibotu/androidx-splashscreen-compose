package net.kibotu.splashscreendecorator.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.kibotu.splashscreen.SplashScreenDecorator
import net.kibotu.splashscreen.splash
import net.kibotu.splashscreendecorator.demo.ui.theme.SplashScreenDecoratorTheme
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

class MainActivity : ComponentActivity() {

    var splashScreen: SplashScreenDecorator? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        startSplash()

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            SplashScreenDecoratorTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    private fun startSplash() {

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

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SplashScreenDecoratorTheme {
        Greeting("Android")
    }
}
