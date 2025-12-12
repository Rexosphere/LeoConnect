package com.rexosphere.leoconnect.presentation.verifyleoid

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.rexosphere.leoconnect.presentation.icons.CheckBadge
import com.rexosphere.leoconnect.presentation.icons.ChevronLeft
import com.rexosphere.leoconnect.presentation.icons.User
import com.rexosphere.leoconnect.presentation.icons.BuildingOffice2

class VerifyLeoIdScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Verify Leo ID") },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(ChevronLeft, contentDescription = "Back")
                        }
                    }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Icon
                Icon(
                    CheckBadge,
                    contentDescription = null,
                    modifier = Modifier.size(80.dp),
                    tint = MaterialTheme.colorScheme.primary
                )

                Spacer(Modifier.height(24.dp))

                Text(
                    "Get Verified as a Leo Member",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(16.dp))

                Text(
                    "Verification proves you're a real Leo member and unlocks full access to create posts and events.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.height(32.dp))

                // Instructions Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                User,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Spacer(Modifier.width(12.dp))
                            Text(
                                "For Existing Members",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }

                        Spacer(Modifier.height(16.dp))

                        Text(
                            "Contact the admin to verify your Leo ID. You'll need to provide:",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )

                        Spacer(Modifier.height(12.dp))

                        BulletPoint("Your full name")
                        BulletPoint("Your Leo ID number")
                        BulletPoint("Your assigned Leo club")
                        BulletPoint("Proof of membership (if requested)")

                        Spacer(Modifier.height(16.dp))

                        Text(
                            "Once verified, you'll be able to create posts and events on LeoConnect.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                // New Members Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                BuildingOffice2,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                tint = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Spacer(Modifier.width(12.dp))
                            Text(
                                "For New Members",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }

                        Spacer(Modifier.height(16.dp))

                        Text(
                            "If you're new to Leo Club, visit your nearest Leo Club office to get yourself registered.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )

                        Spacer(Modifier.height(12.dp))

                        Text(
                            "Steps to register:",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )

                        Spacer(Modifier.height(8.dp))

                        BulletPoint("Find your nearest Leo Club office", MaterialTheme.colorScheme.onSecondaryContainer)
                        BulletPoint("Fill out the membership registration form", MaterialTheme.colorScheme.onSecondaryContainer)
                        BulletPoint("Pay the membership fee (if applicable)", MaterialTheme.colorScheme.onSecondaryContainer)
                        BulletPoint("Receive your Leo ID", MaterialTheme.colorScheme.onSecondaryContainer)
                        BulletPoint("Return to LeoConnect and contact admin for verification", MaterialTheme.colorScheme.onSecondaryContainer)
                    }
                }

                Spacer(Modifier.height(32.dp))

                // Note
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            "📝 Note",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Until you're verified, you can still browse posts, follow clubs and users, and participate in the community. You just won't be able to create your own posts or events.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BulletPoint(
    text: String,
    color: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onPrimaryContainer
) {
    Row(
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Text(
            "• ",
            style = MaterialTheme.typography.bodyMedium,
            color = color
        )
        Text(
            text,
            style = MaterialTheme.typography.bodyMedium,
            color = color
        )
    }
}
