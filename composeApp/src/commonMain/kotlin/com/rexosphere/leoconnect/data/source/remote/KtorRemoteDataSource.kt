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
import io.ktor.client.request.delete
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

    suspend fun createPost(content: String, imageBytes: String?, clubId: String?, clubName: String?): Post {
        return client.post("$baseUrl/posts") {
            contentType(ContentType.Application.Json)
            getToken()?.let { token ->
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
                }
            }
            setBody(mapOf(
                "content" to content,
                "imageBytes" to imageBytes,
                "clubId" to clubId,
                "clubName" to clubName
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

    suspend fun updateUserProfile(leoId: String?, assignedClubId: String?, bio: String?): UserProfile {
        return client.patch("$baseUrl/users/me") {
            contentType(ContentType.Application.Json)
            getToken()?.let { token ->
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
                }
            }
            setBody(buildMap {
                leoId?.let { put("leoId", it) }
                assignedClubId?.let { put("assignedClubId", it) }
                bio?.let { put("bio", it) }
            })
        }.body()
    }

    suspend fun completeOnboarding(leoId: String?, assignedClubId: String?): UserProfile {
        return client.post("$baseUrl/users/me/quick-start") {
            contentType(ContentType.Application.Json)
            getToken()?.let { token ->
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
                }
            }
            setBody(buildMap {
                leoId?.let { put("leoId", it) }
                assignedClubId?.let { put("assignedClubId", it) }
            })
        }.body()
    }

    suspend fun followUser(userId: String): FollowResponse {
        return client.post("$baseUrl/users/$userId/follow") {
            getToken()?.let { token ->
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
                }
            }
        }.body()
    }

    suspend fun unfollowUser(userId: String): FollowResponse {
        return client.delete("$baseUrl/users/$userId/follow") {
            getToken()?.let { token ->
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
                }
            }
        }.body()
    }

    suspend fun followClub(clubId: String): FollowResponse {
        return client.post("$baseUrl/clubs/$clubId/follow") {
            getToken()?.let { token ->
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
                }
            }
        }.body()
    }

    suspend fun unfollowClub(clubId: String): FollowResponse {
        return client.delete("$baseUrl/clubs/$clubId/follow") {
            getToken()?.let { token ->
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
                }
            }
        }.body()
    }

    suspend fun getUserFollowers(userId: String, limit: Int = 50, offset: Int = 0): FollowersResponse {
        return client.get("$baseUrl/users/$userId/followers?limit=$limit&offset=$offset") {
            getToken()?.let { token ->
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
                }
            }
        }.body()
    }

    suspend fun getUserFollowing(userId: String, limit: Int = 50, offset: Int = 0): FollowersResponse {
        return client.get("$baseUrl/users/$userId/following?limit=$limit&offset=$offset") {
            getToken()?.let { token ->
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
                }
            }
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

    suspend fun search(query: String): SearchResponse {
        return client.get("$baseUrl/search?q=$query") {
            getToken()?.let { token ->
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
                }
            }
        }.body()
    }

    suspend fun getConversations(): List<com.rexosphere.leoconnect.domain.model.Conversation> {
        return client.get("$baseUrl/conversations") {
            getToken()?.let { token ->
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
                }
            }
        }.body()
    }

    suspend fun getMessages(userId: String): List<com.rexosphere.leoconnect.domain.model.Message> {
        return client.get("$baseUrl/messages/$userId") {
            getToken()?.let { token ->
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
                }
            }
        }.body()
    }

    suspend fun sendMessage(receiverId: String, content: String): com.rexosphere.leoconnect.domain.model.Message {
        return client.post("$baseUrl/messages") {
            contentType(ContentType.Application.Json)
            getToken()?.let { token ->
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
                }
            }
            setBody(mapOf(
                "receiverId" to receiverId,
                "content" to content
            ))
        }.body()
    }

    suspend fun deleteMessage(messageId: String): DeleteResponse {
        return client.delete("$baseUrl/messages/$messageId") {
            getToken()?.let { token ->
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
                }
            }
        }.body()
    }

    suspend fun deleteConversation(userId: String): DeleteResponse {
        return client.delete("$baseUrl/conversations/$userId") {
            getToken()?.let { token ->
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
                }
            }
        }.body()
    }

    suspend fun searchUsers(query: String): List<UserSearchResult> {
        return client.get("$baseUrl/search/users?q=$query") {
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

@kotlinx.serialization.Serializable
data class SearchResponse(
    val clubs: List<Club>,
    val districts: List<String>,
    val posts: List<Post>
)

@kotlinx.serialization.Serializable
data class DeleteResponse(
    val success: Boolean
)

@kotlinx.serialization.Serializable
data class UserSearchResult(
    val userId: String,
    val displayName: String,
    val photoUrl: String?
)

@kotlinx.serialization.Serializable
data class FollowResponse(
    val isFollowing: Boolean,
    val followersCount: Int? = null
)

@kotlinx.serialization.Serializable
data class FollowerUser(
    val uid: String,
    val displayName: String,
    val photoURL: String? = null,
    val leoId: String? = null,
    val isFollowing: Boolean? = false,
    val isMutualFollow: Boolean? = false
)

@kotlinx.serialization.Serializable
data class FollowersResponse(
    val followers: List<FollowerUser>,
    val total: Int,
    val hasMore: Boolean
)

