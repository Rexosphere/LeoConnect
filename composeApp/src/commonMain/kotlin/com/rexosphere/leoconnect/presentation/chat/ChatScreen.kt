package com.rexosphere.leoconnect.presentation.chat

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.rexosphere.leoconnect.domain.model.Message
import com.rexosphere.leoconnect.domain.model.UserProfile
import com.rexosphere.leoconnect.domain.service.AuthService
import org.koin.compose.koinInject
import com.rexosphere.leoconnect.presentation.icons.ChevronLeft
import com.rexosphere.leoconnect.presentation.icons.PaperAirplane
import com.rexosphere.leoconnect.presentation.icons.User
import com.rexosphere.leoconnect.util.ClickableTextWithLinks
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource

data class ChatScreen(
    val otherUser: UserProfile
) : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = koinScreenModel<ChatScreenModel>()
        val authService = koinInject<AuthService>()
        val uiState by screenModel.uiState.collectAsState()
        var messageText by remember { mutableStateOf("") }
        val currentUserId = authService.getCurrentUserId()

        LaunchedEffect(otherUser.uid) {
            screenModel.loadMessages(otherUser.uid)
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (otherUser.photoURL != null) {
                                KamelImage(
                                    resource = asyncPainterResource(otherUser.photoURL),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop,
                                    onFailure = {
                                        Icon(
                                            User,
                                            null,
                                            modifier = Modifier.size(32.dp),
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.surfaceVariant),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        User,
                                        null,
                                        modifier = Modifier.size(20.dp),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Spacer(Modifier.width(12.dp))
                            Text(otherUser.displayName)
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(ChevronLeft, contentDescription = "Back")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when (val state = uiState) {
                    is ChatUiState.Loading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    is ChatUiState.Success -> {
                        MessagesList(
                            messages = state.messages,
                            currentUserId = currentUserId ?: "",
                            onDeleteMessage = { messageId ->
                                screenModel.deleteMessage(messageId)
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    is ChatUiState.Error -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = state.message,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }

                // Message input
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 8.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = messageText,
                            onValueChange = { messageText = it },
                            modifier = Modifier.weight(1f),
                            placeholder = { Text("Type a message...") },
                            shape = RoundedCornerShape(24.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(0.3f)
                            ),
                            maxLines = 4
                        )

                        Spacer(Modifier.width(12.dp))

                        IconButton(
                            onClick = {
                                if (messageText.isNotBlank()) {
                                    screenModel.sendMessage(otherUser.uid, messageText.trim())
                                    messageText = ""
                                }
                            },
                            enabled = messageText.isNotBlank()
                        ) {
                            Icon(
                                PaperAirplane,
                                contentDescription = "Send",
                                tint = if (messageText.isNotBlank())
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MessagesList(
    messages: List<Message>,
    currentUserId: String,
    onDeleteMessage: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            scrollState.animateScrollToItem(messages.size - 1)
        }
    }

    LazyColumn(
        state = scrollState,
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(messages, key = { it.id }) { message ->
            val isCurrentUser = message.senderId == currentUserId
            MessageBubble(
                message = message,
                isCurrentUser = isCurrentUser,
                onDeleteMessage = onDeleteMessage
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun MessageBubble(
    message: Message,
    isCurrentUser: Boolean,
    onDeleteMessage: (String) -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    val bubbleColor = if (isCurrentUser) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }

    val textColor = if (isCurrentUser) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    val bubbleShape = if (isCurrentUser) {
        RoundedCornerShape(20.dp, 4.dp, 20.dp, 20.dp)
    } else {
        RoundedCornerShape(4.dp, 20.dp, 20.dp, 20.dp)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = if (isCurrentUser) Arrangement.End else Arrangement.Start
    ) {
        Surface(
            modifier = if (isCurrentUser) {
                Modifier
                    .padding(start = 40.dp)
                    .combinedClickable(
                        onClick = {},
                        onLongClick = { showDeleteDialog = true }
                    )
            } else {
                Modifier.padding(end = 40.dp)
            },
            color = bubbleColor,
            shape = bubbleShape
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                ClickableTextWithLinks(
                    text = message.content,
                    style = MaterialTheme.typography.bodyLarge,
                    color = textColor,
                    linkColor = if (isCurrentUser) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.primary
                    }
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = formatTimestamp(message.createdAt),
                    style = MaterialTheme.typography.bodySmall,
                    color = textColor.copy(alpha = 0.7f),
                    modifier = Modifier.align(if (isCurrentUser) Alignment.End else Alignment.Start)
                )
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Message") },
            text = { Text("Are you sure you want to delete this message?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteMessage(message.id)
                        showDeleteDialog = false
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

private fun formatTimestamp(timestamp: String): String {
    // TODO: Implement proper timestamp formatting
    // For now, just return a simple format
    return try {
        // Parse ISO 8601 timestamp and format it
        // This is a placeholder - you'll need proper date formatting
        timestamp.split("T").getOrNull(1)?.take(5) ?: timestamp
    } catch (e: Exception) {
        timestamp
    }
}
