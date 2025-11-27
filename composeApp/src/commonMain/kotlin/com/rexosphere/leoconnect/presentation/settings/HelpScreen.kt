package com.rexosphere.leoconnect.presentation.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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

data class FAQItem(
    val question: String,
    val answer: String
)

class HelpScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        val faqList = remember {
            listOf(
                FAQItem(
                    "What is LeoConnect?",
                    "LeoConnect is a social platform designed specifically for Leo Club members to connect, share activities, and collaborate on service projects."
                ),
                FAQItem(
                    "How do I verify my Leo membership?",
                    "Go to Edit Profile in Settings and enter your Leo ID. Your club advisor can help you find this information."
                ),
                FAQItem(
                    "How can I join a Leo Club on LeoConnect?",
                    "Navigate to the Clubs tab, find your club, and click Follow. If your club isn't listed, contact support to add it."
                ),
                FAQItem(
                    "Can I create posts?",
                    "Yes! Verified Leo members and club webmasters can create posts to share club activities and updates."
                ),
                FAQItem(
                    "How do I report inappropriate content?",
                    "Click the three dots menu on any post or comment and select 'Report'. Our team will review it promptly."
                ),
                FAQItem(
                    "What are the community guidelines?",
                    "Be respectful, share authentic Leo activities, no spam or harassment, and maintain a positive community spirit."
                ),
                FAQItem(
                    "How do I change my notification settings?",
                    "Go to Settings > Notifications to customize what alerts you receive."
                ),
                FAQItem(
                    "Is my data secure?",
                    "Yes, we use industry-standard encryption and security practices. See our Privacy Policy for details."
                )
            )
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Help & Support") },
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
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = "Get Help",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            HelpActionItem(
                                icon = Icons.Default.Email,
                                title = "Contact Support",
                                description = "support@leoconnect.org",
                                onClick = { /* TODO: Open email */ }
                            )
                            Divider()
                            HelpActionItem(
                                icon = Icons.Default.BugReport,
                                title = "Report a Bug",
                                description = "Help us improve the app",
                                onClick = { /* TODO: Open bug report */ }
                            )
                            Divider()
                            HelpActionItem(
                                icon = Icons.Default.Feedback,
                                title = "Send Feedback",
                                description = "Share your thoughts",
                                onClick = { /* TODO: Open feedback */ }
                            )
                        }
                    }
                }

                item {
                    Text(
                        text = "Frequently Asked Questions",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                items(faqList) { faq ->
                    FAQItemView(faq)
                }

                item {
                    Text(
                        text = "Resources",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            HelpActionItem(
                                icon = Icons.Default.MenuBook,
                                title = "User Guide",
                                description = "Learn how to use LeoConnect",
                                onClick = { /* TODO: Open guide */ }
                            )
                            Divider()
                            HelpActionItem(
                                icon = Icons.Default.VideoLibrary,
                                title = "Video Tutorials",
                                description = "Watch helpful videos",
                                onClick = { /* TODO: Open videos */ }
                            )
                            Divider()
                            HelpActionItem(
                                icon = Icons.Default.Public,
                                title = "Visit Website",
                                description = "www.leoconnect.org",
                                onClick = { /* TODO: Open website */ }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FAQItemView(faq: FAQItem) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = faq.question,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (expanded) "Collapse" else "Expand"
                )
            }

            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Text(
                    text = faq.answer,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 12.dp)
                )
            }
        }
    }
}

@Composable
fun HelpActionItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
