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
import androidx.compose.material.icons.filled.Check
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
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

class EditProfileScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = koinScreenModel<ProfileScreenModel>()
        val repository: com.rexosphere.leoconnect.domain.repository.LeoRepository = koinInject()
        val state by screenModel.uiState.collectAsState()
        val scope = rememberCoroutineScope()

        var bio by remember { mutableStateOf("") }
        var leoId by remember { mutableStateOf("") }
        var selectedClubId by remember { mutableStateOf<String?>(null) }
        var selectedDistrict by remember { mutableStateOf<String?>(null) }
        var districts by remember { mutableStateOf<List<String>>(emptyList()) }
        var clubs by remember { mutableStateOf<List<com.rexosphere.leoconnect.domain.model.Club>>(emptyList()) }
        var showDistrictDialog by remember { mutableStateOf(false) }
        var showClubDialog by remember { mutableStateOf(false) }
        var isSaving by remember { mutableStateOf(false) }

        // Initialize with current profile data
        LaunchedEffect(state) {
            if (state is ProfileUiState.Success) {
                val profile = (state as ProfileUiState.Success).profile
                bio = profile.bio ?: ""
                leoId = profile.leoId ?: ""
                selectedClubId = profile.assignedClubId
                isSaving = false
            } else if (state is ProfileUiState.Loading) {
                isSaving = true
            } else if (state is ProfileUiState.Error) {
                isSaving = false
            }
        }

        // Load districts
        LaunchedEffect(Unit) {
            repository.getDistricts().onSuccess {
                districts = it
            }
        }

        // Load clubs when district is selected
        LaunchedEffect(selectedDistrict) {
            selectedDistrict?.let { district ->
                repository.getClubsByDistrict(district).onSuccess {
                    clubs = it
                }
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
                        TextButton(
                            onClick = {
                                scope.launch {
                                    screenModel.updateProfile(
                                        leoId = if (leoId.isNotBlank()) leoId else null,
                                        assignedClubId = selectedClubId,
                                        bio = if (bio.isNotBlank()) bio else null
                                    )
                                    navigator.pop()
                                }
                            },
                            enabled = !isSaving
                        ) {
                            if (isSaving) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp))
                            } else {
                                Text("Save")
                            }
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
                        text = "Bio",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.fillMaxWidth()
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
                    OutlinedButton(
                        onClick = { showDistrictDialog = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = selectedDistrict ?: "Select District",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    OutlinedButton(
                        onClick = { showClubDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = selectedDistrict != null && clubs.isNotEmpty()
                    ) {
                        Text(
                            text = clubs.find { it.clubId == selectedClubId }?.name ?: "Select Club",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    Button(
                        onClick = {
                            scope.launch {
                                screenModel.updateProfile(
                                    leoId = if (leoId.isNotBlank()) leoId else null,
                                    assignedClubId = selectedClubId,
                                    bio = if (bio.isNotBlank()) bio else null
                                )
                                navigator.pop()
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isSaving
                    ) {
                        if (isSaving) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Text("Save Changes")
                        }
                    }
                }
            }
        }

        // District Selection Dialog
        if (showDistrictDialog) {
            AlertDialog(
                onDismissRequest = { showDistrictDialog = false },
                title = { Text("Select District") },
                text = {
                    LazyColumn {
                        items(districts.size) { index ->
                            val district = districts[index]
                            TextButton(
                                onClick = {
                                    selectedDistrict = district
                                    showDistrictDialog = false
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = district,
                                    modifier = Modifier.weight(1f)
                                )
                                if (selectedDistrict == district) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showDistrictDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        // Club Selection Dialog
        if (showClubDialog && selectedDistrict != null) {
            AlertDialog(
                onDismissRequest = { showClubDialog = false },
                title = { Text("Select Club") },
                text = {
                    if (clubs.isEmpty()) {
                        Text("No clubs available in this district")
                    } else {
                        LazyColumn {
                            items(clubs.size) { index ->
                                val club = clubs[index]
                                TextButton(
                                    onClick = {
                                        selectedClubId = club.clubId
                                        showClubDialog = false
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = club.name,
                                        modifier = Modifier.weight(1f)
                                    )
                                    if (selectedClubId == club.clubId) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Selected",
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showClubDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}
