package com.nxoim.sample.ui

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.OverscrollFactory
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import com.nxoim.sample.notCupertinoSpring.spring
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun rememberNotCupertinoOverscrollFactory(
    animationSpec: AnimationSpec<Float> = spring(
        duration = 400.milliseconds,
        bounce = 0f,
        visibilityThreshold = 0.5f * LocalDensity.current.density
    )
): NotCupertinoOverscrollEffectFactory {
    val density = LocalDensity.current
    val layoutDirection = LocalLayoutDirection.current

    return remember {
        NotCupertinoOverscrollEffectFactory(
            density = density,
            layoutDirection = layoutDirection,
            animationSpec = animationSpec
        )
    }
}

data class NotCupertinoOverscrollEffectFactory(
    private val density: Density,
    private val layoutDirection: LayoutDirection,
    private val animationSpec: AnimationSpec<Float>
) : OverscrollFactory {
    @OptIn(ExperimentalFoundationApi::class)
    override fun createOverscrollEffect() =
        NotCupertinoOverscrollEffect(
            density.density,
            applyClip = false,
            animationSpec = animationSpec
        )
}