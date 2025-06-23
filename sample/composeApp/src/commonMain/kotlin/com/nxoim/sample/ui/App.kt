package com.nxoim.sample.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.LocalOverscrollFactory
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.draggable2D
import androidx.compose.foundation.gestures.rememberDraggable2DState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberOverscrollEffect
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import androidx.compose.ui.util.lerp
import com.nxoim.sample.notCupertinoSpring.NotCupertinoDefaultSprings
import com.nxoim.sample.notCupertinoSpring.PhysicsBasedSpring
import com.nxoim.sample.notCupertinoSpring.SettlingDurationSpring
import com.nxoim.sample.notCupertinoSpring.spring
import com.nxoim.sample.ui.theme.SampleTheme
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.reflect.KClass
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {
    var selectedType by remember { mutableStateOf(animationTypes.keys.first()) }
    val selectedSpec = animationTypes[selectedType]!!

    var showCustomSpringDialog by remember { mutableStateOf(false) } // New state for dialog
    var customSpring by remember { mutableStateOf<SpringSpec<Float>?>(null) } // Store custom spring

    // Calculate final spec (custom or selected)
    val finalSpec by remember(selectedType, customSpring) {
        derivedStateOf {
            if (selectedType == "Custom" && customSpring != null) customSpring!!
            else selectedSpec
        }
    }
    CompositionLocalProvider(
        LocalOverscrollFactory provides rememberNotCupertinoOverscrollFactory()
    ) {
        val listOverscrollEffect = rememberOverscrollEffect()
        val overscrollVisualEffectThreshold = with(LocalDensity.current) { 128.dp.toPx() }

        SampleTheme {
            LazyColumn(
                contentPadding = WindowInsets.systemBars.asPaddingValues(),
                overscrollEffect = listOverscrollEffect,
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Totally not cupertino springs",
                            style = MaterialTheme.typography.headlineSmall,
                            modifier = Modifier
                                .padding(vertical = 16.dp)
                                .graphicsLayer() {
                                    (listOverscrollEffect as? NotCupertinoOverscrollEffect)?.let { listOverscrollEffect ->
                                        val progress = (listOverscrollEffect.overscrollOffset.y / overscrollVisualEffectThreshold)
                                                .coerceAtLeast(0f)

                                        val scale = lerp(1f, 1.05f, progress)

                                        scaleX = scale
                                        scaleY = scale
                                    }
                                }
                        )

                        AnimationTypeSelector(
                            types = animationTypes.keys.toList(),
                            selectedType = selectedType,
                            onTypeSelected = {
                                selectedType = it
                                showCustomSpringDialog = it == "Custom"
                            }
                        )

                        AnimatedVisibility(visible = showCustomSpringDialog) {
                            SpringCustomizer(
                                onSpringCreated = { spring -> customSpring = spring },
                                modifier = Modifier.padding(16.dp)
                            )
                        }

                        Demos(selectedSpec = finalSpec, Modifier.padding(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun Demos(
    selectedSpec: SpringSpec<Float>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        DemoContainer(Modifier.height(200.dp)) {
            RepeatingSpringCircle(
                spec = selectedSpec,
                initialVelocity = 20f
            )
        }

        DemoContainer(Modifier.height(200.dp)) {
            AnchoredDraggableCircle(spec = selectedSpec.derive(visibilityThreshold = Offset.halfAPixel))
        }

        DemoContainer(modifier = Modifier.height(400.dp)) {
            ChainedCircles(spec = selectedSpec.derive(visibilityThreshold = Offset.halfAPixel))
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

@Composable
private fun AnimationTypeSelector(
    types: List<String>,
    selectedType: String,
    onTypeSelected: (String) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(types) { type ->
            val isSelected = type == selectedType
            Button(
                onClick = { onTypeSelected(type) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                    contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = if (isSelected) 4.dp else 1.dp)
            ) {
                Text(type)
            }
        }
    }
}

@Composable
private fun RepeatingSpringCircle(
    spec: FiniteAnimationSpec<Float>,
    initialVelocity: Float
) {
    val progressAnimatable = remember { Animatable(0f) }

    LaunchedEffect(spec) {
        while (true) {
            progressAnimatable.animateTo(
                if (progressAnimatable.value <= 0f) 1f else 0f,
                spec,
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

@Composable
private fun AnchoredDraggableCircle(spec: FiniteAnimationSpec<Offset>) {
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
                                animationSpec = spec,
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


@Composable
private fun ChainedCircles(spec: FiniteAnimationSpec<Offset>) {
    val scope = rememberCoroutineScope()
    val colorScheme = MaterialTheme.colorScheme
    var animationJob by remember { mutableStateOf<Job?>(null) }

    BoxWithConstraints(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        val center = remember { Offset(constraints.maxWidth / 2f, constraints.maxHeight / 2f) }
        val circleRadius = 15.dp

        var mainCircleOffset by remember { mutableStateOf(center) }
        val follower1Offset = animateOffsetAsState(mainCircleOffset, spec)
        val follower2Offset = animateOffsetAsState(follower1Offset.value, spec)
        val follower3Offset = animateOffsetAsState(follower2Offset.value, spec)

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
//                            mainCircleOffset.animateTo(
//                                targetValue = mainCircleOffset.,
//                                animationSpec = spec,
//                                initialVelocity = dragVelocity.run { Offset(x, y) }
//                            )
                            animate(
                                initialValue = mainCircleOffset,
                                targetValue = mainCircleOffset,
                                animationSpec = spec,
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
                .size(circleRadius * 3)
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

val cachedSpringTypeValues = mutableStateMapOf(
    SpringType.Zero::class to SpringType.Zero(500.milliseconds, 0f),
    SpringType.One::class to SpringType.One(0.5, 0.825f),
    SpringType.Two::class to SpringType.Two(1f, 100f, 10f),
    SpringType.Three::class to SpringType.Three(500.milliseconds, 0.5f)
)

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun SpringCustomizer(
    onSpringCreated: (SpringSpec<Float>) -> Unit,
    modifier: Modifier = Modifier
) {
    var springTypeKClass by remember { mutableStateOf(SpringType.Zero::class as KClass<out SpringType>) }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            val springTypeClasses = listOf(
                SpringType.Zero::class to "0",
                SpringType.One::class to "1",
                SpringType.Two::class to "2",
                SpringType.Three::class to "3"
            )
            springTypeClasses.forEach { (typeClass, label) ->
                ToggleButton(
                    checked = springTypeKClass == typeClass,
                    onCheckedChange = { springTypeKClass = typeClass }
                ) {
                    Text(label)
                }
            }
        }

        AnimatedContent(targetState = springTypeKClass) {
            val settings = cachedSpringTypeValues[it]!!

            when (settings) {
                is SpringType.Zero -> {
                    var duration by remember { mutableStateOf(settings.duration) }
                    var bounce by remember { mutableStateOf(settings.bounce) }

                    LaunchedEffect(duration, bounce) {
                        cachedSpringTypeValues[SpringType.Zero::class] =
                            SpringType.Zero(duration, bounce)
                        onSpringCreated(spring(duration = duration, bounce = bounce))
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Perceptual duration: $duration")
                        Slider(
                            value = duration.inWholeMilliseconds.toFloat(),
                            onValueChange = { duration = it.toDouble().milliseconds },
                            valueRange = 0f..10.seconds.inWholeMilliseconds.toFloat()
                        )

                        Text("Bounce: $bounce")
                        Slider(value = bounce, onValueChange = { bounce = it }, valueRange = 0f..1f)
                    }
                }

                is SpringType.One -> {
                    var response by remember { mutableStateOf(settings.response.toFloat()) }
                    var dampingFraction by remember { mutableStateOf(settings.dampingFraction) }

                    LaunchedEffect(response, dampingFraction) {
                        cachedSpringTypeValues[SpringType.One::class] =
                            SpringType.One(response.toDouble(), dampingFraction)
                        onSpringCreated(
                            PhysicsBasedSpring.spring(
                                response = response.toDouble(),
                                dampingFraction = dampingFraction
                            )
                        )
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Response: $response")
                        Slider(
                            value = response,
                            onValueChange = { response = it },
                            valueRange = 0.01f..5f
                        )

                        Text("Damping Fraction: $dampingFraction")
                        Slider(
                            value = dampingFraction,
                            onValueChange = { dampingFraction = it },
                            valueRange = 0f..1f
                        )
                    }
                }

                is SpringType.Two -> {
                    var mass by remember { mutableStateOf(settings.mass) }
                    var stiffness by remember { mutableStateOf(settings.stiffness) }
                    var damping by remember { mutableStateOf(settings.damping) }

                    LaunchedEffect(mass, stiffness, damping) {
                        cachedSpringTypeValues[SpringType.Two::class] =
                            SpringType.Two(mass, stiffness, damping)
                        onSpringCreated(
                            PhysicsBasedSpring.spring(
                                mass = mass,
                                stiffness = stiffness,
                                damping = damping
                            )
                        )
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Mass: $mass")
                        Slider(value = mass, onValueChange = { mass = it }, valueRange = 0.01f..10f)

                        Text("Stiffness: $stiffness")
                        Slider(
                            value = stiffness,
                            onValueChange = { stiffness = it },
                            valueRange = 0.01f..2000f
                        )

                        Text("Damping: $damping")
                        Slider(
                            value = damping,
                            onValueChange = { damping = it },
                            valueRange = 0.01f..50f
                        )
                    }
                }

                is SpringType.Three -> {
                    var settlingDurationMs by remember {
                        mutableStateOf(settings.settlingDuration.inWholeMilliseconds.toFloat())
                    }
                    var dampingRatio by remember { mutableStateOf(settings.dampingRatio) }

                    LaunchedEffect(settlingDurationMs, dampingRatio) {
                        cachedSpringTypeValues[SpringType.Three::class] =
                            SpringType.Three(settlingDurationMs.toLong().milliseconds, dampingRatio)
                        onSpringCreated(
                            SettlingDurationSpring.spring(
                                settlingDuration = settlingDurationMs.toLong().milliseconds,
                                dampingRatio = dampingRatio
                            )
                        )
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Settling Duration (ms): ${settlingDurationMs.toLong()}")
                        Slider(
                            value = settlingDurationMs,
                            onValueChange = { settlingDurationMs = it },
                            valueRange = 1f..5000f
                        )

                        Text("Damping Ratio: $dampingRatio")
                        Slider(
                            value = dampingRatio,
                            onValueChange = { dampingRatio = it },
                            valueRange = 0f..1f
                        )
                    }
                }
            }
        }
    }
}

sealed interface SpringType {
    data class Zero(val duration: Duration, val bounce: Float) : SpringType
    data class One(val response: Double, val dampingFraction: Float) : SpringType
    data class Two(val mass: Float, val stiffness: Float, val damping: Float) : SpringType
    data class Three(val settlingDuration: Duration, val dampingRatio: Float) : SpringType
}

/**
 * is like data class .copy() except you can only specify the
 * visibility threshold
 */
fun <T> SpringSpec<*>.derive(visibilityThreshold: T? = null) =
    SpringSpec(this.dampingRatio, this.stiffness, visibilityThreshold)

val animationTypes = mapOf<String, SpringSpec<Float>>(
    "Default" to com.nxoim.sample.notCupertinoSpring.spring(),
    "Custom" to spring(), // placeholder sspring spec
    "Interactive" to NotCupertinoDefaultSprings.interactiveSpring(),
    "Bouncy" to NotCupertinoDefaultSprings.bouncy(),
    "Smooth" to NotCupertinoDefaultSprings.smooth(),
    "Snappy" to NotCupertinoDefaultSprings.snappy()
)

val Float.Companion.halfAPixel
    @Composable
    @ReadOnlyComposable
    get() = 0.5f * LocalDensity.current.density

val Offset.Companion.halfAPixel
    @Composable
    @ReadOnlyComposable
    get() = Offset(0.5f, 0.5f) * LocalDensity.current.density
