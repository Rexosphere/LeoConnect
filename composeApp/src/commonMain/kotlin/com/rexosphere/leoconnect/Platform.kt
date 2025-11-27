package com.rexosphere.leoconnect

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform