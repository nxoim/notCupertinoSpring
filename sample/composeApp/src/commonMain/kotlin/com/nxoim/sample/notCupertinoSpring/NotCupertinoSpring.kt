package com.nxoim.sample.notCupertinoSpring

import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.spring
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

// this file is organized like this to prevent overload conflicts.
// usage should rely on importing these objects.
// obviously fixable by proper naming but for the sake of syntax
// ill leave it like that
fun <T> spring(
    duration: Duration = 500.milliseconds,
    bounce: Float = 0f,
    visibilityThreshold: T? = null
): SpringSpec<T> {
    require(duration.isPositive()) { "duration must be positive" }
    // -1.0 -> 2.0 (overdamped)
    //  0.0 -> 1.0 (critically damped)
    //  1.0 -> 0.0 (undamped)
    val dampingRatio = when {
        bounce >= 0f -> 1.0f - bounce
        else -> 1.0f + abs(bounce)
    }

    val periodSeconds = duration.inWholeNanoseconds / 1_000_000_000.0f
    val undampedNaturalFrequency = (2.0f * PI.toFloat()) / periodSeconds // ωn = 2π / T

    return spring(
        stiffness = undampedNaturalFrequency.pow(2),
        dampingRatio = dampingRatio,
        visibilityThreshold = visibilityThreshold
    )
}

object PhysicsBasedSpring {
    fun <T> spring(
        response: Double = 0.5,
        dampingFraction: Float = 0.825f,
        visibilityThreshold: T? = null
    ): SpringSpec<T> {
        require(response > 0) { "response must be positive" }
        require(dampingFraction > 0) { "dampingFraction must be positive" }

        return spring(
            stiffness = (2 * PI / response).pow(2).toFloat(),
            dampingRatio = dampingFraction,
            visibilityThreshold = visibilityThreshold
        )
    }

    fun <T> spring(
        mass: Float,
        stiffness: Float,
        damping: Float,
        visibilityThreshold: T? = null
    ): SpringSpec<T> {
        require(mass > 0) { "mass must be positive" }
        require(stiffness > 0) { "stiffness must be positive" }
        require(damping >= 0) { "damping must be non-negative" }

        // this reads misleading but the result is correct
        return spring(
            dampingRatio = damping / (2 * sqrt(mass * stiffness)),
            stiffness = sqrt(stiffness / mass),
            visibilityThreshold = visibilityThreshold
        )
    }
}

object SettlingDurationSpring {
    fun <T> spring(
        settlingDuration: Duration,
        dampingRatio: Float,
        visibilityThreshold: T? = null
    ): SpringSpec<T> {
        require(settlingDuration.isPositive()) { "settlingDuration must be positive" }

        return spring(
            stiffness = (2 * PI.toFloat() / (settlingDuration.inWholeMilliseconds / 1000f)).pow(2) / (dampingRatio.pow(2)),
            dampingRatio = dampingRatio,
            visibilityThreshold = visibilityThreshold
        )
    }
}
