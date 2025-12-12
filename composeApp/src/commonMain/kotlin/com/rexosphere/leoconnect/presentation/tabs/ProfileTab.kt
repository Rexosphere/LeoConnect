package com.rexosphere.leoconnect.presentation.tabs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.rexosphere.leoconnect.presentation.LocalBottomBarPadding
import com.rexosphere.leoconnect.presentation.settings.SettingsScreen
import com.rexosphere.leoconnect.presentation.following.FollowingScreen
import com.rexosphere.leoconnect.presentation.myposts.MyPostsScreen
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import org.jetbrains.compose.resources.painterResource
import leoconnect.composeapp.generated.resources.Res
import leoconnect.composeapp.generated.resources.ic_leo_badge
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.navigator.Navigator
import com.rexosphere.leoconnect.domain.model.UserProfile
import com.rexosphere.leoconnect.presentation.icons.CalendarDays
import com.rexosphere.leoconnect.presentation.icons.CheckBadge
import com.rexosphere.leoconnect.presentation.icons.ChevronRight
import com.rexosphere.leoconnect.presentation.icons.Cog6Tooth
import com.rexosphere.leoconnect.presentation.icons.ExclamationTriangle
import com.rexosphere.leoconnect.presentation.icons.Heart
import com.rexosphere.leoconnect.presentation.icons.User
import com.rexosphere.leoconnect.presentation.icons.Users
import com.rexosphere.leoconnect.presentation.icons.Newspaper

object ProfileTab : Tab {
    override val options: TabOptions
        @Composable get() {
            val icon = rememberVectorPainter(User)
            return TabOptions(
                index = 3u,
                title = "Profile",
                icon = icon
            )
        }

    @Composable override fun Content() {
        Navigator(ProfileScreen())
    }
}

class ProfileScreen : Screen {
    @Composable
    override fun Content() {
        val screenModel = koinScreenModel<ProfileScreenModel>()
        val state by screenModel.uiState.collectAsState()
        val navigator = LocalNavigator.currentOrThrow
        val bottomBarPadding = LocalBottomBarPadding.current

        when (val uiState = state) {
            is ProfileUiState.Loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is ProfileUiState.Error -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(ExclamationTriangle, null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.error)
                        Spacer(Modifier.height(16.dp))
                        Text(uiState.message, color = MaterialTheme.colorScheme.error)
                        Spacer(Modifier.height(16.dp))
                        Button(onClick = { screenModel.loadProfile() }) {
                            Text("Retry")
                        }
                    }
                }
            }
            is ProfileUiState.Success -> {
                val profile = uiState.profile
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = bottomBarPadding + 16.dp)
                ) {
                    item { ProfileHeader(profile, onSettingsClick = { navigator.push(SettingsScreen()) }) }
                    item { Spacer(Modifier.height(32.dp)) }

                    // Menu Items
                    if (profile.leoId.isNullOrEmpty()) {
                        item {
                            ProfileMenuItem(
                                icon = CheckBadge,
                                title = "Verify Leo ID",
                                subtitle = "Prove you're a real Leo member",
                                badge = "New",
                                onClick = { navigator.push(com.rexosphere.leoconnect.presentation.verifyleoid.VerifyLeoIdScreen()) }
                            )
                        }
                    }


                    item {
                        ProfileMenuItem(
                            icon = Heart,
                            title = "Following",
                            subtitle = "${profile.followingClubs.size} clubs • ${profile.followingCount ?: 0} users",
                            onClick = { navigator.push(FollowingScreen(profile.uid)) }
                        )
                    }

                    item {
                        ProfileMenuItem(
                            icon = Newspaper,
                            title = "My Posts",
                            subtitle = "View and manage your posts",
                            onClick = { navigator.push(MyPostsScreen(profile.uid)) }
                        )
                    }

                    item { Spacer(Modifier.height(50.dp)) }
                }
            }
        }
    }
}

@Composable
private fun ProfileHeader(profile: UserProfile, onSettingsClick: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // Top-right Settings
        Box(modifier = Modifier.fillMaxWidth()) {
            IconButton(
                onClick = onSettingsClick,
                modifier = Modifier.align(Alignment.TopEnd).padding(16.dp)
            ) {
                Icon(Cog6Tooth, contentDescription = "Settings")
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth().padding(top = 20.dp)
        ) {
            // Profile Picture
            if (profile.photoURL != null) {
                KamelImage(
                    resource = asyncPainterResource(profile.photoURL),
                    contentDescription = "Profile picture",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop,
                    onLoading = { CircularProgressIndicator(strokeWidth = 3.dp) },
                    onFailure = {
                        Box(Modifier.background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)) {
                            Icon(User, null, modifier = Modifier.size(50.dp).align(Alignment.Center))
                        }
                    }
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(User, null, modifier = Modifier.size(50.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Spacer(Modifier.height(20.dp))

            // Name + Verified Badge
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = profile.displayName,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp
                    )
                )
                if (!profile.leoId.isNullOrEmpty()) {
                    Spacer(Modifier.width(8.dp))
                    Icon(
                        CheckBadge,
                        contentDescription = "Verified Leo",
                        tint = Color(0xFF1DA1F2),
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Text(
                text = profile.email,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(12.dp))

            // Leo ID Badge
            if (!profile.leoId.isNullOrEmpty()) {
                Surface(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_leo_badge), // Optional custom badge
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "Leo ID: ${profile.leoId}",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            if (profile.isWebmaster) {
                Spacer(Modifier.height(8.dp))
                Surface(color = Color(0xFFFF6B6B).copy(alpha = 0.15f), shape = RoundedCornerShape(20.dp)) {
                    Text(
                        text = "Webmaster",
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                        color = Color(0xFFFF6B6B),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileMenuItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    badge: String? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
        }

        Spacer(Modifier.width(20.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
                badge?.let {
                    Spacer(Modifier.width(8.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = it,
                            color = Color.White,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }
            }
            Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        Icon(
            ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}