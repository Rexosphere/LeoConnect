package com.rexosphere.leoconnect.presentation.createpost

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.rexosphere.leoconnect.presentation.components.Base64Image
import com.rexosphere.leoconnect.presentation.components.rememberImagePicker
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
        var selectedImages by remember { mutableStateOf<List<String>>(emptyList()) }
        var imagePickerError by remember { mutableStateOf<String?>(null) }

        // Image picker launcher
        val imagePicker = rememberImagePicker(
            onImageSelected = { base64 ->
                if (selectedImages.size < 4) {
                    selectedImages = selectedImages + base64!!
                    imagePickerError = null
                } else {
                    imagePickerError = "Maximum 4 images allowed"
                }
            },
            onError = { error ->
                imagePickerError = error
            }
        )

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
                                    imagesList = selectedImages
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
                // Error messages
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

                // Image picker error
                if (imagePickerError != null) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = imagePickerError!!,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(onClick = { imagePickerError = null }) {
                                Icon(
                                    XMark,
                                    contentDescription = "Dismiss",
                                    tint = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                        }
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

                // Images preview section
                if (selectedImages.isNotEmpty()) {
                    // Grid layout for images
                    val rows = (selectedImages.size + 1) / 2
                    Column(modifier = Modifier.fillMaxWidth()) {
                        for (rowIndex in 0 until rows) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                for (colIndex in 0 until 2) {
                                    val imageIndex = rowIndex * 2 + colIndex
                                    if (imageIndex < selectedImages.size) {
                                        Card(
                                            modifier = Modifier
                                                .weight(1f)
                                                .aspectRatio(1f),
                                            shape = RoundedCornerShape(12.dp)
                                        ) {
                                            Box(modifier = Modifier.fillMaxSize()) {
                                                Base64Image(
                                                    base64String = selectedImages[imageIndex],
                                                    contentDescription = "Selected image ${imageIndex + 1}",
                                                    modifier = Modifier.fillMaxSize(),
                                                    contentScale = ContentScale.Crop
                                                )

                                                // Remove button
                                                IconButton(
                                                    onClick = {
                                                        selectedImages = selectedImages.filterIndexed { index, _ -> index != imageIndex }
                                                    },
                                                    modifier = Modifier
                                                        .align(Alignment.TopEnd)
                                                        .padding(4.dp),
                                                    colors = IconButtonDefaults.iconButtonColors(
                                                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
                                                    )
                                                ) {
                                                    Icon(
                                                        XMark,
                                                        contentDescription = "Remove image",
                                                        tint = MaterialTheme.colorScheme.onSurface,
                                                        modifier = Modifier.size(20.dp)
                                                    )
                                                }
                                            }
                                        }
                                    } else {
                                        Spacer(modifier = Modifier.weight(1f))
                                    }
                                }
                            }
                            if (rowIndex < rows - 1) {
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Add image button
                OutlinedButton(
                    onClick = {
                        imagePicker.launch()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = selectedImages.size < 4
                ) {
                    Icon(
                        Photo,
                        contentDescription = "Add photo",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        when {
                            selectedImages.isEmpty() -> "Add Photos (up to 4)"
                            selectedImages.size < 4 -> "Add More Photos (${selectedImages.size}/4)"
                            else -> "Maximum Photos Reached (4/4)"
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Helper text
                Text(
                    text = "Your post will be shared with your Leo club community",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (selectedImages.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Each image will be compressed to under 2MB",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
