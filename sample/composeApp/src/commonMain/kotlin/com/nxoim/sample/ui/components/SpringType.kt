package com.nxoim.sample.ui.components

import kotlin.time.Duration

sealed interface SpringType {
    data class Zero(val duration: Duration, val bounce: Float) : SpringType
    data class One(val response: Double, val dampingFraction: Float) : SpringType
    data class Two(val mass: Float, val stiffness: Float, val damping: Float) : SpringType
    data class Three(val settlingDuration: Duration, val dampingRatio: Float) : SpringType
}
