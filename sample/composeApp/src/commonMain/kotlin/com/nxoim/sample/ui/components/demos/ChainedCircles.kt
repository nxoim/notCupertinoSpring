package com.nxoim.sample.ui.components.demos

import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.draggable2D
import androidx.compose.foundation.gestures.rememberDraggable2DState
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@Composable
fun ChainedCircles(specForPixels: FiniteAnimationSpec<Offset>) {
    val scope = rememberCoroutineScope()
    val colorScheme = MaterialTheme.colorScheme
    var animationJob by remember { mutableStateOf<Job?>(null) }

    BoxWithConstraints(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        val center = remember { Offset(constraints.maxWidth / 2f, constraints.maxHeight / 2f) }
        val circleRadius = 15.dp

        var mainCircleOffset by remember { mutableStateOf(center) }
        val follower1Offset = animateOffsetAsState(mainCircleOffset, specForPixels)
        val follower2Offset = animateOffsetAsState(follower1Offset.value, specForPixels)
        val follower3Offset = animateOffsetAsState(follower2Offset.value, specForPixels)

        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            drawLine(
                Color.Gray,
                follower3Offset.value,
                follower2Offset.value,
                2.dp.toPx(),
                cap = StrokeCap.Round
            )
            drawLine(
                Color.Gray,
                follower2Offset.value,
                follower1Offset.value,
                2.dp.toPx(),
                cap = StrokeCap.Round
            )
            drawLine(
                Color.Gray,
                follower1Offset.value,
                mainCircleOffset,
                2.dp.toPx(),
                cap = StrokeCap.Round
            )

            listOf(
                follower1Offset,
                follower2Offset,
                follower3Offset
            ).forEachIndexed { index, follower ->
                drawCircle(
                    color = Color.Gray.copy(alpha = 1f - (index * 0.2f)),
                    radius = circleRadius.toPx(),
                    center = follower.value
                )
            }
        }

        Spacer(
            modifier = Modifier
                .offset { mainCircleOffset.round() - center.round() }
                .draggable2D(
                    state = rememberDraggable2DState { delta ->
                        scope.launch {
                            mainCircleOffset += delta
                        }
                    },
                    startDragImmediately = true,
                    onDragStarted = {
                        animationJob?.cancel()
                    },
                    onDragStopped = { dragVelocity ->
                        animationJob = scope.launch {
                            animate(
                                initialValue = mainCircleOffset,
                                targetValue = center,
                                animationSpec = specForPixels,
                                initialVelocity = dragVelocity.run { Offset(x, y) },
                                typeConverter = Offset.VectorConverter
                            ) { value, _ ->
                                if (isActive) mainCircleOffset = value
                            }
                        }
                    }
                )
                .drawWithContent() {
                    val drawCenter = Offset(size.width / 2f, size.height / 2f)
                    mainCircle(colorScheme.primary, circleRadius.toPx(), drawCenter)
                }
                .size(circleRadius * 6)
        )

        Text(
            text = "Drag the main circle",
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            textAlign = TextAlign.Center,
            color = colorScheme.outline
        )
    }
}

private fun DrawScope.mainCircle(
    primaryColor: Color,
    radius: Float,
    center: Offset
) {
    drawCircle(
        color = primaryColor,
        radius = radius + 2.dp.toPx(),
        center = center
    )
    drawCircle(
        color = Color.White,
        radius = radius - 2.dp.toPx(),
        center = center
    )
}