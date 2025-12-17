package com.rexosphere.leoconnect.presentation.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.rexosphere.leoconnect.presentation.MainScreen
import com.rexosphere.leoconnect.presentation.encryption.EncryptionSetupScreen
import com.rexosphere.leoconnect.domain.repository.LeoRepository
import org.koin.compose.koinInject

class EmailAuthScreen : Screen {
    @Composable
    override fun Content() {
        val screenModel = koinScreenModel<EmailAuthScreenModel>()
        val state by screenModel.state.collectAsState()
        val navigator = LocalNavigator.currentOrThrow
        val repository = koinInject<LeoRepository>()

        // Handle navigation based on state
        LaunchedEffect(state.isSignedIn, state.needsOnboarding) {
            if (state.isSignedIn) {
                val needsEncryptionSetup = repository.needsEncryptionSetup()
                if (needsEncryptionSetup) {
                    navigator.replaceAll(EncryptionSetupScreen(state.needsOnboarding))
                } else if (state.needsOnboarding) {
                    navigator.replaceAll(OnboardingScreen())
                } else {
                    navigator.replaceAll(MainScreen())
                }
            }
        }

        // Show verification screen or auth form
        if (state.showVerificationScreen) {
            EmailVerificationContent(
                email = state.email,
                isLoading = state.isLoading,
                error = state.error,
                statusMessage = state.statusMessage,
                onResendClick = screenModel::resendVerificationEmail,
                onCheckVerification = screenModel::checkVerificationStatus,
                onBackClick = screenModel::backFromVerification
            )
        } else {
            EmailAuthContent(
                state = state,
                onEmailChange = screenModel::updateEmail,
                onPasswordChange = screenModel::updatePassword,
                onConfirmPasswordChange = screenModel::updateConfirmPassword,
                onToggleMode = screenModel::toggleMode,
                onSubmit = screenModel::submit,
                onBackClick = { navigator.pop() }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EmailAuthContent(
    state: EmailAuthUiState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onToggleMode: () -> Unit,
    onSubmit: () -> Unit,
    onBackClick: () -> Unit
) {
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (state.isSignUpMode) "Create Account" else "Sign In") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
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
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = if (state.isSignUpMode) "Create your account" else "Welcome back",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (state.isSignUpMode) "Sign up with your email to get started" else "Sign in to continue to LeoConnect",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(40.dp))

            OutlinedTextField(
                value = state.email,
                onValueChange = onEmailChange,
                label = { Text("Email") },
                placeholder = { Text("your@email.com") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                isError = state.emailError != null,
                supportingText = state.emailError?.let { { Text(it) } },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = state.password,
                onValueChange = onPasswordChange,
                label = { Text("Password") },
                placeholder = { Text("••••••••") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility, contentDescription = null)
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                isError = state.passwordError != null,
                supportingText = state.passwordError?.let { { Text(it) } },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = if (state.isSignUpMode) ImeAction.Next else ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) },
                    onDone = { focusManager.clearFocus(); if (!state.isSignUpMode) onSubmit() }
                ),
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading
            )

            AnimatedVisibility(visible = state.isSignUpMode, enter = fadeIn(), exit = fadeOut()) {
                Column {
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = state.confirmPassword,
                        onValueChange = onConfirmPasswordChange,
                        label = { Text("Confirm Password") },
                        placeholder = { Text("••••••••") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        trailingIcon = {
                            IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                                Icon(if (confirmPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility, contentDescription = null)
                            }
                        },
                        visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        isError = state.confirmPasswordError != null,
                        supportingText = state.confirmPasswordError?.let { { Text(it) } },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus(); onSubmit() }),
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !state.isLoading
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onSubmit,
                enabled = !state.isLoading,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.5.dp)
                    Spacer(modifier = Modifier.width(12.dp))
                }
                Text(
                    text = when {
                        state.isLoading && state.statusMessage != null -> state.statusMessage
                        state.isSignUpMode -> "Create Account"
                        else -> "Sign In"
                    },
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            AnimatedVisibility(visible = state.error != null) {
                Column {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = state.error ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                Text(
                    text = if (state.isSignUpMode) "Already have an account?" else "Don't have an account?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                TextButton(onClick = onToggleMode, enabled = !state.isLoading) {
                    Text(text = if (state.isSignUpMode) "Sign In" else "Sign Up", fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EmailVerificationContent(
    email: String,
    isLoading: Boolean,
    error: String?,
    statusMessage: String?,
    onResendClick: () -> Unit,
    onCheckVerification: () -> Unit,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Verify Email") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(imageVector = Icons.Default.Email, contentDescription = null, modifier = Modifier.size(80.dp), tint = MaterialTheme.colorScheme.primary)

            Spacer(modifier = Modifier.height(24.dp))

            Text(text = "Check your email", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onBackground)

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "We've sent a verification link to:", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = email, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold), color = MaterialTheme.colorScheme.primary, textAlign = TextAlign.Center)

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Click the link in your email to verify your account, then come back here and tap the button below.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = onCheckVerification,
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.5.dp)
                    Spacer(modifier = Modifier.width(12.dp))
                }
                Text(text = statusMessage ?: "I've Verified My Email", fontSize = 16.sp, fontWeight = FontWeight.Medium)
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(onClick = onResendClick, enabled = !isLoading, modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(12.dp)) {
                Text(text = "Resend Verification Email", fontSize = 16.sp)
            }

            AnimatedVisibility(visible = error != null) {
                Column {
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(text = error ?: "", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(text = "Didn't receive the email? Check your spam folder.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f), textAlign = TextAlign.Center)
        }
    }
}
