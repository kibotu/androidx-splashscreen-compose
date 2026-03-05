# androidx-splashscreen-compose 🎨

[![Maven Central Version](https://img.shields.io/maven-central/v/net.kibotu/androidx-splashscreen-compose)](https://central.sonatype.com/artifact/net.kibotu/androidx-splashscreen-compose)
[![](https://jitpack.io/v/kibotu/androidx-splashscreen-compose.svg)](https://jitpack.io/#kibotu/androidx-splashscreen-compose)
[![Android CI](https://github.com/kibotu/androidx-splashscreen-compose/actions/workflows/android.yml/badge.svg)](https://github.com/kibotu/androidx-splashscreen-compose/actions/workflows/android.yml)
[![API](https://img.shields.io/badge/Min%20API-23%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=23)
[![API](https://img.shields.io/badge/Target%20API-36%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=36)
[![API](https://img.shields.io/badge/Java-17-brightgreen.svg?style=flat)](https://www.oracle.com/java/technologies/javase/17all-relnotes.html)
[![Gradle Version](https://img.shields.io/badge/gradle-9.4.0-green.svg)](https://docs.gradle.org/current/release-notes)
[![Kotlin](https://img.shields.io/badge/kotlin-2.3.0-green.svg)](https://kotlinlang.org/)
[![License](https://img.shields.io/badge/license-Apache%202-blue)](LICENSE)

Let's cut to the chase: Android's default splash screen is boring. This library lets you create stunning animated splash screens using Compose without the headache. No more static drawables, no more janky transitions.

## What's the Point? 🎯

Look, we all know the pain points:
- Android's splash screen is just a static image
- Animations? Good luck with that
- Transitions that look like they're from 2010
- Zero Compose support out of the box

**Here's what you get with androidx-splashscreen-compose:**
- Drop-in Compose animations that actually look good
- Smooth transitions that don't make users cringe
- Complete control over timing and animations
- Works with AndroidX SplashScreen, not against it

## Get Started in 30 Seconds 🚀

1. Add the dependency:
```groovy
implementation 'net.kibotu:androidx-splashscreen-compose:{latest-version}'
```

2. Create your splash screen:
```kotlin
class MainActivity : ComponentActivity() {

    private var splashScreen: SplashScreenDecorator? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        val exitDuration = 800L

        // Initialize before super.onCreate()
        splashScreen = splash {
            exitAnimationDuration = exitDuration
            composeViewFadeDurationOffset = 200
            backgroundColor = Color.White // match your windowSplashScreenBackground
            content {
                MyAnimation(
                    isVisible = isVisible.value,
                    exitAnimationDuration = exitDuration.milliseconds,
                    onStartExitAnimation = { startExitAnimation() }
                )
            }
        }
        splashScreen?.shouldKeepOnScreen = false

        super.onCreate(savedInstanceState)

        setContent {
            MyAppTheme {
                MainScreen()
            }
        }

        // Dismiss after your content is ready, e.g. after requests are complete
        lifecycleScope.launch {
            delay(2.seconds)
            splashScreen?.dismiss()
        }
    }

    override fun onDestroy() {
        splashScreen = null
        super.onDestroy()
    }
}
```

That's it. No, really.

## Show Me the Good Stuff 🎨

### Heartbeat Animation Example

Here's a real-world example of a heartbeat animation that actually ships in production apps:



```kotlin
fun HeartBeatAnimation(
    modifier: Modifier = Modifier,
    isVisible: Boolean = true,
    exitAnimationDuration: Duration = Duration.ZERO,
    onStartExitAnimation: () -> Unit = {}
) {
    // Animation constants
    val rippleCount = 4
    val rippleDurationMs = 3313
    val rippleDelayMs = rippleDurationMs / 8
    val baseSize = 144.dp
    val containerSize = 288.dp

    // Track exit animation state
    var isExitAnimationStarted by remember { mutableStateOf(false) }

    // Trigger exit animation when visibility changes
    LaunchedEffect(isVisible) {
        if (!isVisible && !isExitAnimationStarted) {
            isExitAnimationStarted = true
            onStartExitAnimation()
        }
    }

    // Calculate screen diagonal for exit animation scaling
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    val screenHeight = configuration.screenHeightDp
    val screenDiagonal = sqrt((screenWidth * screenWidth + screenHeight * screenHeight).toFloat())

    // Exit animation scale with snappy easing
    val snappyEasing = CubicBezierEasing(0.2f, 0.0f, 0.2f, 1.0f)
    val exitAnimationScale by animateFloatAsState(
        targetValue = if (isExitAnimationStarted) screenDiagonal / baseSize.value else 0f,
        animationSpec = tween(
            durationMillis = exitAnimationDuration.toInt(DurationUnit.MILLISECONDS),
            easing = snappyEasing
        ),
        label = "exitScale"
    )

    // Infinite ripple animation transition
    val infiniteTransition = rememberInfiniteTransition(label = "heartbeatTransition")

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Only show ripples when visible and not exiting
        if (isVisible && !isExitAnimationStarted) {
            Box(
                modifier = Modifier.size(containerSize),
                contentAlignment = Alignment.Center
            ) {
                // Create ripple circles with staggered animations
                repeat(rippleCount) { index ->
                    RippleCircle(
                        infiniteTransition = infiniteTransition,
                        index = index,
                        rippleDurationMs = rippleDurationMs,
                        rippleDelayMs = rippleDelayMs,
                        baseSize = baseSize
                    )
                }
            }
        }

        // Exit animation circle
        if (isExitAnimationStarted) {
            Box(
                modifier = Modifier
                    .size(baseSize)
                    .graphicsLayer {
                        scaleX = exitAnimationScale
                        scaleY = exitAnimationScale
                    }
                    .background(
                        color = blueCatalina,
                        shape = CircleShape
                    )
            )
        }
    }
}

@Composable
private fun RippleCircle(
    infiniteTransition: InfiniteTransition,
    index: Int,
    rippleDurationMs: Int,
    rippleDelayMs: Int,
    baseSize: Dp
) {
    val totalDuration = rippleDurationMs + (rippleDelayMs * index)
    val easing = CubicBezierEasing(0.4f, 0.0f, 0.2f, 1.0f)

    // Animate scale from 1f to 4f
    val animatedScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 4f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = totalDuration,
                delayMillis = rippleDelayMs * index,
                easing = easing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "rippleScale$index"
    )

    // Animate alpha from 0.25f to 0f
    val animatedAlpha by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = totalDuration,
                delayMillis = rippleDelayMs * index,
                easing = easing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "rippleAlpha$index"
    )

    Box(
        modifier = Modifier
            .size(baseSize)
            .graphicsLayer {
                scaleX = animatedScale
                scaleY = animatedScale
                alpha = animatedAlpha
            }
            .background(
                color = blueCatalina,
                shape = CircleShape
            )
    )
}

```

## Pro Tips 💡

1. **Timing is Everything**
   ```kotlin
   splashScreen = splash {
       exitAnimationDuration = 800L // Sweet spot for most animations
       composeViewFadeDurationOffset = 200 // Prevents jarring transitions
       backgroundColor = splashBg // Match windowSplashScreenBackground
       content {
           // Your composable here
       }
   }
   ```

2. **Memory Management**
   ```kotlin
   override fun onDestroy() {
       splashScreen = null // Don't leak memory
       super.onDestroy()
   }
   ```

3. **Performance First**
   - Use `rememberInfiniteTransition()` for repeating animations
   - Keep animations under 1 second (users hate waiting)
   - Test on low-end devices

## Comparison with Alternatives 🔄

### vs *only* AndroidX SplashScreen

| Feature | androidx-splashscreen-compose | AndroidX SplashScreen |
|---------|----------------------|----------------------|
| Animation Support | ✅ Full Compose animations | ❌ Static vector only |
| Custom Content | ✅ Any Composable | ❌ Icon + background only |
| Transition Control | ✅ Precise timing control | ❌ Limited control |
| Branding Flexibility | ✅ Complete creative freedom | ❌ Very constrained |
| Implementation Complexity | ✅ Simple DSL setup | ✅ Minimal setup |
| Performance | ✅ Optimized Compose rendering | ✅ Lightweight |
| Backward Compatibility | ✅ Built on AndroidX | ✅ Native support |

### vs Custom Splash Activities

| Feature | androidx-splashscreen-compose | Custom Splash Activity |
|---------|----------------------|------------------------|
| Android 12+ Compliance | ✅ Fully compliant | ❌ Requires extra work |
| App Launch Performance | ✅ No additional activity | ❌ Extra activity overhead |
| Transition Seamlessness | ✅ Native system integration | ❌ Potential flicker |
| Code Complexity | ✅ Single file setup | ❌ Multiple components |
| Maintenance | ✅ Library handles updates | ❌ Manual Android compliance |   
   
## When to Use What 🤔

**Use androidx-splashscreen-compose when:**
- You need animations that don't look like they're from a 2010 tutorial
- Your brand guidelines require more than a static logo
- You want Compose-based animations without the setup headache
- Android 12+ compliance with zero additional effort
- Seamless integration with existing AndroidX SplashScreen setup

**Stick with AndroidX SplashScreen when:**
- A static logo is all you need
- You're optimizing for the smallest possible APK size
- You don't need any custom animations

**Choose Custom Splash Activity when:**
- Pre-Android 12 apps with no compliance requirements
- Complex initialization flows requiring multiple screens
- Non-Compose apps with View-based animations

## Compatibility 📱

- Minimum Android SDK: 23
- Target Android SDK: 36
- Kotlin: 2.3.0
- Java: 17
- Gradle: 9.4.0

## Contributing 🤝

Got ideas? Found a bug? PRs are welcome:

1. Fork it
2. Create your feature branch (`git checkout -b feature/amazing`)
3. Commit your changes (`git commit -m 'Add something amazing'`)
4. Push to the branch (`git push origin feature/amazing`)
5. Open a Pull Request

## License 📄

Apache 2.0 - do what you want, just don't blame us if something goes wrong. See [LICENSE](LICENSE) for the boring details.

---
- Built on top of [AndroidX SplashScreen](https://developer.android.com/develop/ui/views/launch/splash-screen)
- Powered by [Jetpack Compose](https://developer.android.com/compose)
- Inspired by modern app branding expectations
- Made with ☕ by [kibotu](https://github.com/kibotu)