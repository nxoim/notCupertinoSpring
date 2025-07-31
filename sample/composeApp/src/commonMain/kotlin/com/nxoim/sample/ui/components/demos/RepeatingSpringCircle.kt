package com.nxoim.sample.ui.components.demos

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp

@Composable
fun RepeatingSpringCircle(
    specForPercentages: FiniteAnimationSpec<Float>,
    initialVelocity: Float
) {
    val progressAnimatable = remember { Animatable(0f) }

    LaunchedEffect(specForPercentages) {
        while (true) {
            progressAnimatable.animateTo(
                if (progressAnimatable.value <= 0f) 1f else 0f,
                specForPercentages,
                initialVelocity = if (progressAnimatable.value <= 0f) initialVelocity else -initialVelocity
            )
        }
    }

    val progress by progressAnimatable.asState()

    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .graphicsLayer() {
                    val scale = lerp(0.5f, 1f, progress)
                    scaleX = scale
                    scaleY = scale
                }
                .size(80.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
        )

        Text(
            text = "Initial velocity $initialVelocity",
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.outline
        )
    }
}