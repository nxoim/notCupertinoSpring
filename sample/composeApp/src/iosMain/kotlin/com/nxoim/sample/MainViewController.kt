package com.nxoim.sample

import androidx.compose.ui.window.ComposeUIViewController
import com.nxoim.sample.ui.App
import com.nxoim.sample.ui.theme.SampleTheme

fun MainViewController(

) = ComposeUIViewController {
    SampleTheme { App() }
}