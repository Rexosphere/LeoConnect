package com.rexosphere.leoconnect.data.service

import com.rexosphere.leoconnect.domain.service.AuthService
import io.ktor.http.*
import io.ktor.server.cio.CIO as ServerCIO
import io.ktor.server.cio.CIOApplicationEngine
import io.ktor.server.engine.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.client.*
import io.ktor.client.engine.cio.CIO as ClientCIO
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import kotlinx.coroutines.*
import kotlinx.serialization.json.*
import java.awt.Desktop
import java.net.URI
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64
import java.util.prefs.Preferences
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Desktop authentication service implementing Google OAuth 2.0 with PKCE
 * Uses a localhost redirect server to receive the OAuth callback
 * 
 * Note: This implementation stores the Google ID token directly and uses it
 * for backend API calls. The backend validates the token using Firebase Admin SDK.
 */
class DesktopAuthService : AuthService {

    companion object {
        private const val TAG = "DesktopAuthService"
        
        // OAuth Configuration - loaded from environment or local.properties
        private val DESKTOP_CLIENT_ID: String by lazy {
            System.getenv("GOOGLE_DESKTOP_CLIENT_ID")
                ?: System.getProperty("GOOGLE_DESKTOP_CLIENT_ID")
                ?: loadFromLocalProperties("GOOGLE_DESKTOP_CLIENT_ID")
                ?: throw IllegalStateException("GOOGLE_DESKTOP_CLIENT_ID not configured. Set it in local.properties or as environment variable.")
        }
        
        private val DESKTOP_CLIENT_SECRET: String by lazy {
            System.getenv("GOOGLE_DESKTOP_CLIENT_SECRET")
                ?: System.getProperty("GOOGLE_DESKTOP_CLIENT_SECRET")
                ?: loadFromLocalProperties("GOOGLE_DESKTOP_CLIENT_SECRET")
                ?: throw IllegalStateException("GOOGLE_DESKTOP_CLIENT_SECRET not configured. Set it in local.properties or as environment variable.")
        }
        
        private const val REDIRECT_PORT = 8085
        private const val REDIRECT_URI = "http://localhost:$REDIRECT_PORT/callback"
        
        // Google OAuth Endpoints
        private const val AUTHORIZATION_ENDPOINT = "https://accounts.google.com/o/oauth2/v2/auth"
        private const val TOKEN_ENDPOINT = "https://oauth2.googleapis.com/token"
        
        // Scopes needed for Firebase Auth
        private const val SCOPES = "openid email profile"
        
        // Preference keys
        private const val PREF_ID_TOKEN = "google_id_token"
        private const val PREF_ACCESS_TOKEN = "google_access_token"
        private const val PREF_REFRESH_TOKEN = "google_refresh_token"
        private const val PREF_USER_ID = "google_user_id"
        
        /**
         * Load a property from local.properties file
         */
        private fun loadFromLocalProperties(key: String): String? {
            return try {
                val propsFile = java.io.File("local.properties")
                if (propsFile.exists()) {
                    val props = java.util.Properties()
                    props.load(propsFile.inputStream())
                    props.getProperty(key)
                } else {
                    null
                }
            } catch (e: Exception) {
                println("[$TAG] Failed to load $key from local.properties: ${e.message}")
                null
            }
        }
    }

    private val httpClient = HttpClient(ClientCIO)
    private var currentCodeVerifier: String? = null
    private val prefs = Preferences.userNodeForPackage(DesktopAuthService::class.java)
    
    // In-memory cache
    private var cachedIdToken: String? = null
    private var cachedUserId: String? = null
    
    init {
        // Load cached tokens on startup
        cachedIdToken = prefs.get(PREF_ID_TOKEN, null)
        cachedUserId = prefs.get(PREF_USER_ID, null)
    }

