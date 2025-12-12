package com.rexosphere.leoconnect.util

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class IosNetworkMonitor : NetworkMonitor {
    private val _isOnline = MutableStateFlow(true) // Assume online for iOS
    override val isOnline: StateFlow<Boolean> = _isOnline.asStateFlow()
}
