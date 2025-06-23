package com.nxoim.sample.notCupertinoSpring

import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

object NotCupertinoDefaultSprings {
    fun <T> bouncy(
        duration: Duration = 500.milliseconds,
        extraBounce: Float = 0.0f,
        visibilityThreshold: T? = null
    ) = spring(
        duration = duration,
        bounce = 0.35f + extraBounce,
        visibilityThreshold = visibilityThreshold
    )

    fun <T> smooth(
        duration: Duration = 500.milliseconds,
        extraBounce: Float = 0.0f,
        visibilityThreshold: T? = null
    ) = spring(
        duration = duration,
        bounce = (0.05f + extraBounce).coerceAtLeast(0f),
        visibilityThreshold = visibilityThreshold
    )

    fun <T> snappy(
        duration: Duration = 500.milliseconds,
        extraBounce: Float = 0.0f,
        visibilityThreshold: T? = null
    ) = spring(
        duration = duration,
        bounce = (0.15f + extraBounce),
        visibilityThreshold = visibilityThreshold
    )

    fun <T> interactiveSpring(visibilityThreshold: T? = null) = spring(
        duration = 150.milliseconds,
        visibilityThreshold = visibilityThreshold
    )
}