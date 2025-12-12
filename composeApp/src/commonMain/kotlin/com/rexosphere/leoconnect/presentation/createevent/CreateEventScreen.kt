package com.rexosphere.leoconnect.presentation.createevent

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
import com.rexosphere.leoconnect.presentation.components.rememberImagePicker
import com.rexosphere.leoconnect.presentation.icons.ChevronLeft
import com.rexosphere.leoconnect.presentation.icons.Photo
import com.rexosphere.leoconnect.presentation.icons.XMark
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

class CreateEventScreen : Screen {

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = koinScreenModel<CreateEventScreenModel>()
        val uiState by screenModel.uiState.collectAsState()

        var name by remember { mutableStateOf("") }
        var description by remember { mutableStateOf("") }
        var eventDate by remember { mutableStateOf("") }
        var selectedImageBase64 by remember { mutableStateOf<String?>(null) }
        var imagePickerError by remember { mutableStateOf<String?>(null) }
        var showDatePicker by remember { mutableStateOf(false) }

        // Image picker launcher
        val imagePicker = rememberImagePicker(
            onImageSelected = { base64 ->
                selectedImageBase64 = base64
                imagePickerError = null
            },
            onError = { error ->
                imagePickerError = error
            }
        )

        // Handle success state
        LaunchedEffect(uiState) {
            if (uiState is CreateEventUiState.Success) {
                navigator.pop()
                screenModel.resetState()
            }
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Create Event") },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(ChevronLeft, contentDescription = "Back")
                        }
                    },
                    actions = {
                        val isLoading = uiState is CreateEventUiState.Loading
                        TextButton(
                            onClick = {
                                screenModel.createEvent(
                                    name = name,
                                    description = description,
                                    eventDate = eventDate,
                                    imageBytes = selectedImageBase64
                                )
                            },
                            enabled = name.isNotBlank() && description.isNotBlank() && eventDate.isNotBlank() && !isLoading
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text("Create")
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
                if (uiState is CreateEventUiState.Error) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Text(
                            text = (uiState as CreateEventUiState.Error).message,
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

                // Event Name TextField
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Event Name") },
                    placeholder = { Text("Enter event name") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Description TextField
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    label = { Text("Description") },
                    placeholder = { Text("Describe your event") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    ),
                    maxLines = 8
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Event Date Picker
                val datePickerState = rememberDatePickerState()
                
                OutlinedButton(
                    onClick = { showDatePicker = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (eventDate.isNotEmpty()) {
                            "Event Date: ${eventDate.substringBefore('T')}"
                        } else {
                            "Select Event Date"
                        }
                    )
                }
                
                if (showDatePicker) {
                    DatePickerDialog(
                        onDismissRequest = { showDatePicker = false },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    datePickerState.selectedDateMillis?.let { millis ->
                                        // Convert millis to ISO 8601 format
                                        val instant = Instant.fromEpochMilliseconds(millis)
                                        // Set time to noon UTC to avoid timezone issues
                                        val dateTime = instant.toString().substringBefore('T') + "T12:00:00Z"
                                        eventDate = dateTime
                                    }
                                    showDatePicker = false
                                }
                            ) {
                                Text("OK")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showDatePicker = false }) {
                                Text("Cancel")
                            }
                        }
                    ) {
                        DatePicker(state = datePickerState)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Image preview section
                if (selectedImageBase64 != null) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            // Display selected image preview
                            KamelImage(
                                resource = asyncPainterResource("data:image/jpeg;base64,$selectedImageBase64"),
                                contentDescription = "Selected image",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop,
                                onLoading = {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator()
                                    }
                                },
                                onFailure = {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("Failed to load image")
                                    }
                                }
                            )

                            // Remove button with background
                            IconButton(
                                onClick = { selectedImageBase64 = null },
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(8.dp),
                                colors = IconButtonDefaults.iconButtonColors(
                                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
                                )
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
                        imagePicker.launch()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = selectedImageBase64 == null
                ) {
                    Icon(
                        Photo,
                        contentDescription = "Add photo",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (selectedImageBase64 == null) "Add Photo" else "Photo Added")
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Helper text
                Text(
                    text = "Your event will be shared with your Leo club community",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (selectedImageBase64 != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Image will be compressed to under 2MB",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
