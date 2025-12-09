package com.rexosphere.leoconnect.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import com.rexosphere.leoconnect.presentation.navigation.BottomBar
import com.rexosphere.leoconnect.presentation.tabs.HomeTab
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze

val LocalBottomBarPadding = compositionLocalOf { 0.dp }

class MainScreen : Screen {
    @Composable
    override fun Content() {
        val hazeState = remember { HazeState() }

        TabNavigator(HomeTab) {
            Scaffold(
                bottomBar = {
                    BottomBar(hazeState = hazeState)
                }
            ) { innerPadding ->
                CompositionLocalProvider(LocalBottomBarPadding provides innerPadding.calculateBottomPadding()) {
                    Box(
                        modifier = Modifier
                            .haze(state = hazeState)
                    ) {
                        CurrentTab()
                    }
                }
            }
        }
    }
}