    override suspend fun signInWithGoogle(): Result<String> {
        return try {
            println("[$TAG] Starting Google Sign-In flow...")
            
            // 1. Generate PKCE code verifier and challenge
            val codeVerifier = generateCodeVerifier()
            currentCodeVerifier = codeVerifier
            val codeChallenge = generateCodeChallenge(codeVerifier)
            
            // 2. Generate state for CSRF protection
            val state = generateState()
            
            // 3. Start localhost server and wait for callback
            val authCode = startCallbackServerAndWaitForCode(state, codeChallenge)
            println("[$TAG] Received authorization code")
            
            // 4. Exchange authorization code for tokens
            val tokens = exchangeCodeForTokens(authCode, codeVerifier)
            val idToken = tokens["id_token"] ?: throw Exception("No ID token received")
            val accessToken = tokens["access_token"] ?: ""
            val refreshToken = tokens["refresh_token"] ?: ""
            
            println("[$TAG] Received tokens from Google")
            
            // 5. Parse the ID token to extract user info
            val userId = parseUserIdFromToken(idToken)
            
            // 6. Store tokens persistently
            prefs.put(PREF_ID_TOKEN, idToken)
            prefs.put(PREF_ACCESS_TOKEN, accessToken)
            prefs.put(PREF_REFRESH_TOKEN, refreshToken)
            prefs.put(PREF_USER_ID, userId)
            
            // Update in-memory cache
            cachedIdToken = idToken
            cachedUserId = userId
            
            println("[$TAG] Successfully completed sign-in for user: $userId")
            Result.success(idToken)
            
        } catch (e: Exception) {
            println("[$TAG] Sign-in failed: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }

    override suspend fun getCurrentToken(forceRefresh: Boolean): String? {
        return try {
            if (forceRefresh) {
                // Try to refresh the token using the refresh token
                val refreshToken = prefs.get(PREF_REFRESH_TOKEN, null)
                if (refreshToken != null) {
                    val newTokens = refreshAccessToken(refreshToken)
                    val newIdToken = newTokens["id_token"]
                    if (newIdToken != null) {
                        prefs.put(PREF_ID_TOKEN, newIdToken)
                        cachedIdToken = newIdToken
                        return newIdToken
                    }
                }
            }
            cachedIdToken ?: prefs.get(PREF_ID_TOKEN, null)
        } catch (e: Exception) {
            println("[$TAG] Failed to get current token: ${e.message}")
            cachedIdToken
        }
    }

    override suspend fun signOut() {
        try {
            // Clear all stored tokens
            prefs.remove(PREF_ID_TOKEN)
            prefs.remove(PREF_ACCESS_TOKEN)
            prefs.remove(PREF_REFRESH_TOKEN)
            prefs.remove(PREF_USER_ID)
            
            // Clear in-memory cache
            cachedIdToken = null
            cachedUserId = null
            
            println("[$TAG] Signed out successfully")
        } catch (e: Exception) {
            println("[$TAG] Sign out error: ${e.message}")
        }
    }

    override fun getCurrentUserId(): String? {
        return cachedUserId ?: prefs.get(PREF_USER_ID, null)
    }

    override fun isSignedIn(): Boolean {
        val token = cachedIdToken ?: prefs.get(PREF_ID_TOKEN, null)
        return token != null
    }

    // Email/Password authentication methods - Desktop uses OAuth flow only
    override suspend fun createUserWithEmailPassword(email: String, password: String): Result<String> {
        return Result.failure(Exception("Email/Password sign-up is not available on Desktop. Please use Google Sign-In."))
    }

    override suspend fun signInWithEmailPassword(email: String, password: String): Result<String> {
        return Result.failure(Exception("Email/Password sign-in is not available on Desktop. Please use Google Sign-In."))
    }

    override suspend fun sendEmailVerification(): Result<Unit> {
        return Result.failure(Exception("Email verification is not available on Desktop."))
    }

    override suspend fun isEmailVerified(): Boolean = true // OAuth users are verified

    override suspend fun reloadUser(): Result<Unit> = Result.success(Unit)
    
    /**
     * Refresh the access token using the refresh token
     */
    private suspend fun refreshAccessToken(refreshToken: String): Map<String, String> {
        val response = httpClient.post(TOKEN_ENDPOINT) {
            contentType(ContentType.Application.FormUrlEncoded)
            setBody(FormDataContent(Parameters.build {
                append("client_id", DESKTOP_CLIENT_ID)
                append("client_secret", DESKTOP_CLIENT_SECRET)
                append("refresh_token", refreshToken)
                append("grant_type", "refresh_token")
            }))
        }
        
        val responseText = response.bodyAsText()
        
        if (response.status != HttpStatusCode.OK) {
            throw Exception("Token refresh failed: $responseText")
        }
        
        val json = Json.parseToJsonElement(responseText).jsonObject
        return mapOf(
            "access_token" to (json["access_token"]?.jsonPrimitive?.content ?: ""),
            "id_token" to (json["id_token"]?.jsonPrimitive?.content ?: "")
        )
    }
    
    /**
     * Parse user ID from JWT ID token
     */
    private fun parseUserIdFromToken(idToken: String): String {
        try {
            // JWT is in format: header.payload.signature
            val parts = idToken.split(".")
            if (parts.size >= 2) {
                val payload = String(Base64.getUrlDecoder().decode(parts[1]))
                val json = Json.parseToJsonElement(payload).jsonObject
                return json["sub"]?.jsonPrimitive?.content ?: "unknown"
            }
        } catch (e: Exception) {
            println("[$TAG] Failed to parse user ID from token: ${e.message}")
        }
        return "unknown"
    }

    /**
     * Start a temporary localhost server to receive the OAuth callback,
     * open the browser for authentication, and wait for the authorization code
     */
    private suspend fun startCallbackServerAndWaitForCode(state: String, codeChallenge: String): String {
        return suspendCancellableCoroutine { continuation ->
            var server: EmbeddedServer<CIOApplicationEngine, CIOApplicationEngine.Configuration>? = null
            
            try {
                server = embeddedServer(ServerCIO, port = REDIRECT_PORT) {
                    routing {
                        get("/callback") {
                            val receivedState = call.parameters["state"]
                            val code = call.parameters["code"]
                            val error = call.parameters["error"]
                            
                            when {
                                error != null -> {
                                    call.respondText(
                                        createErrorHtml(error),
                                        ContentType.Text.Html
                                    )
                                    continuation.resumeWithException(
                                        Exception("OAuth error: $error")
                                    )
                                }
                                receivedState != state -> {
                                    call.respondText(
                                        createErrorHtml("State mismatch - possible CSRF attack"),
                                        ContentType.Text.Html
                                    )
                                    continuation.resumeWithException(
                                        Exception("State mismatch")
                                    )
                                }
                                code != null -> {
                                    call.respondText(
                                        createSuccessHtml(),
                                        ContentType.Text.Html
                                    )
                                    continuation.resume(code)
                                }
                                else -> {
                                    call.respondText(
                                        createErrorHtml("No authorization code received"),
                                        ContentType.Text.Html
                                    )
                                    continuation.resumeWithException(
                                        Exception("No authorization code received")
                                    )
                                }
                            }
                            
                            // Stop the server after handling the callback
                            CoroutineScope(Dispatchers.IO).launch {
                                delay(1000) // Give time for response to be sent
                                server?.stop(500, 1000)
                            }
                        }
                    }
                }
                
                server.start(wait = false)
                println("[$TAG] Callback server started on port $REDIRECT_PORT")
                
                // Build and open the authorization URL
                val authUrl = buildAuthorizationUrl(state, codeChallenge)
                openBrowser(authUrl)
                
            } catch (e: Exception) {
                server?.stop(0, 0)
                continuation.resumeWithException(e)
            }
            
            continuation.invokeOnCancellation {
                server?.stop(0, 0)
            }
        }
    }

    /**
     * Build the Google OAuth authorization URL
     */
    private fun buildAuthorizationUrl(state: String, codeChallenge: String): String {
        return "$AUTHORIZATION_ENDPOINT?" +
            "client_id=$DESKTOP_CLIENT_ID&" +
            "redirect_uri=${REDIRECT_URI.encodeURLParameter()}&" +
            "response_type=code&" +
            "scope=${SCOPES.encodeURLParameter()}&" +
            "state=$state&" +
            "code_challenge=$codeChallenge&" +
            "code_challenge_method=S256&" +
            "access_type=offline&" +
            "prompt=consent"
    }

    /**
     * Exchange authorization code for tokens
     */
    private suspend fun exchangeCodeForTokens(code: String, codeVerifier: String): Map<String, String> {
        val response = httpClient.post(TOKEN_ENDPOINT) {
            contentType(ContentType.Application.FormUrlEncoded)
            setBody(FormDataContent(Parameters.build {
                append("client_id", DESKTOP_CLIENT_ID)
                append("client_secret", DESKTOP_CLIENT_SECRET)
                append("code", code)
                append("code_verifier", codeVerifier)
                append("grant_type", "authorization_code")
                append("redirect_uri", REDIRECT_URI)
            }))
        }
        
        val responseText = response.bodyAsText()
        println("[$TAG] Token response status: ${response.status}")
        
        if (response.status != HttpStatusCode.OK) {
            throw Exception("Token exchange failed: $responseText")
        }
        
        val json = Json.parseToJsonElement(responseText).jsonObject
        return mapOf(
            "access_token" to (json["access_token"]?.jsonPrimitive?.content ?: ""),
            "id_token" to (json["id_token"]?.jsonPrimitive?.content ?: ""),
            "refresh_token" to (json["refresh_token"]?.jsonPrimitive?.content ?: "")
        )
    }

    /**
     * Open URL in system browser
     */
    private fun openBrowser(url: String) {
        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(URI(url))
                println("[$TAG] Opened browser for authentication")
            } else {
                // Fallback for systems where Desktop is not supported
                val os = System.getProperty("os.name").lowercase()
                val command = when {
                    os.contains("win") -> arrayOf("cmd", "/c", "start", url)
                    os.contains("mac") -> arrayOf("open", url)
                    else -> arrayOf("xdg-open", url)
                }
                Runtime.getRuntime().exec(command)
                println("[$TAG] Opened browser using runtime command")
            }
        } catch (e: Exception) {
            println("[$TAG] Failed to open browser: ${e.message}")
            throw Exception("Failed to open browser for authentication: ${e.message}")
        }
    }

    /**
     * Generate a random code verifier for PKCE
     */
    private fun generateCodeVerifier(): String {
        val bytes = ByteArray(32)
        SecureRandom().nextBytes(bytes)
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes)
    }

