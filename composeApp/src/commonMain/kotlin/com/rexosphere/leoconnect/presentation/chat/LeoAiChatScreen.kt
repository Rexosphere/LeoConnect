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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.rexosphere.leoconnect.presentation.LocalBottomBarPadding
import com.rexosphere.leoconnect.presentation.icons.ChevronLeft
import com.rexosphere.leoconnect.presentation.icons.PaperAirplane
import com.rexosphere.leoconnect.util.ClickableTextWithLinks

data class AiChatMessage(
    val id: String,
    val content: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

class LeoAiChatScreen : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = koinScreenModel<LeoAiChatScreenModel>()
        val uiState by screenModel.uiState.collectAsState()
        var messageText by remember { mutableStateOf("") }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "🤖",
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text("Leo AI", style = MaterialTheme.typography.titleMedium)
                                Text(
                                    "Ask me anything about Leo Clubs",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
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
            val bottomBarPadding = LocalBottomBarPadding.current
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(bottom = bottomBarPadding)
            ) {
                when (val state = uiState) {
                    is AiChatUiState.Idle, is AiChatUiState.Success -> {
                        AiMessagesList(
                            messages = (state as? AiChatUiState.Success)?.messages ?: emptyList(),
                            isLoading = state is AiChatUiState.Loading,
                            onDeleteMessage = { messageId ->
                                screenModel.deleteMessage(messageId)
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    is AiChatUiState.Loading -> {
                        AiMessagesList(
                            messages = (state as? AiChatUiState.Success)?.messages ?: screenModel.getMessages(),
                            isLoading = true,
                            onDeleteMessage = { messageId ->
                                screenModel.deleteMessage(messageId)
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    is AiChatUiState.Error -> {
                        Column(modifier = Modifier.weight(1f)) {
                            AiMessagesList(
                                messages = screenModel.getMessages(),
                                isLoading = false,
                                onDeleteMessage = { messageId ->
                                    screenModel.deleteMessage(messageId)
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                // Error message banner
                if (uiState is AiChatUiState.Error) {
                    Surface(
                        color = MaterialTheme.colorScheme.errorContainer,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = (uiState as AiChatUiState.Error).message,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
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
                            placeholder = { Text("Ask about Leo Clubs...") },
                            shape = RoundedCornerShape(24.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(0.3f)
                            ),
                            maxLines = 4,
                            enabled = uiState !is AiChatUiState.Loading
                        )

                        Spacer(Modifier.width(12.dp))

                        IconButton(
                            onClick = {
                                if (messageText.isNotBlank()) {
                                    screenModel.sendMessage(messageText.trim())
                                    messageText = ""
                                }
                            },
                            enabled = messageText.isNotBlank() && uiState !is AiChatUiState.Loading
                        ) {
                            Icon(
                                PaperAirplane,
                                contentDescription = "Send",
                                tint = if (messageText.isNotBlank() && uiState !is AiChatUiState.Loading)
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
private fun AiMessagesList(
    messages: List<AiChatMessage>,
    isLoading: Boolean,
    onDeleteMessage: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberLazyListState()

    LaunchedEffect(messages.size, isLoading) {
        if (messages.isNotEmpty() || isLoading) {
            scrollState.animateScrollToItem(if (isLoading) messages.size else messages.size - 1)
        }
    }

    LazyColumn(
        state = scrollState,
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        if (messages.isEmpty() && !isLoading) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "👋",
                            style = MaterialTheme.typography.displayLarge
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = "Hi! I'm Leo AI",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "Ask me anything about Leo Clubs!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        items(messages, key = { it.id }) { message ->
            AiMessageBubble(
                message = message,
                onDeleteMessage = onDeleteMessage
            )
        }

        if (isLoading) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Surface(
                        modifier = Modifier.padding(end = 40.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(4.dp, 20.dp, 20.dp, 20.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "Thinking...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun AiMessageBubble(
    message: AiChatMessage,
    onDeleteMessage: (String) -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    val isUser = message.isUser
    val bubbleColor = if (isUser) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }

    val textColor = if (isUser) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    val bubbleShape = if (isUser) {
        RoundedCornerShape(20.dp, 4.dp, 20.dp, 20.dp)
    } else {
        RoundedCornerShape(4.dp, 20.dp, 20.dp, 20.dp)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Surface(
            modifier = if (isUser) {
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
                    linkColor = MaterialTheme.colorScheme.primary
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
