package com.rexosphere.leoconnect.presentation.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
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

class OnboardingScreen : Screen {
    @Composable
    override fun Content() {
        val screenModel = koinScreenModel<OnboardingScreenModel>()
        val state by screenModel.uiState.collectAsState()
        val navigator = LocalNavigator.currentOrThrow

        LaunchedEffect(state.isCompleted) {
            if (state.isCompleted) {
                navigator.replaceAll(MainScreen())
            }
        }

        OnboardingScreenContent(
            state = state,
            onLeoIdChange = screenModel::updateLeoId,
            onDistrictSelect = screenModel::selectDistrict,
            onClubSelect = screenModel::selectClub,
            onComplete = screenModel::completeOnboarding,
            onErrorDismiss = screenModel::clearError
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OnboardingScreenContent(
    state: OnboardingUiState,
    onLeoIdChange: (String) -> Unit,
    onDistrictSelect: (String) -> Unit,
    onClubSelect: (String) -> Unit,
    onComplete: () -> Unit,
    onErrorDismiss: () -> Unit
) {
    var showDistrictDialog by remember { mutableStateOf(false) }
    var showClubDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "Welcome to LeoConnect!",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Let's set up your profile",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(48.dp))

            // LEO ID Input
            OutlinedTextField(
                value = state.leoId,
                onValueChange = onLeoIdChange,
                label = { Text("LEO ID (Optional)") },
                placeholder = { Text("LEO123456") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading,
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            // District Selection
            OutlinedButton(
                onClick = { showDistrictDialog = true },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = !state.isLoading
            ) {
                Text(
                    text = state.selectedDistrict ?: "Select District",
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Start
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Club Selection (only show if district is selected)
            if (state.selectedDistrict != null) {
                OutlinedButton(
                    onClick = { showClubDialog = true },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    enabled = !state.isLoading && state.clubs.isNotEmpty()
                ) {
                    Text(
                        text = state.clubs.find { it.clubId == state.selectedClubId }?.name
                            ?: "Select Club (Optional)",
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Start
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Complete Button
            Button(
                onClick = onComplete,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !state.isLoading && (state.leoId.isNotBlank() || state.selectedClubId != null),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Complete Setup", fontSize = 16.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "You can update this information later in your profile settings",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }

    // District Selection Dialog
    if (showDistrictDialog) {
        AlertDialog(
            onDismissRequest = { showDistrictDialog = false },
            title = { Text("Select District") },
            text = {
                LazyColumn {
                    items(state.districts) { district ->
                        TextButton(
                            onClick = {
                                onDistrictSelect(district)
                                showDistrictDialog = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = district,
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Start
                            )
                            if (state.selectedDistrict == district) {
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
    if (showClubDialog && state.selectedDistrict != null) {
        AlertDialog(
            onDismissRequest = { showClubDialog = false },
            title = { Text("Select Club") },
            text = {
                if (state.clubs.isEmpty()) {
                    Text("No clubs available in this district")
                } else {
                    LazyColumn {
                        items(state.clubs) { club ->
                            TextButton(
                                onClick = {
                                    onClubSelect(club.clubId)
                                    showClubDialog = false
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = club.name,
                                    modifier = Modifier.weight(1f),
                                    textAlign = TextAlign.Start
                                )
                                if (state.selectedClubId == club.clubId) {
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

    // Error Snackbar
    state.error?.let { error ->
        Snackbar(
            modifier = Modifier.padding(16.dp),
            action = {
                TextButton(onClick = onErrorDismiss) {
                    Text("Dismiss")
                }
            }
        ) {
            Text(error)
        }
    }
}
