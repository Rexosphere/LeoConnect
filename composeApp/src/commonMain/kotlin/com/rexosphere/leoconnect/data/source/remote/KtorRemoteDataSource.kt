package com.rexosphere.leoconnect.data.source.remote

import com.rexosphere.leoconnect.domain.model.Club
import com.rexosphere.leoconnect.domain.model.Post
import com.rexosphere.leoconnect.domain.model.UserProfile
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.patch
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType

class KtorRemoteDataSource(
    private val client: HttpClient,
    private val getToken: suspend () -> String?
) {
    private val baseUrl = "https://leoconnect.rexosphere.com"

    /**
     * Authenticate with Google and get user profile
     * @param firebaseToken Firebase ID token from Google Sign-In
     */
    suspend fun googleSignIn(firebaseToken: String): UserProfile {
        return client.post("$baseUrl/auth/google") {
            headers {
                append(HttpHeaders.Authorization, "Bearer $firebaseToken")
            }
        }.body()
    }

    suspend fun getHomeFeed(limit: Int): List<Post> {
        return client.get("$baseUrl/feed?limit=$limit") {
            getToken()?.let { token ->
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
                }
            }
        }.body()
    }

    suspend fun likePost(postId: String) {
        client.post("$baseUrl/posts/$postId/like") {
            getToken()?.let { token ->
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
                }
            }
        }
    }

    suspend fun createPost(content: String, imageUrl: String?): Post {
        return client.post("$baseUrl/posts") {
            contentType(ContentType.Application.Json)
            getToken()?.let { token ->
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
                }
            }
            setBody(mapOf(
                "content" to content,
                "imageUrl" to imageUrl
            ))
        }.body()
    }

    suspend fun getDistricts(): List<String> {
        return client.get("$baseUrl/districts").body()
    }

    suspend fun getClubsByDistrict(district: String): List<Club> {
        return client.get("$baseUrl/clubs?district=$district").body()
    }

    suspend fun getUserProfile(uid: String? = null): UserProfile {
        val url = if (uid != null) "$baseUrl/users/me?uid=$uid" else "$baseUrl/users/me"
        return client.get(url) {
            getToken()?.let { token ->
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
                }
            }
        }.body()
    }

    suspend fun updateUserProfile(leoId: String?, assignedClubId: String?): UserProfile {
        return client.patch("$baseUrl/users/me") {
            contentType(ContentType.Application.Json)
            getToken()?.let { token ->
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
                }
            }
            setBody(mapOf(
                "leoId" to leoId,
                "assignedClubId" to assignedClubId
            ))
        }.body()
    }

    suspend fun getComments(postId: String): CommentResponse {
        return client.get("$baseUrl/posts/$postId/comments") {
            getToken()?.let { token ->
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
                }
            }
        }.body()
    }

    suspend fun addComment(postId: String, content: String): CommentResponseWrapper {
        return client.post("$baseUrl/posts/$postId/comments") {
            contentType(ContentType.Application.Json)
            getToken()?.let { token ->
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
                }
            }
            setBody(mapOf("content" to content))
        }.body()
    }

    suspend fun getClubPosts(clubId: String): List<Post> {
        return client.get("$baseUrl/clubs/$clubId/posts") {
            getToken()?.let { token ->
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
                }
            }
        }.body()
    }

    suspend fun getUserProfileById(userId: String): UserProfile {
        return client.get("$baseUrl/users/$userId") {
            getToken()?.let { token ->
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
                }
            }
        }.body()
    }

    suspend fun getUserPosts(userId: String): List<Post> {
        return client.get("$baseUrl/users/$userId/posts") {
            getToken()?.let { token ->
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
                }
            }
        }.body()
    }
}

@kotlinx.serialization.Serializable
data class CommentResponse(
    val comments: List<com.rexosphere.leoconnect.domain.model.Comment>,
    val total: Int,
    val hasMore: Boolean
)

@kotlinx.serialization.Serializable
data class CommentResponseWrapper(
    val comment: com.rexosphere.leoconnect.domain.model.Comment
)

