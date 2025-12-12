package com.rexosphere.leoconnect.presentation.auth

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.rexosphere.leoconnect.presentation.MainScreen
import org.jetbrains.compose.resources.painterResource
import leoconnect.composeapp.generated.resources.Res
import leoconnect.composeapp.generated.resources.ic_google_logo
import leoconnect.composeapp.generated.resources.leo_gold
import org.jetbrains.compose.resources.*

class LoginScreen : Screen {
    @Composable
    override fun Content() {
        val screenModel = koinScreenModel<LoginScreenModel>()
        val state by screenModel.state.collectAsState()
        val navigator = LocalNavigator.currentOrThrow

        LaunchedEffect(state.isSignedIn, state.needsOnboarding) {
            if (state.isSignedIn) {
                if (state.needsOnboarding) {
                    navigator.replace(OnboardingScreen())
                } else {
                    navigator.replace(MainScreen())
                }
            }
        }

        LoginScreenContent(
            state = state,
            onSignInClick = screenModel::signInWithGoogle
        )
    }
}

@Composable
private fun LoginScreenContent(
    state: LoginUiState,
    onSignInClick: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // App Icon + Name
            Image(
                painter = painterResource(Res.drawable.leo_gold),
                contentDescription = "Leo Connect Logo",
                modifier = Modifier.size(82.dp)
            )

            Text(
                text = "LeoConnect",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.5.sp
                ),
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Connect with Leo Clubs worldwide",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                lineHeight = 28.sp
            )

            Spacer(modifier = Modifier.height(80.dp))

            // Google Sign-In Button – Official Style (Multiplatform-safe)
            Button(
                onClick = onSignInClick,
                enabled = !state.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSystemInDarkTheme()) Color(0xFF4285F4) else Color(0xFFFFFFFF),
                    contentColor = if (isSystemInDarkTheme()) Color.White else Color.Black
                ),
                border = if (!isSystemInDarkTheme()) {
                    ButtonDefaults.outlinedButtonBorder(enabled = true)
                } else null
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Google Logo – loaded via Compose Resources (multiplatform)
                    Icon(
                        painter = painterResource(Res.drawable.ic_google_logo),
                        contentDescription = null,
                        tint = Color.Unspecified,
                        modifier = Modifier.size(24.dp)
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Text(
                        text = if (state.isLoading) "Signing in..." else "Continue with Google",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )

                    if (state.isLoading) {
                        Spacer(modifier = Modifier.width(16.dp))
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.5.dp,
                            color = LocalContentColor.current
                        )
                    }
                }
            }

            // Error message
            if (state.error != null) {
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = state.error ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(60.dp))

            Text(
                text = "By continuing, you agree to our Terms of Service and Privacy Policy",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f),
                textAlign = TextAlign.Center,
                lineHeight = 18.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}