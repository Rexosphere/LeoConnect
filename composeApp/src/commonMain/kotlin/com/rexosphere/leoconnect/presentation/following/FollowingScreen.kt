package com.rexosphere.leoconnect.presentation.following

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.rexosphere.leoconnect.presentation.icons.ChevronLeft
import com.rexosphere.leoconnect.presentation.icons.ExclamationTriangle
import com.rexosphere.leoconnect.presentation.icons.User
import com.rexosphere.leoconnect.presentation.clubdetail.ClubDetailScreen
import com.rexosphere.leoconnect.presentation.userprofile.UserProfileScreen
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource

data class FollowingScreen(val userId: String) : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val screenModel = koinScreenModel<FollowingScreenModel>()
        val state by screenModel.uiState.collectAsState()
        val navigator = LocalNavigator.currentOrThrow

        LaunchedEffect(userId) {
            screenModel.loadFollowing(userId)
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Following") },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(ChevronLeft, contentDescription = "Back")
                        }
                    }
                )
            }
        ) { paddingValues ->
            when (val uiState = state) {
                is FollowingUiState.Loading -> {
                    Box(Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is FollowingUiState.Error -> {
                    Box(Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(ExclamationTriangle, null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.error)
                            Spacer(Modifier.height(16.dp))
                            Text(uiState.message, color = MaterialTheme.colorScheme.error)
                            Spacer(Modifier.height(16.dp))
                            Button(onClick = { screenModel.loadFollowing(userId) }) {
                                Text("Retry")
                            }
                        }
                    }
                }
                is FollowingUiState.Success -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(paddingValues),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        // Following Clubs Section
                        if (uiState.clubs.isNotEmpty()) {
                            item {
                                Text(
                                    "Clubs",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }
                            items(uiState.clubs) { club ->
                                ClubItem(
                                    name = club.name,
                                    logoUrl = club.logoUrl,
                                    district = club.district,
                                    onClick = { navigator.push(ClubDetailScreen(club)) }
                                )
                            }
                            item { Spacer(Modifier.height(24.dp)) }
                        }

                        // Following Users Section
                        if (uiState.users.isNotEmpty()) {
                            item {
                                Text(
                                    "Users",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }
                            items(uiState.users) { user ->
                                UserItem(
                                    displayName = user.displayName,
                                    photoUrl = user.photoURL,
                                    leoId = user.leoId,
                                    onClick = { navigator.push(UserProfileScreen(user.uid)) }
                                )
                            }
                        }

                        // Empty State
                        if (uiState.clubs.isEmpty() && uiState.users.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        "Not following anyone yet",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
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
private fun ClubItem(
    name: String,
    logoUrl: String?,
    district: String?,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (logoUrl != null) {
            KamelImage(
                resource = asyncPainterResource(logoUrl),
                contentDescription = "Club logo",
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
                onLoading = { CircularProgressIndicator(strokeWidth = 2.dp) },
                onFailure = {
                    Box(Modifier.background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)) {
                        Icon(User, null, modifier = Modifier.size(24.dp).align(Alignment.Center))
                    }
                }
            )
        } else {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(User, null, modifier = Modifier.size(24.dp))
            }
        }

        Spacer(Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
            if (district != null) {
                Text(
                    district,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun UserItem(
    displayName: String,
    photoUrl: String?,
    leoId: String?,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (photoUrl != null) {
            KamelImage(
                resource = asyncPainterResource(photoUrl),
                contentDescription = "Profile picture",
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
                onLoading = { CircularProgressIndicator(strokeWidth = 2.dp) },
                onFailure = {
                    Box(Modifier.background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)) {
                        Icon(User, null, modifier = Modifier.size(24.dp).align(Alignment.Center))
                    }
                }
            )
        } else {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(User, null, modifier = Modifier.size(24.dp))
            }
        }

        Spacer(Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(displayName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
            if (leoId != null) {
                Text(
                    "Leo ID: $leoId",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
