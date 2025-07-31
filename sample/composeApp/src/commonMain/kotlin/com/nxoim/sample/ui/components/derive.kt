package com.nxoim.sample.ui.components

import androidx.compose.animation.core.SpringSpec

/**
 * is like data class .copy() except you can only specify the
 * visibility threshold
 */
fun <T> SpringSpec<*>.derive(visibilityThreshold: T? = null) =
    SpringSpec(this.dampingRatio, this.stiffness, visibilityThreshold)