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
import net.kibotu.borg.splashscreendecorator.demo.ui.theme.SplashScreenDecoratorTheme
import net.kibotu.splashscreen.SplashScreenDecorator
import net.kibotu.splashscreen.splash
import kotlin.time.Duration.Companion.milliseconds

class MainActivity : ComponentActivity() {

    var splashScreen: SplashScreenDecorator? = null

    override fun onCreate(savedInstanceState: Bundle?) {

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

        splashScreen?.shouldKeepOnScreen = false

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

    override fun onResume() {
        super.onResume()
        splashScreen?.dismiss()
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
