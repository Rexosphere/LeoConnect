package com.rexosphere.leoconnect.presentation.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.DividerDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.rexosphere.leoconnect.presentation.auth.LoginScreen
import com.rexosphere.leoconnect.presentation.tabs.ProfileScreenModel

class SettingsScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = koinScreenModel<ProfileScreenModel>() // Reuse ProfileScreenModel for sign out

        var showAboutDialog by remember { mutableStateOf(false) }
        var showLanguageDialog by remember { mutableStateOf(false) }
        var showDataUsageDialog by remember { mutableStateOf(false) }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Settings") },
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
            ) {
                // Account Section
                item {
                    SettingsSectionHeader(title = "Account")
                }
                item {
                    SettingsItem(
                        icon = Icons.Default.Person,
                        title = "Edit Profile",
                        subtitle = "Update your profile information",
                        onClick = { navigator.push(EditProfileScreen()) }
                    )
                }
                item {
                    SettingsItem(
                        icon = Icons.Default.Notifications,
                        title = "Notifications",
                        subtitle = "Manage notification preferences",
                        onClick = { navigator.push(NotificationsScreen()) }
                    )
                }
                item {
                    SettingsItem(
                        icon = Icons.Default.Security,
                        title = "Privacy & Security",
                        subtitle = "Control your privacy settings",
                        onClick = { navigator.push(PrivacyScreen()) }
                    )
                }

                // App Section
                item {
                    SettingsSectionHeader(title = "App")
                }
                item {
                    SettingsItem(
                        icon = Icons.Default.Palette,
                        title = "Appearance",
                        subtitle = "Dark mode and theme settings",
                        onClick = { navigator.push(AppearanceScreen()) }
                    )
                }
                item {
                    SettingsItem(
                        icon = Icons.Default.Language,
                        title = "Language",
                        subtitle = "English",
                        onClick = { showLanguageDialog = true }
                    )
                }
                item {
                    SettingsItem(
                        icon = Icons.Default.DataUsage,
                        title = "Data Usage",
                        subtitle = "Manage data and storage",
                        onClick = { showDataUsageDialog = true }
                    )
                }

                // Support Section
                item {
                    SettingsSectionHeader(title = "Support")
                }
                item {
                    SettingsItem(
                        icon = Icons.Default.Help,
                        title = "Help & Support",
                        subtitle = "Get help and send feedback",
                        onClick = { navigator.push(HelpScreen()) }
                    )
                }
                item {
                    SettingsItem(
                        icon = Icons.Default.Info,
                        title = "About",
                        subtitle = "Version 1.0.0",
                        onClick = { showAboutDialog = true }
                    )
                }
                item {
                    SettingsItem(
                        icon = Icons.Default.Policy,
                        title = "Terms & Privacy Policy",
                        onClick = { navigator.push(TermsScreen()) }
                    )
                }

                // Sign Out
                item {
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                }
                item {
                    SettingsItem(
                        icon = Icons.Default.ExitToApp,
                        title = "Sign Out",
                        onClick = {
                            screenModel.signOut()
                            // Navigate to the root navigator and replace with login screen
                            var rootNavigator = navigator
                            while (rootNavigator.parent != null) {
                                rootNavigator = rootNavigator.parent!!
                            }
                            rootNavigator.replaceAll(LoginScreen())
                        },
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }

        // Dialogs
        if (showAboutDialog) {
            AboutDialog(onDismiss = { showAboutDialog = false })
        }

        if (showLanguageDialog) {
            LanguageDialog(onDismiss = { showLanguageDialog = false })
        }

        if (showDataUsageDialog) {
            DataUsageDialog(onDismiss = { showDataUsageDialog = false })
        }
    }
}

@Composable
fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
    )
}

@Composable
fun SettingsItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit,
    color: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = color
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun AboutDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(Icons.Default.Info, contentDescription = null) },
        title = { Text("About LeoConnect") },
        text = {
            Column {
                Text("LeoConnect - Connect with Leo Clubs", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.padding(8.dp))
                Text("Version: 1.0.0")
                Text("Build: 1")
                Spacer(modifier = Modifier.padding(8.dp))
                Text("A platform to connect Leo members, share activities, and collaborate on projects.")
                Spacer(modifier = Modifier.padding(8.dp))
                Text("© 2024 LeoConnect. All rights reserved.")
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("OK")
            }
        }
    )
}

@Composable
fun LanguageDialog(onDismiss: () -> Unit) {
    var selectedLanguage by remember { mutableStateOf("English") }
    val languages = listOf("English", "Spanish", "French", "German", "Chinese", "Japanese")

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(Icons.Default.Language, contentDescription = null) },
        title = { Text("Select Language") },
        text = {
            Column {
                languages.forEach { language ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedLanguage = language }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedLanguage == language,
                            onClick = { selectedLanguage = language }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(language)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun DataUsageDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(Icons.Default.DataUsage, contentDescription = null) },
        title = { Text("Data Usage") },
        text = {
            Column {
                Text("Storage Information", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.padding(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween
                ) {
                    Text("Cache")
                    Text("12.5 MB")
                }
                Spacer(modifier = Modifier.padding(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween
                ) {
                    Text("Images")
                    Text("45.2 MB")
                }
                Spacer(modifier = Modifier.padding(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween
                ) {
                    Text("Total")
                    Text("57.7 MB", fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.padding(12.dp))
                Button(
                    onClick = { /* TODO: Clear cache */ },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Clear Cache")
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}
