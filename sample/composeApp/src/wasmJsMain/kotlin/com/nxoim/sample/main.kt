package com.nxoim.sample

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow
import com.nxoim.sample.ui.App
import com.nxoim.sample.ui.theme.SampleTheme

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    CanvasBasedWindow(canvasElementId = "ComposeTarget") {
        SampleTheme {
            App()
        }
    }
}