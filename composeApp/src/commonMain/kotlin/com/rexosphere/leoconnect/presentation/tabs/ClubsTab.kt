package com.rexosphere.leoconnect.presentation.tabs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.rexosphere.leoconnect.domain.model.Club
import com.rexosphere.leoconnect.presentation.LocalBottomBarPadding
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import com.rexosphere.leoconnect.presentation.icons.Check
import com.rexosphere.leoconnect.presentation.icons.DocumentMagnifyingGlass
import com.rexosphere.leoconnect.presentation.icons.ExclamationTriangle
import com.rexosphere.leoconnect.presentation.icons.Heart
import com.rexosphere.leoconnect.presentation.icons.MagnifyingGlass
import com.rexosphere.leoconnect.presentation.icons.UserGroup
import com.rexosphere.leoconnect.presentation.icons.Users
import com.rexosphere.leoconnect.presentation.search.SearchScreen
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeChild
import dev.chrisbanes.haze.materials.HazeMaterials

object ClubsTab : Tab {
    override val options: TabOptions
        @Composable get() {
            val icon = rememberVectorPainter(UserGroup)
            return TabOptions(
                index = 1u,
                title = "Clubs",
                icon = icon
            )
        }

    @Composable override fun Content() {
        Navigator(ClubsScreen())
    }
}

@OptIn(ExperimentalMaterial3Api::class)
class ClubsScreen : Screen {
    @Composable
    override fun Content() {
        val screenModel = koinScreenModel<ClubsScreenModel>()
        val state by screenModel.uiState.collectAsState()
        val bottomBarPadding = LocalBottomBarPadding.current
        val hazeState = remember { HazeState() }
        val navigator = LocalNavigator.currentOrThrow

        Scaffold(
            topBar = {
                // 1. Capture the color outside the draw scope
                val borderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)

                TopAppBar(
                    title = {
                        Text(
                            "Clubs",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    },
                    actions = {
                        IconButton(onClick = { navigator.push(SearchScreen()) }) {
                            Icon(MagnifyingGlass, "Search")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        scrolledContainerColor = Color.Transparent
                    ),
                    modifier = Modifier
                        .hazeChild(
                            state = hazeState,
                            style = HazeMaterials.thin(MaterialTheme.colorScheme.surface)
                        )
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.2f))
                        .drawBehind {
                            val strokeWidth = 1.dp.toPx()
                            val y = size.height - strokeWidth

                            drawLine(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        borderColor, // 2. Use the captured value here
                                        Color.Transparent
                                    )
                                ),
                                start = Offset(0f, y),
                                end = Offset(size.width, y),
                                strokeWidth = strokeWidth
                            )
                        }
                )
            },
            containerColor = Color.Transparent
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .haze(state = hazeState)
            ) {
                when (val uiState = state) {
                    is ClubsUiState.Loading -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }

                    is ClubsUiState.Error -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    ExclamationTriangle,
                                    null,
                                    modifier = Modifier.size(64.dp),
                                    tint = MaterialTheme.colorScheme.error
                                )
                                Spacer(Modifier.height(16.dp))
                                Text(uiState.message, color = MaterialTheme.colorScheme.error)
                            }
                        }
                    }

                    is ClubsUiState.Success -> {
                        LazyColumn(
                            contentPadding = PaddingValues(bottom = bottomBarPadding + 16.dp)
                        ) {
                            // District Filter Chips
                            item {
                                LazyRow(
                                    contentPadding = PaddingValues(
                                        horizontal = 16.dp,
                                        vertical = 12.dp
                                    ),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    item {
                                        FilterChip(
                                            selected = uiState.selectedDistrict == null,
                                            onClick = { screenModel.selectDistrict(null) },
                                            label = { Text("All Districts") },
                                            leadingIcon = if (uiState.selectedDistrict == null) {
                                                {
                                                    Icon(
                                                        Check,
                                                        null,
                                                        modifier = Modifier.size(18.dp)
                                                    )
                                                }
                                            } else null
                                        )
                                    }
                                    items(uiState.districts) { district ->
                                        FilterChip(
                                            selected = district == uiState.selectedDistrict,
                                            onClick = { screenModel.selectDistrict(district) },
                                            label = { Text(district) }
                                        )
                                    }
                                }
                            }

                            // Clubs List
                            if (uiState.clubs.isEmpty()) {
                                item {
                                    Box(
                                        modifier = Modifier.fillParentMaxHeight(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Icon(
                                                DocumentMagnifyingGlass,
                                                null,
                                                modifier = Modifier.size(80.dp),
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                                                    alpha = 0.6f
                                                )
                                            )
                                            Spacer(Modifier.height(24.dp))
                                            Text(
                                                text = if (uiState.selectedDistrict == null)
                                                    "No clubs yet" else "No clubs in ${uiState.selectedDistrict}",
                                                style = MaterialTheme.typography.titleLarge,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            Text(
                                                text = "Be the first to create one!",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                                                    alpha = 0.8f
                                                ),
                                                modifier = Modifier.padding(top = 8.dp)
                                            )
                                        }
                                    }
                                }
                            } else {
                                items(uiState.clubs, key = { it.id }) { club ->
                                    ThreadsStyleClubItem(club = club)
                                    Divider(
                                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                        thickness = 1.dp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ThreadsStyleClubItem(club: Club) {
    val navigator = LocalNavigator.currentOrThrow
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { navigator.push(com.rexosphere.leoconnect.presentation.clubdetail.ClubDetailScreen(club)) }
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Club Logo
        if (club.logoUrl != null) {
            KamelImage(
                resource = asyncPainterResource(club.logoUrl),
                contentDescription = "Club logo",
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
                onLoading = {
                    Box(Modifier.background(MaterialTheme.colorScheme.surfaceVariant, CircleShape))
                },
                onFailure = {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(UserGroup, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            )
        } else {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(UserGroup, null, modifier = Modifier.size(32.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        Spacer(Modifier.width(16.dp))

        // Club Info
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = club.name,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp
                )
            )
            Text(
                text = club.district,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary
            )
            if (!club.description.isNullOrBlank()) {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = club.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2
                )
            }

            Spacer(Modifier.height(8.dp))

            // Stats
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Users, null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "${club.membersCount} members",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Heart, null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "${club.followersCount} followers",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Follow Button (optional)
        // OutlinedButton(onClick = { }, modifier = Modifier.height(36.dp)) {
        //     Text("Follow")
        // }
    }
}