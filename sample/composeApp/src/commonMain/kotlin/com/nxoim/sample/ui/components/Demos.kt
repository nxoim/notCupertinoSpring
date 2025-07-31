package com.nxoim.sample.ui.components

import androidx.compose.animation.core.SpringSpec
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import com.nxoim.sample.ui.components.demos.AnchoredDraggableCircle
import com.nxoim.sample.ui.components.demos.ChainedCircles
import com.nxoim.sample.ui.components.demos.RepeatingSpringCircle

@Composable
fun Demos(
    selectedSpec: SpringSpec<Float>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        DemoContainer(Modifier.height(200.dp)) {
            RepeatingSpringCircle(
                specForPercentages = selectedSpec.derive(visibilityThreshold = 0.0001f),
                initialVelocity = 20f
            )
        }

        DemoContainer(Modifier.height(200.dp)) {
            AnchoredDraggableCircle(specForPixels = selectedSpec.derive(visibilityThreshold = Offset.onePixel))
        }

        DemoContainer(modifier = Modifier.height(400.dp)) {
            ChainedCircles(specForPixels = selectedSpec.derive(visibilityThreshold = Offset.onePixel))
        }
    }
}

@Composable
private fun DemoContainer(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}