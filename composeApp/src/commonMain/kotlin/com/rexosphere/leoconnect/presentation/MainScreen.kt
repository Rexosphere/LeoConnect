package com.rexosphere.leoconnect.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import com.rexosphere.leoconnect.presentation.navigation.BottomBar
import com.rexosphere.leoconnect.presentation.tabs.HomeTab

class MainScreen : Screen {
    @Composable
    override fun Content() {
        TabNavigator(HomeTab) {
            Scaffold(
                bottomBar = {
                    BottomBar()
                }
            ) { innerPadding ->
                Box(modifier = Modifier.padding(innerPadding)) {
                    CurrentTab()
                }
            }
        }
    }
}
