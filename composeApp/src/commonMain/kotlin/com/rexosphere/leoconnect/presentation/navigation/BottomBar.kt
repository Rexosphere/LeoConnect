package com.rexosphere.leoconnect.presentation.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import com.rexosphere.leoconnect.presentation.createpost.CreatePostScreen
import com.rexosphere.leoconnect.presentation.icons.Plus
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeChild
import dev.chrisbanes.haze.materials.HazeMaterials

@Composable
fun BottomBar(hazeState: HazeState) {
    // 1. Define the shape once to reuse for clipping, haze, and border
    val barShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)

    // 2. Capture colors for the border gradient
    val borderBrush = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f), // Top highlight (Light edge)
            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.02f) // Fades out at bottom
        )
    )

    NavigationBar(
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onSurface,
        // 3. CRITICAL: Remove default M3 tonal elevation to prevent opaque overlay
        tonalElevation = 0.dp,
        modifier = Modifier
            // 4. Add a soft shadow for depth
            .shadow(
                elevation = 12.dp,
                shape = barShape,
                spotColor = Color.Black.copy(alpha = 0.15f),
                ambientColor = Color.Black.copy(alpha = 0.15f)
            )
            // 5. Clip explicitly before haze
            .clip(barShape)
            .hazeChild(
                state = hazeState,
                shape = barShape,
                style = HazeMaterials.thin(MaterialTheme.colorScheme.surface)
            )
            // 6. Subtle background tint for readability
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.1f))
            .border(
                width = 1.dp,
                brush = borderBrush,
                shape = barShape
            )
    ) {
        TabNavigationItem(com.rexosphere.leoconnect.presentation.tabs.HomeTab)
        TabNavigationItem(com.rexosphere.leoconnect.presentation.tabs.ClubsTab)
        CreatePostNavItem(hazeState = hazeState)
        TabNavigationItem(com.rexosphere.leoconnect.presentation.tabs.MessagesTabNavigator)
        TabNavigationItem(com.rexosphere.leoconnect.presentation.tabs.ProfileTab)
    }
}

@Composable
private fun RowScope.TabNavigationItem(tab: Tab) {
    val tabNavigator = LocalTabNavigator.current
    val selected = tabNavigator.current == tab

    NavigationBarItem(
        selected = selected,
        onClick = { tabNavigator.current = tab },
        icon = {
            tab.options.icon?.let { icon ->
                Icon(
                    painter = icon,
                    contentDescription = tab.options.title,
                    // Optional: Scale effect on selection
                    modifier = Modifier.scale(if (selected) 1.1f else 1.0f)
                )
            }
        },
        label = {
            Text(
                text = tab.options.title,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
            )
        },
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = MaterialTheme.colorScheme.primary,
            selectedTextColor = MaterialTheme.colorScheme.primary,
            // Keep indicator transparent for the clean glass look
            indicatorColor = Color.Transparent,
            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
        )
    )
}

@Composable
private fun RowScope.CreatePostNavItem(hazeState: HazeState) {
    val navigator = LocalNavigator.currentOrThrow.parent ?: LocalNavigator.currentOrThrow

    Box(
        modifier = Modifier
            .weight(1f),
        contentAlignment = Alignment.Center
    ) {
        FloatingActionButton(
            onClick = { navigator.push(CreatePostScreen()) },
            shape = CircleShape,
            containerColor = Color.Transparent,
            elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 0.dp, pressedElevation = 0.dp),
            modifier = Modifier
                .size(56.dp)
                .shadow(
                    elevation = 8.dp,
                    shape = CircleShape,
                    spotColor = Color.Black.copy(alpha = 0.25f),
                    ambientColor = Color.Black.copy(alpha = 0.25f)
                )
                .clip(CircleShape)
                .hazeChild(
                    state = hazeState,
                    shape = CircleShape,
                    style = HazeMaterials.thin(MaterialTheme.colorScheme.surface)
                )
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.1f))
                .border(
                    width = 1.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.4f),
                            Color.White.copy(alpha = 0.1f),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
        ) {
            Icon(
                imageVector = Plus,
                contentDescription = "Create Post",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}