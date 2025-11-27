package com.rexosphere.leoconnect.presentation.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow

class TermsScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        var selectedTab by remember { mutableStateOf(0) }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Legal") },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("Terms of Service") }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Privacy Policy") }
                    )
                }

                when (selectedTab) {
                    0 -> TermsOfServiceContent()
                    1 -> PrivacyPolicyContent()
                }
            }
        }
    }
}

@Composable
fun TermsOfServiceContent() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Terms of Service",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            Text(
                text = "Last Updated: December 2024",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        item {
            SectionTitle("1. Acceptance of Terms")
            SectionContent(
                "By accessing and using LeoConnect, you accept and agree to be bound by the terms and provision of this agreement. If you do not agree to these terms, please do not use this service."
            )
        }

        item {
            SectionTitle("2. Description of Service")
            SectionContent(
                "LeoConnect provides a social networking platform for Leo Club members to connect, share activities, and collaborate on service projects. The service is provided \"as is\" without warranty of any kind."
            )
        }

        item {
            SectionTitle("3. User Responsibilities")
            SectionContent(
                "You are responsible for:\n" +
                        "• Maintaining the confidentiality of your account\n" +
                        "• All activities that occur under your account\n" +
                        "• Ensuring all content you post is accurate and appropriate\n" +
                        "• Respecting other users and the Leo Club community"
            )
        }

        item {
            SectionTitle("4. Content Guidelines")
            SectionContent(
                "Users must not post content that is:\n" +
                        "• Illegal, harmful, or offensive\n" +
                        "• Infringing on intellectual property rights\n" +
                        "• Spam or unsolicited commercial content\n" +
                        "• False or misleading information"
            )
        }

        item {
            SectionTitle("5. Account Termination")
            SectionContent(
                "We reserve the right to suspend or terminate accounts that violate these terms or engage in activities harmful to the community."
            )
        }

        item {
            SectionTitle("6. Limitation of Liability")
            SectionContent(
                "LeoConnect shall not be liable for any indirect, incidental, special, consequential or punitive damages resulting from your use or inability to use the service."
            )
        }

        item {
            SectionTitle("7. Changes to Terms")
            SectionContent(
                "We reserve the right to modify these terms at any time. Continued use of the service after changes constitutes acceptance of the new terms."
            )
        }

        item {
            SectionTitle("8. Contact Information")
            SectionContent(
                "For questions about these Terms of Service, please contact us at legal@leoconnect.org"
            )
        }
    }
}

@Composable
fun PrivacyPolicyContent() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Privacy Policy",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            Text(
                text = "Last Updated: December 2024",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        item {
            SectionTitle("1. Information We Collect")
            SectionContent(
                "We collect information you provide directly:\n" +
                        "• Account information (name, email, Leo ID)\n" +
                        "• Profile information\n" +
                        "• Posts, comments, and other content\n" +
                        "• Communication with other users\n\n" +
                        "We automatically collect:\n" +
                        "• Device information\n" +
                        "• Usage data\n" +
                        "• Log data"
            )
        }

        item {
            SectionTitle("2. How We Use Your Information")
            SectionContent(
                "We use collected information to:\n" +
                        "• Provide and improve our services\n" +
                        "• Communicate with you\n" +
                        "• Personalize your experience\n" +
                        "• Ensure platform security\n" +
                        "• Comply with legal obligations"
            )
        }

        item {
            SectionTitle("3. Information Sharing")
            SectionContent(
                "We do not sell your personal information. We may share information:\n" +
                        "• With other Leo Club members as per your privacy settings\n" +
                        "• With service providers who assist our operations\n" +
                        "• When required by law\n" +
                        "• With your consent"
            )
        }

        item {
            SectionTitle("4. Data Security")
            SectionContent(
                "We implement industry-standard security measures to protect your data. However, no method of transmission over the internet is 100% secure."
            )
        }

        item {
            SectionTitle("5. Your Rights")
            SectionContent(
                "You have the right to:\n" +
                        "• Access your personal data\n" +
                        "• Correct inaccurate data\n" +
                        "• Delete your account and data\n" +
                        "• Control privacy settings\n" +
                        "• Opt-out of communications"
            )
        }

        item {
            SectionTitle("6. Data Retention")
            SectionContent(
                "We retain your information for as long as your account is active or as needed to provide services. You may request deletion at any time through account settings."
            )
        }

        item {
            SectionTitle("7. Children's Privacy")
            SectionContent(
                "LeoConnect is intended for Leo Club members, typically ages 12-30. We comply with applicable laws regarding children's privacy."
            )
        }

        item {
            SectionTitle("8. Changes to This Policy")
            SectionContent(
                "We may update this Privacy Policy from time to time. We will notify you of any changes by posting the new policy on this page."
            )
        }

        item {
            SectionTitle("9. Contact Us")
            SectionContent(
                "For privacy-related questions or concerns, contact us at:\n" +
                        "privacy@leoconnect.org"
            )
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary
    )
}

@Composable
fun SectionContent(content: String) {
    Text(
        text = content,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurface
    )
}
