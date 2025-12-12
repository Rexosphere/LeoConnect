package com.rexosphere.leoconnect.di

import android.content.Context
import com.rexosphere.leoconnect.data.service.AndroidAuthService
import com.rexosphere.leoconnect.domain.service.AuthService
import org.koin.core.module.Module
import org.koin.dsl.module

fun androidPlatformModule(context: Context): Module = module {
    single<Context> { context }
    single<AuthService> {
        AndroidAuthService(
            context = context.applicationContext,
            firebaseAuth = get()
        )
    }
    single<com.rexosphere.leoconnect.data.source.local.LeoPreferences> {
        com.rexosphere.leoconnect.data.source.local.AndroidPreferences(context.applicationContext)
    }
    single<com.rexosphere.leoconnect.data.source.local.LocalDataSource> {
        com.rexosphere.leoconnect.data.source.local.PreferencesDataSource(get())
    }
    single<com.rexosphere.leoconnect.util.NetworkMonitor> {
        com.rexosphere.leoconnect.util.AndroidNetworkMonitor(context.applicationContext)
    }
}

actual val platformModule: Module
    get() = throw IllegalStateException(
        "Android platformModule should be created using androidPlatformModule(context). " +
        "Make sure you're initializing Koin properly from MainActivity."
    )
