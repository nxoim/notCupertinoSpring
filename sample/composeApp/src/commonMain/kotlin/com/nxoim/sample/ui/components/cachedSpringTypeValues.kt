package com.nxoim.sample.ui.components

import androidx.compose.runtime.mutableStateMapOf
import kotlin.time.Duration.Companion.milliseconds

val cachedSpringTypeValues = mutableStateMapOf(
    SpringType.Zero::class to SpringType.Zero(500.milliseconds, 0f),
    SpringType.One::class to SpringType.One(0.5, 0.825f),
    SpringType.Two::class to SpringType.Two(1f, 100f, 10f),
    SpringType.Three::class to SpringType.Three(500.milliseconds, 0.5f)
)

