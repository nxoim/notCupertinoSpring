package com.nxoim.sample.notCupertinoSpring

import androidx.compose.animation.core.SpringSpec
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

fun <T> bouncy(
    duration: Duration = 500.milliseconds,
    extraBounce: Float = 0.0f,
    visibilityThreshold: T? = null
): SpringSpec<T> = spring(
    duration = duration,
    bounce = 0.35f + extraBounce,
    visibilityThreshold = visibilityThreshold
)

fun <T> smooth(
    duration: Duration = 500.milliseconds,
    extraBounce: Float = 0.0f,
    visibilityThreshold: T? = null
): SpringSpec<T> = spring(
    duration = duration,
    bounce = (0.05f + extraBounce),
    visibilityThreshold = visibilityThreshold
)

fun <T> snappy(
    duration: Duration = 500.milliseconds,
    extraBounce: Float = 0.0f,
    visibilityThreshold: T? = null
): SpringSpec<T> = spring(
    duration = duration,
    bounce = (0.15f + extraBounce),
    visibilityThreshold = visibilityThreshold
)

fun <T> interactiveSpring(visibilityThreshold: T? = null): SpringSpec<T> = spring(
    duration = 150.milliseconds,
    visibilityThreshold = visibilityThreshold
)