package com.rexosphere.leoconnect.data.service

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.rexosphere.leoconnect.domain.service.AuthService
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await

class AndroidAuthService(
    private val context: Context,
    private val firebaseAuth: FirebaseAuth
) : AuthService {

    private val credentialManager = CredentialManager.create(context)

    companion object {
        private const val TAG = "AndroidAuthService"
    }

    override suspend fun signInWithGoogle(): Result<String> {
        return try {
            // Verify we have an Activity context
            if (context !is Activity) {
                Log.e(TAG, "Context is not an Activity: ${context.javaClass.simpleName}")
                return Result.failure(Exception(
                    "Sign-in requires an Activity context. " +
                    "Please make sure you're passing the Activity, not Application context."
                ))
            }

            val webClientId = getWebClientId()
            Log.d(TAG, "Starting Google Sign-In with Web Client ID: ${webClientId.take(20)}...")

            // 1. Configure Google Sign-In
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false) // Show all Google accounts
                .setServerClientId(webClientId)
                .setAutoSelectEnabled(false) // Always show account picker
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            Log.d(TAG, "Requesting credentials...")

            // 2. Get the Google ID token using Credential Manager
            val result = credentialManager.getCredential(
                request = request,
                context = context,
            )

            Log.d(TAG, "Received credential type: ${result.credential.type}")

            val credential = result.credential

            when (credential) {
                is GoogleIdTokenCredential -> {
                    val googleIdToken = credential.idToken
                    Log.d(TAG, "Successfully received Google ID token")

                    // 3. Sign in to Firebase with the Google ID token
                    Log.d(TAG, "Signing in to Firebase...")
                    val authCredential = GoogleAuthProvider.credential(googleIdToken, null)
                    firebaseAuth.signInWithCredential(authCredential)

                    // 4. Get the Firebase ID token
                    val firebaseToken = firebaseAuth.currentUser?.getIdToken(false)
                        ?: return Result.failure(Exception("Failed to get Firebase token after sign in"))

                    Log.d(TAG, "Successfully signed in to Firebase")
                    Result.success(firebaseToken)
                }
                else -> {
                    Log.e(TAG, "Invalid credential type: ${credential.type}")
                    Result.failure(Exception("Invalid credential type: ${credential.type}"))
                }
            }
        } catch (e: GetCredentialException) {
            Log.e(TAG, "GetCredentialException: ${e.type} - ${e.message}", e)
            val errorMessage = when {
                e.message?.contains("No credentials available") == true ->
                    "No Google account found. Please add a Google account to your device in Settings > Accounts"
                e.message?.contains("Caller has been temporarily blocked") == true ->
                    "Too many failed attempts. Please try again later"
                e.message?.contains("16") == true || e.message?.contains("SIGN_IN_REQUIRED") == true ->
                    "Please add a Google account to your device"
                else -> "Google Sign-In failed: ${e.type} - ${e.message}"
            }
            Result.failure(Exception(errorMessage, e))
        } catch (e: Exception) {
            Log.e(TAG, "Authentication failed", e)
            Result.failure(Exception("Authentication failed: ${e.message}", e))
        }
    }

    override suspend fun getCurrentToken(forceRefresh: Boolean): String? {
        return try {
            firebaseAuth.currentUser?.getIdToken(forceRefresh)
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun signOut() {
        firebaseAuth.signOut()
    }

    override fun getCurrentUserId(): String? {
        return firebaseAuth.currentUser?.uid
    }

    override fun isSignedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }

    /**
     * Get the Web Client ID from the Firebase project
     * Replace this with your actual Web Client ID from Firebase Console
     */
    private fun getWebClientId(): String {
        // TODO: Replace with your actual Web Client ID from Firebase Console
        // You can find this in google-services.json under oauth_client with client_type: 3
        return context.resources.getIdentifier(
            "default_web_client_id",
            "string",
            context.packageName
        ).let { resId ->
            if (resId != 0) {
                context.getString(resId)
            } else {
                throw IllegalStateException(
                    "Web client ID not found. Make sure google-services.json is properly configured"
                )
            }
        }
    }
}
