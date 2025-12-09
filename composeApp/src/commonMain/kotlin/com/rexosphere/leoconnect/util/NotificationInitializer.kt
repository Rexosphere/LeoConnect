package com.rexosphere.leoconnect.util

import com.rexosphere.leoconnect.data.repository.NotificationRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Helper class to initialize notification handling in the app
 */
class NotificationInitializer(
    private val notificationRepository: NotificationRepository,
    private val scope: CoroutineScope
) {
    
    fun initialize() {
        // Setup notification listeners
        notificationRepository.setupNotificationHandlers(
            onNotificationClicked = { data ->
                // Handle notification click
                println("Notification clicked with data: $data")
                // You can emit events or navigate based on the data
            }
        )
        
        // Register device token
        scope.launch {
            notificationRepository.registerDeviceToken()
        }
    }
    
    fun cleanup() {
        // Unregister device token on logout
        scope.launch {
            notificationRepository.unregisterDeviceToken()
        }
    }
}
