package com.rexosphere.leoconnect

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import com.rexosphere.leoconnect.presentation.auth.LoginScreen
import com.rexosphere.leoconnect.ui.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Common App composable for non-Android platforms
 * Android uses AndroidApp instead to provide Context
 */
@Composable
@Preview
fun App() {
    AppTheme {
        Navigator(LoginScreen())
    }
}