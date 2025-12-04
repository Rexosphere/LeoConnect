package com.rexosphere.leoconnect.presentation.createpost

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.rexosphere.leoconnect.presentation.icons.ChevronLeft
import com.rexosphere.leoconnect.presentation.icons.Photo
import com.rexosphere.leoconnect.presentation.icons.XMark

class CreatePostScreen : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = koinScreenModel<CreatePostScreenModel>()
        val uiState by screenModel.uiState.collectAsState()

        var content by remember { mutableStateOf("") }
        var selectedImageBase64 by remember { mutableStateOf<String?>(null) }
        var showImagePicker by remember { mutableStateOf(false) }

        // Handle success state
        LaunchedEffect(uiState) {
            if (uiState is CreatePostUiState.Success) {
                navigator.pop()
                screenModel.resetState()
            }
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Create Post") },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(ChevronLeft, contentDescription = "Back")
                        }
                    },
                    actions = {
                        val isLoading = uiState is CreatePostUiState.Loading
                        TextButton(
                            onClick = {
                                screenModel.createPost(
                                    content = content,
                                    imageBytes = selectedImageBase64,
                                    clubId = null,
                                    clubName = null
                                )
                            },
                            enabled = content.isNotBlank() && !isLoading
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text("Post")
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.onSurface
                    )
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Error message
                if (uiState is CreatePostUiState.Error) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Text(
                            text = (uiState as CreatePostUiState.Error).message,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }

                // Content TextField
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    placeholder = { Text("What's on your mind?") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    ),
                    maxLines = 10
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Image section
                if (selectedImageBase64 != null) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            // Display selected image preview here
                            // For now, just show a placeholder
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Image Selected")
                            }

                            // Remove button
                            IconButton(
                                onClick = { selectedImageBase64 = null },
                                modifier = Modifier.align(Alignment.TopEnd)
                            ) {
                                Icon(
                                    XMark,
                                    contentDescription = "Remove image",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Add image button
                OutlinedButton(
                    onClick = {
                        // TODO: Implement image picker
                        // For now, just show a message
                        showImagePicker = true
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        Photo,
                        contentDescription = "Add photo",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add Photo")
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Helper text
                Text(
                    text = "Your post will be shared with your Leo club community",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Image picker dialog (placeholder)
        if (showImagePicker) {
            AlertDialog(
                onDismissRequest = { showImagePicker = false },
                title = { Text("Image Picker") },
                text = { Text("Image picker functionality will be implemented in platform-specific code. For now, you can create text-only posts.") },
                confirmButton = {
                    TextButton(onClick = { showImagePicker = false }) {
                        Text("OK")
                    }
                }
            )
        }
    }
}
