package com.rexosphere.leoconnect.data.service

import com.mmk.kmpnotifier.notification.NotifierManager
import com.mmk.kmpnotifier.notification.PayloadData
import com.rexosphere.leoconnect.data.model.Notification
import com.rexosphere.leoconnect.data.model.NotificationListResponse
import com.rexosphere.leoconnect.data.model.NotificationPreferences
import com.rexosphere.leoconnect.data.model.NotificationResponse
import com.rexosphere.leoconnect.data.model.NotificationTokenRequest
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.http.ContentType
import io.ktor.http.contentType

interface NotificationService {
    suspend fun registerToken(token: String, deviceId: String? = null, deviceType: String? = null): Result<NotificationResponse>
    suspend fun removeToken(token: String): Result<NotificationResponse>
    suspend fun getNotifications(limit: Int = 50, offset: Int = 0, unreadOnly: Boolean = false): Result<NotificationListResponse>
    suspend fun markAsRead(notificationId: String): Result<NotificationResponse>
    suspend fun markAllAsRead(): Result<NotificationResponse>
    suspend fun getPreferences(): Result<NotificationPreferences>
    suspend fun updatePreferences(preferences: NotificationPreferences): Result<NotificationResponse>
    fun setupNotificationListeners(
        onNewToken: (String) -> Unit = {},
        onNotificationReceived: (title: String?, body: String?, data: PayloadData) -> Unit = { _, _, _ -> },
        onNotificationClicked: (data: PayloadData) -> Unit = {}
    )
    suspend fun getCurrentToken(): String?
}

class NotificationServiceImpl(
    private val httpClient: HttpClient,
    private val baseUrl: String,
    private val getToken: suspend () -> String?
) : NotificationService {

    override suspend fun registerToken(
        token: String,
        deviceId: String?,
        deviceType: String?
    ): Result<NotificationResponse> = runCatching {
        httpClient.post("$baseUrl/notifications/token") {
            contentType(ContentType.Application.Json)
            getToken()?.let { authToken ->
                headers {
                    append(io.ktor.http.HttpHeaders.Authorization, "Bearer $authToken")
                }
            }
            setBody(NotificationTokenRequest(token, deviceId, deviceType))
        }.body()
    }

    override suspend fun removeToken(token: String): Result<NotificationResponse> = runCatching {
        httpClient.delete("$baseUrl/notifications/token") {
            contentType(ContentType.Application.Json)
            getToken()?.let { authToken ->
                headers {
                    append(io.ktor.http.HttpHeaders.Authorization, "Bearer $authToken")
                }
            }
            setBody(mapOf("token" to token))
        }.body()
    }

    override suspend fun getNotifications(
        limit: Int,
        offset: Int,
        unreadOnly: Boolean
    ): Result<NotificationListResponse> = runCatching {
        httpClient.get("$baseUrl/notifications") {
            getToken()?.let { authToken ->
                headers {
                    append(io.ktor.http.HttpHeaders.Authorization, "Bearer $authToken")
                }
            }
            parameter("limit", limit)
            parameter("offset", offset)
            parameter("unreadOnly", unreadOnly)
        }.body()
    }

    override suspend fun markAsRead(notificationId: String): Result<NotificationResponse> = runCatching {
        httpClient.patch("$baseUrl/notifications/$notificationId/read") {
            getToken()?.let { authToken ->
                headers {
                    append(io.ktor.http.HttpHeaders.Authorization, "Bearer $authToken")
                }
            }
        }.body()
    }

    override suspend fun markAllAsRead(): Result<NotificationResponse> = runCatching {
        httpClient.post("$baseUrl/notifications/read-all") {
            getToken()?.let { authToken ->
                headers {
                    append(io.ktor.http.HttpHeaders.Authorization, "Bearer $authToken")
                }
            }
        }.body()
    }

    override suspend fun getPreferences(): Result<NotificationPreferences> = runCatching {
        httpClient.get("$baseUrl/notifications/preferences") {
            getToken()?.let { authToken ->
                headers {
                    append(io.ktor.http.HttpHeaders.Authorization, "Bearer $authToken")
                }
            }
        }.body()
    }

    override suspend fun updatePreferences(preferences: NotificationPreferences): Result<NotificationResponse> = runCatching {
        httpClient.patch("$baseUrl/notifications/preferences") {
            contentType(ContentType.Application.Json)
            getToken()?.let { authToken ->
                headers {
                    append(io.ktor.http.HttpHeaders.Authorization, "Bearer $authToken")
                }
            }
            setBody(preferences)
        }.body()
    }

    override fun setupNotificationListeners(
        onNewToken: (String) -> Unit,
        onNotificationReceived: (title: String?, body: String?, data: PayloadData) -> Unit,
        onNotificationClicked: (data: PayloadData) -> Unit
    ) {
        NotifierManager.addListener(object : NotifierManager.Listener {
            override fun onNewToken(token: String) {
                println("FCM Token: $token")
                onNewToken(token)
            }

            override fun onPushNotificationWithPayloadData(
                title: String?,
                body: String?,
                data: PayloadData
            ) {
                println("Notification received - Title: $title, Body: $body, Data: $data")
                onNotificationReceived(title, body, data)
            }

            override fun onNotificationClicked(data: PayloadData) {
                println("Notification clicked - Data: $data")
                onNotificationClicked(data)
            }
        })
    }

    override suspend fun getCurrentToken(): String? {
        return try {
            NotifierManager.getPushNotifier().getToken()
        } catch (e: Exception) {
            println("Error getting FCM token: ${e.message}")
            null
        }
    }
}
