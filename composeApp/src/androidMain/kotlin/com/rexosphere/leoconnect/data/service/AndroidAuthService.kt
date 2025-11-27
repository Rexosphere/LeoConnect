package com.rexosphere.leoconnect.data.service

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.rexosphere.leoconnect.domain.service.AuthService
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await
import com.rexosphere.leoconnect.util.ActivityProvider

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


            val webClientId = getWebClientId()
            Log.d(TAG, "Starting Google Sign-In with Web Client ID: ${webClientId.take(20)}...")

            // 1. Configure Google Sign-In
            // Generate a nonce (optional but recommended for security)
            val hashedNonce = java.util.UUID.randomUUID().toString()
            
            val googleIdOption = GetSignInWithGoogleOption.Builder(webClientId)
                .setNonce(hashedNonce)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            Log.d(TAG, "Requesting credentials...")

            // 2. Get the Google ID token using Credential Manager
            val currentActivity = ActivityProvider.currentActivity
            if (currentActivity == null) {
                Log.e(TAG, "No active activity found for sign-in")
                return Result.failure(Exception("No active activity found. Please try again."))
            }

            val result = credentialManager.getCredential(
                request = request,
                context = currentActivity,
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
                is androidx.credentials.CustomCredential -> {
                    if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                        try {
                            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                            val googleIdToken = googleIdTokenCredential.idToken
                            Log.d(TAG, "Successfully received Google ID token from CustomCredential")

                            // 3. Sign in to Firebase with the Google ID token
                            Log.d(TAG, "Signing in to Firebase...")
                            val authCredential = GoogleAuthProvider.credential(googleIdToken, null)
                            firebaseAuth.signInWithCredential(authCredential)

                            // 4. Get the Firebase ID token
                            val firebaseToken = firebaseAuth.currentUser?.getIdToken(false)
                                ?: return Result.failure(Exception("Failed to get Firebase token after sign in"))

                            Log.d(TAG, "Successfully signed in to Firebase")
                            Result.success(firebaseToken)
                        } catch (e: Exception) {
                            Log.e(TAG, "Failed to parse Google ID token from CustomCredential", e)
                            Result.failure(e)
                        }
                    } else {
                        Log.e(TAG, "Unexpected CustomCredential type: ${credential.type}")
                        Result.failure(Exception("Unexpected CustomCredential type: ${credential.type}"))
                    }
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
                    "No Google account found. This often happens if the app's SHA-1 fingerprint is missing from Firebase Console. Please check your debug keystore SHA-1."
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
    /**
     * Get the Web Client ID from the Firebase project
     */
    private fun getWebClientId(): String {
        // Hardcoded Web Client ID to ensure correct resolution
        // This is the client_type: 3 ID from google-services.json
        val webClientId = "124058547668-kon20mi71tottki8najp3cv58qj3ptf3.apps.googleusercontent.com"
        Log.d(TAG, "Using Web Client ID: $webClientId")
        return webClientId
    }
}
