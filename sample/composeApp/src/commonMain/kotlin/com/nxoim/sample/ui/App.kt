package com.nxoim.sample.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.SpringSpec
import androidx.compose.foundation.LocalOverscrollFactory
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberOverscrollEffect
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.nxoim.sample.notCupertinoSpring.bouncy
import com.nxoim.sample.notCupertinoSpring.interactiveSpring
import com.nxoim.sample.notCupertinoSpring.smooth
import com.nxoim.sample.notCupertinoSpring.snappy
import com.nxoim.sample.notCupertinoSpring.spring
import com.nxoim.sample.ui.components.Demos
import com.nxoim.sample.ui.components.SpringCustomizer
import com.nxoim.sample.ui.components.SpringEquivalentInfo
import com.nxoim.sample.ui.components.demos.AnimationTypeSelector
import com.nxoim.sample.ui.theme.SampleTheme

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

                        SpringEquivalentInfo(finalSpec = finalSpec, modifier = Modifier.padding(16.dp))

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

val animationTypes = mapOf<String, SpringSpec<Float>>(
    "Default" to spring(),
    "Custom" to spring(), // placeholder spring spec
    "Interactive" to interactiveSpring(),
    "Bouncy" to bouncy(),
    "Smooth" to smooth(),
    "Snappy" to snappy()
)

