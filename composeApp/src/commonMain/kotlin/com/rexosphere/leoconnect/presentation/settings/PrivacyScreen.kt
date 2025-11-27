package com.rexosphere.leoconnect.presentation.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow

class PrivacyScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        var profileVisibility by remember { mutableStateOf("Public") }
        var showEmail by remember { mutableStateOf(false) }
        var showPhoneNumber by remember { mutableStateOf(false) }
        var allowMessages by remember { mutableStateOf(true) }
        var showActivity by remember { mutableStateOf(true) }
        var allowTagging by remember { mutableStateOf(true) }
        var dataCollection by remember { mutableStateOf(true) }

        val visibilityOptions = listOf("Public", "Leo Members Only", "Private")
        var showVisibilityDialog by remember { mutableStateOf(false) }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Privacy & Security") },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Text(
                        text = "Profile Privacy",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        onClick = { showVisibilityDialog = true }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Visibility,
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Profile Visibility",
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                    Text(
                                        text = profileVisibility,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                item {
                    PrivacyToggleItem(
                        icon = Icons.Default.Email,
                        title = "Show Email Address",
                        description = "Display your email on your profile",
                        checked = showEmail,
                        onCheckedChange = { showEmail = it }
                    )
                }

                item {
                    PrivacyToggleItem(
                        icon = Icons.Default.Phone,
                        title = "Show Phone Number",
                        description = "Display your phone number on your profile",
                        checked = showPhoneNumber,
                        onCheckedChange = { showPhoneNumber = it }
                    )
                }

                item {
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                }

                item {
                    Text(
                        text = "Activity & Interactions",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                item {
                    PrivacyToggleItem(
                        icon = Icons.Default.Message,
                        title = "Allow Direct Messages",
                        description = "Let other Leo members send you messages",
                        checked = allowMessages,
                        onCheckedChange = { allowMessages = it }
                    )
                }

                item {
                    PrivacyToggleItem(
                        icon = Icons.Default.Timeline,
                        title = "Show Activity Status",
                        description = "Let others see when you're active",
                        checked = showActivity,
                        onCheckedChange = { showActivity = it }
                    )
                }

                item {
                    PrivacyToggleItem(
                        icon = Icons.Default.LocalOffer,
                        title = "Allow Tagging",
                        description = "Let others tag you in posts",
                        checked = allowTagging,
                        onCheckedChange = { allowTagging = it }
                    )
                }

                item {
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                }

                item {
                    Text(
                        text = "Data & Security",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                item {
                    PrivacyToggleItem(
                        icon = Icons.Default.Analytics,
                        title = "Usage Data Collection",
                        description = "Help improve LeoConnect by sharing anonymous usage data",
                        checked = dataCollection,
                        onCheckedChange = { dataCollection = it }
                    )
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.DeleteForever,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "Danger Zone",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                            Button(
                                onClick = { /* TODO: Clear all data */ },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.error
                                )
                            ) {
                                Text("Clear All Local Data")
                            }
                            OutlinedButton(
                                onClick = { /* TODO: Delete account */ },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = MaterialTheme.colorScheme.error
                                )
                            ) {
                                Text("Delete Account")
                            }
                        }
                    }
                }
            }
        }

        // Profile Visibility Dialog
        if (showVisibilityDialog) {
            AlertDialog(
                onDismissRequest = { showVisibilityDialog = false },
                icon = { Icon(Icons.Default.Visibility, contentDescription = null) },
                title = { Text("Profile Visibility") },
                text = {
                    Column {
                        visibilityOptions.forEach { option ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = profileVisibility == option,
                                    onClick = { profileVisibility = option }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(option, style = MaterialTheme.typography.bodyLarge)
                                    Text(
                                        text = when (option) {
                                            "Public" -> "Anyone can view your profile"
                                            "Leo Members Only" -> "Only verified Leo members can see your profile"
                                            "Private" -> "Only you can see your profile"
                                            else -> ""
                                        },
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showVisibilityDialog = false }) {
                        Text("Done")
                    }
                }
            )
        }
    }
}

@Composable
fun PrivacyToggleItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange
            )
        }
    }
}
