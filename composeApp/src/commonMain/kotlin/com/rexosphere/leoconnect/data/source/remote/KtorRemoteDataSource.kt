package com.rexosphere.leoconnect.data.source.remote

import com.rexosphere.leoconnect.domain.model.Club
import com.rexosphere.leoconnect.domain.model.Post
import com.rexosphere.leoconnect.domain.model.UserProfile
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.post
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
}

