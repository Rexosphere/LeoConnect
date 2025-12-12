package com.rexosphere.leoconnect.di

import com.rexosphere.leoconnect.data.service.DesktopAuthService
import com.rexosphere.leoconnect.domain.service.AuthService
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    single<AuthService> { DesktopAuthService() }
    single<com.rexosphere.leoconnect.data.source.local.LeoPreferences> {
        com.rexosphere.leoconnect.data.source.local.JvmPreferences()
    }
    single<com.rexosphere.leoconnect.data.source.local.LocalDataSource> {
        com.rexosphere.leoconnect.data.source.local.PreferencesDataSource(get())
    }
    single<com.rexosphere.leoconnect.util.NetworkMonitor> {
        com.rexosphere.leoconnect.util.JvmNetworkMonitor()
    }
}

