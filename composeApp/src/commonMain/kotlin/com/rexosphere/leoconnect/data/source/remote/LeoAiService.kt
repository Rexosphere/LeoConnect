package com.rexosphere.leoconnect.data.source.remote

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AiChatRequest(
    val question: String
)

@Serializable
data class AiChatResponse(
    val answer: String,
    val question: String,
    val success: Boolean
)

class LeoAiService(private val httpClient: HttpClient) {
    private val baseUrl = "http://api.sangeethnipun.cf"

    suspend fun askQuestion(question: String): AiChatResponse {
        val response = httpClient.post("$baseUrl/ask") {
            contentType(ContentType.Application.Json)
            setBody(AiChatRequest(question = question))
        }
        return response.body()
    }
}
