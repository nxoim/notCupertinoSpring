package com.nxoim.sample.ui.components.demos

import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animate
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.draggable2D
import androidx.compose.foundation.gestures.rememberDraggable2DState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.abs

@Composable
fun AnchoredDraggableCircle(specForPixels: FiniteAnimationSpec<Offset>) {
    BoxWithConstraints(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        val boxWidthPx = with(LocalDensity.current) { maxWidth.toPx() }
        val circleRadiusPx = with(LocalDensity.current) { 25.dp.toPx() }

        val leftAnchor = Offset(-boxWidthPx / 4, 0f)
        val rightAnchor = Offset(boxWidthPx / 4, 0f)
        val velocityThreshold = with(LocalDensity.current) { 4.dp.toPx() }

        var offset by remember { mutableStateOf(leftAnchor) }
        val scope = rememberCoroutineScope()
        var animationJob by remember { mutableStateOf<Job?>(null) }

        Canvas(modifier = Modifier.matchParentSize()) {
            drawAnchor(this.center + leftAnchor, circleRadiusPx)
            drawAnchor(this.center + rightAnchor, circleRadiusPx)
        }

        Box(
            modifier = Modifier
                .offset { offset.round() }
                .size(50.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
                .draggable2D(
                    rememberDraggable2DState { offset += it },
                    onDragStarted = {
                        animationJob?.cancel()
                    },
                    startDragImmediately = true,
                    onDragStopped = { dragVelocity ->
                        val target = if (abs(dragVelocity.x) > velocityThreshold) {
                            if (dragVelocity.x > 0) rightAnchor else leftAnchor
                        } else if (abs(offset.x - leftAnchor.x) < abs(offset.x - rightAnchor.x)) {
                            leftAnchor
                        } else {
                            rightAnchor
                        }

                        animationJob = scope.launch {
                            animate(
                                initialValue = offset,
                                targetValue = target,
                                animationSpec = specForPixels,
                                initialVelocity = dragVelocity.run { Offset(x, y) },
                                typeConverter = Offset.VectorConverter
                            ) { value, _ ->
                                if (isActive) offset = value
                            }
                        }
                    }
                )
        )

        Text(
            text = "Drag the circle",
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.outline
        )
    }
}

private fun DrawScope.drawAnchor(center: Offset, radius: Float) {
    drawCircle(
        color = Color.Gray,
        radius = radius,
        center = center,
        style = Stroke(
            width = 2.dp.toPx(),
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
        )
    )
}