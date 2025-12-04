package com.rexosphere.leoconnect.presentation.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
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
import com.rexosphere.leoconnect.presentation.icons.Camera
import com.rexosphere.leoconnect.presentation.icons.ChevronLeft
import com.rexosphere.leoconnect.presentation.icons.User
import com.rexosphere.leoconnect.presentation.tabs.ProfileScreenModel
import com.rexosphere.leoconnect.presentation.tabs.ProfileUiState
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource

class EditProfileScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = koinScreenModel<ProfileScreenModel>()
        val state by screenModel.uiState.collectAsState()

        var displayName by remember { mutableStateOf("") }
        var bio by remember { mutableStateOf("") }
        var leoId by remember { mutableStateOf("") }
        var club by remember { mutableStateOf("") }
        var district by remember { mutableStateOf("") }

        // Initialize with current profile data
        LaunchedEffect(state) {
            if (state is ProfileUiState.Success) {
                val profile = (state as ProfileUiState.Success).profile
                displayName = profile.displayName
                leoId = profile.leoId ?: ""
                // Initialize other fields as needed
            }
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Edit Profile") },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(ChevronLeft, contentDescription = "Back")
                        }
                    },
                    actions = {
                        TextButton(onClick = {
                            // TODO: Save profile changes
                            navigator.pop()
                        }) {
                            Text("Save")
                        }
                    }
                )
            }
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Profile Photo Section
                item {
                    Box(contentAlignment = Alignment.BottomEnd) {
                        if (state is ProfileUiState.Success) {
                            val profile = (state as ProfileUiState.Success).profile
                            if (profile.photoURL != null) {
                                KamelImage(
                                    resource = { asyncPainterResource(data = profile.photoURL) },
                                    contentDescription = "Profile Picture",
                                    modifier = Modifier
                                        .size(120.dp)
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop,
                                    onLoading = { CircularProgressIndicator() },
                                    onFailure = { Icon(User, contentDescription = null) }
                                )
                            } else {
                                Icon(
                                    imageVector = User,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(120.dp)
                                        .clip(CircleShape)
                                )
                            }
                        }

                        FloatingActionButton(
                            onClick = { /* TODO: Change photo */ },
                            modifier = Modifier.size(40.dp),
                            containerColor = MaterialTheme.colorScheme.primary
                        ) {
                            Icon(
                                Camera,
                                contentDescription = "Change Photo",
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                item {
                    Text(
                        text = "Basic Information",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = displayName,
                        onValueChange = { displayName = it },
                        label = { Text("Display Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                item {
                    OutlinedTextField(
                        value = bio,
                        onValueChange = { bio = it },
                        label = { Text("Bio") },
                        placeholder = { Text("Tell others about yourself") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        maxLines = 5
                    )
                }

                item {
                    Text(
                        text = "Leo Information",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = leoId,
                        onValueChange = { leoId = it },
                        label = { Text("Leo ID") },
                        placeholder = { Text("Enter your Leo member ID") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                item {
                    OutlinedTextField(
                        value = club,
                        onValueChange = { club = it },
                        label = { Text("Leo Club") },
                        placeholder = { Text("Your Leo Club name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                item {
                    OutlinedTextField(
                        value = district,
                        onValueChange = { district = it },
                        label = { Text("District") },
                        placeholder = { Text("Your Leo District") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                item {
                    Button(
                        onClick = {
                            // TODO: Save changes
                            navigator.pop()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Save Changes")
                    }
                }
            }
        }
    }
}
