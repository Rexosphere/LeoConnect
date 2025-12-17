package com.rexosphere.leoconnect.presentation.encryption

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.rexosphere.leoconnect.presentation.MainScreen
import com.rexosphere.leoconnect.presentation.auth.OnboardingScreen

class EncryptionSetupScreen(
    private val needsOnboarding: Boolean
) : Screen {
    @Composable
    override fun Content() {
        val screenModel = koinScreenModel<EncryptionSetupScreenModel>()
        val state by screenModel.state.collectAsState()
        val navigator = LocalNavigator.currentOrThrow

        // Navigate when setup is complete
        LaunchedEffect(state) {
            if (state is EncryptionSetupState.Success) {
                if (needsOnboarding) {
                    navigator.replace(OnboardingScreen())
                } else {
                    navigator.replace(MainScreen())
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            when (val currentState = state) {
                is EncryptionSetupState.Loading,
                is EncryptionSetupState.GeneratingKeys,
                is EncryptionSetupState.UploadingKeys -> {
                    LoadingContent(currentState)
                }
                is EncryptionSetupState.KeyConflict -> {
                    KeyConflictContent(
                        hasLocalKeys = currentState.hasLocalKeys,
                        onUseNewKeys = screenModel::useNewKeys,
                        onUseExistingKeys = screenModel::useExistingKeys
                    )
                }
                is EncryptionSetupState.Error -> {
                    ErrorContent(
                        message = currentState.message,
                        onRetry = screenModel::retry
                    )
                }
                is EncryptionSetupState.Success -> {
                    // Will navigate via LaunchedEffect
                }
            }
        }
    }
}

@Composable
private fun LoadingContent(state: EncryptionSetupState) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(40.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Lock,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(24.dp))

        CircularProgressIndicator()

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = when (state) {
                is EncryptionSetupState.Loading -> "Setting up encryption..."
                is EncryptionSetupState.GeneratingKeys -> "Generating encryption keys..."
                is EncryptionSetupState.UploadingKeys -> "Syncing keys with server..."
                else -> "Please wait..."
            },
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "This ensures your messages are end-to-end encrypted",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun KeyConflictContent(
    hasLocalKeys: Boolean,
    onUseNewKeys: () -> Unit,
    onUseExistingKeys: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(40.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.error
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Encryption Key Conflict",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "You already have encryption keys on the server. Choose how to proceed:",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Option 1: Use New Keys
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Use New Keys (This Device)",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "⚠️ Warning: Your old encrypted messages will become unreadable on all devices.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onUseNewKeys,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Overwrite with New Keys")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Option 2: Use Existing Keys
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Use Existing Keys (Server)",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "✓ Recommended: Keep your existing messages readable. This device won't be able to decrypt messages until you transfer keys.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onUseExistingKeys,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Keep Existing Keys")
                }
            }
        }
    }
}

@Composable
private fun ErrorContent(
    message: String,
    onRetry: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(40.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.error
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Setup Failed",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onRetry,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Retry")
        }
    }
}
