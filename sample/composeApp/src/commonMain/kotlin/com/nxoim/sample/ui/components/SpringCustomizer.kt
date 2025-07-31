package com.nxoim.sample.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.SpringSpec
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nxoim.sample.notCupertinoSpring.PhysicsBasedSpring
import com.nxoim.sample.notCupertinoSpring.SettlingDurationSpring
import com.nxoim.sample.notCupertinoSpring.spring
import kotlin.reflect.KClass
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SpringCustomizer(
    onSpringCreated: (SpringSpec<Float>) -> Unit,
    modifier: Modifier = Modifier,
) {
    var springTypeKClass by remember { mutableStateOf(SpringType.Zero::class as KClass<out SpringType>) }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            val springTypeClasses = listOf(
                SpringType.Zero::class to "0",
                SpringType.One::class to "1",
                SpringType.Two::class to "2",
                SpringType.Three::class to "3"
            )
            springTypeClasses.forEach { (typeClass, label) ->
                ToggleButton(
                    checked = springTypeKClass == typeClass,
                    onCheckedChange = { springTypeKClass = typeClass }
                ) {
                    Text(label)
                }
            }
        }

        AnimatedContent(targetState = springTypeKClass) {
            val settings = cachedSpringTypeValues[it]!!

            when (settings) {
                is SpringType.Zero -> {
                    var duration by remember { mutableStateOf(settings.duration) }
                    var bounce by remember { mutableStateOf(settings.bounce) }

                    LaunchedEffect(duration, bounce) {
                        cachedSpringTypeValues[SpringType.Zero::class] =
                            SpringType.Zero(duration, bounce)
                        onSpringCreated(spring(duration = duration, bounce = bounce))
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Perceptual duration: $duration")
                        Slider(
                            value = duration.inWholeMilliseconds.toFloat(),
                            onValueChange = { duration = it.toDouble().milliseconds },
                            valueRange = 0.001f..10.seconds.inWholeMilliseconds.toFloat()
                        )

                        Text("Bounce: $bounce")
                        Slider(value = bounce, onValueChange = { bounce = it }, valueRange = 0.001f..1f)
                    }
                }

                is SpringType.One -> {
                    var response by remember { mutableStateOf(settings.response.toFloat()) }
                    var dampingFraction by remember { mutableStateOf(settings.dampingFraction) }

                    LaunchedEffect(response, dampingFraction) {
                        cachedSpringTypeValues[SpringType.One::class] =
                            SpringType.One(response.toDouble(), dampingFraction)
                        onSpringCreated(
                            PhysicsBasedSpring.spring(
                                response = response.toDouble(),
                                dampingFraction = dampingFraction
                            )
                        )
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Response: $response")
                        Slider(
                            value = response,
                            onValueChange = { response = it },
                            valueRange = 0.01f..5f
                        )

                        Text("Damping Fraction: $dampingFraction")
                        Slider(
                            value = dampingFraction,
                            onValueChange = { dampingFraction = it },
                            valueRange = 0.001f..1f
                        )
                    }
                }

                is SpringType.Two -> {
                    var mass by remember { mutableStateOf(settings.mass) }
                    var stiffness by remember { mutableStateOf(settings.stiffness) }
                    var damping by remember { mutableStateOf(settings.damping) }

                    LaunchedEffect(mass, stiffness, damping) {
                        cachedSpringTypeValues[SpringType.Two::class] =
                            SpringType.Two(mass, stiffness, damping)
                        onSpringCreated(
                            PhysicsBasedSpring.spring(
                                mass = mass,
                                stiffness = stiffness,
                                damping = damping
                            )
                        )
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Mass: $mass")
                        Slider(value = mass, onValueChange = { mass = it }, valueRange = 0.01f..10f)

                        Text("Stiffness: $stiffness")
                        Slider(
                            value = stiffness,
                            onValueChange = { stiffness = it },
                            valueRange = 0.01f..2000f
                        )

                        Text("Damping: $damping")
                        Slider(
                            value = damping,
                            onValueChange = { damping = it },
                            valueRange = 0.01f..50f
                        )
                    }
                }

                is SpringType.Three -> {
                    var settlingDurationMs by remember {
                        mutableStateOf(settings.settlingDuration.inWholeMilliseconds.toFloat())
                    }
                    var dampingRatio by remember { mutableStateOf(settings.dampingRatio) }

                    LaunchedEffect(settlingDurationMs, dampingRatio) {
                        cachedSpringTypeValues[SpringType.Three::class] =
                            SpringType.Three(settlingDurationMs.toLong().milliseconds, dampingRatio)
                        onSpringCreated(
                            SettlingDurationSpring.spring(
                                settlingDuration = settlingDurationMs.toLong().milliseconds,
                                dampingRatio = dampingRatio
                            )
                        )
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Settling Duration (ms): ${settlingDurationMs.toLong()}")
                        Slider(
                            value = settlingDurationMs,
                            onValueChange = { settlingDurationMs = it },
                            valueRange = 1f..5000f
                        )

                        Text("Damping Ratio: $dampingRatio")
                        Slider(
                            value = dampingRatio,
                            onValueChange = { dampingRatio = it },
                            valueRange = 0.001f..1f
                        )
                    }
                }
            }
        }
    }
}