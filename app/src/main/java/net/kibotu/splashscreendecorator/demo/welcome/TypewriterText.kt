package net.kibotu.splashscreendecorator.demo.welcome

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun TypewriterText(
    text: String,
    isVisible: Boolean,
    delay: Long = 0,
    fontSize: TextUnit = 24.sp,
    alpha: Float = 1f
) {
    var displayedText by remember { mutableStateOf("") }

    LaunchedEffect(isVisible) {
        if (isVisible) {
            delay(delay)
            text.forEachIndexed { index, _ ->
                displayedText = text.substring(0, index + 1)
                delay(50)
            }
        }
    }

    Text(
        text = displayedText,
        fontSize = fontSize,
        fontWeight = FontWeight.Companion.Bold,
        color = Color.Companion.White.copy(alpha = alpha),
        textAlign = TextAlign.Companion.Center,
        modifier = Modifier.Companion.fillMaxWidth()
    )
}