    /**
     * Generate code challenge from verifier using SHA-256
     */
    private fun generateCodeChallenge(verifier: String): String {
        val bytes = verifier.toByteArray(Charsets.US_ASCII)
        val digest = MessageDigest.getInstance("SHA-256").digest(bytes)
        return Base64.getUrlEncoder().withoutPadding().encodeToString(digest)
    }

    /**
     * Generate random state for CSRF protection
     */
    private fun generateState(): String {
        val bytes = ByteArray(16)
        SecureRandom().nextBytes(bytes)
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes)
    }

    /**
     * Create success HTML page shown after successful authentication
     */
    private fun createSuccessHtml(): String = """
        <!DOCTYPE html>
        <html>
        <head>
            <title>LeoConnect - Sign In Successful</title>
            <style>
                body {
                    font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
                    display: flex;
                    justify-content: center;
                    align-items: center;
                    min-height: 100vh;
                    margin: 0;
                    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                }
                .container {
                    text-align: center;
                    padding: 40px;
                    background: white;
                    border-radius: 16px;
                    box-shadow: 0 20px 60px rgba(0,0,0,0.3);
                    max-width: 400px;
                }
                .icon { font-size: 64px; margin-bottom: 20px; }
                h1 { color: #333; margin-bottom: 10px; }
                p { color: #666; }
            </style>
        </head>
        <body>
            <div class="container">
                <div class="icon">🦁</div>
                <h1>Welcome to LeoConnect!</h1>
                <p>Sign in successful. You can close this window and return to the app.</p>
            </div>
        </body>
        </html>
    """.trimIndent()

    /**
     * Create error HTML page
     */
    private fun createErrorHtml(error: String): String = """
        <!DOCTYPE html>
        <html>
        <head>
            <title>LeoConnect - Sign In Failed</title>
            <style>
                body {
                    font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
                    display: flex;
                    justify-content: center;
                    align-items: center;
                    min-height: 100vh;
                    margin: 0;
                    background: linear-gradient(135deg, #ff6b6b 0%, #ee5a5a 100%);
                }
                .container {
                    text-align: center;
                    padding: 40px;
                    background: white;
                    border-radius: 16px;
                    box-shadow: 0 20px 60px rgba(0,0,0,0.3);
                    max-width: 400px;
                }
                .icon { font-size: 64px; margin-bottom: 20px; }
                h1 { color: #333; margin-bottom: 10px; }
                p { color: #666; }
                .error { color: #dc3545; font-weight: 500; }
            </style>
        </head>
        <body>
            <div class="container">
                <div class="icon">❌</div>
                <h1>Sign In Failed</h1>
                <p class="error">$error</p>
                <p>Please close this window and try again.</p>
            </div>
        </body>
        </html>
    """.trimIndent()
}

