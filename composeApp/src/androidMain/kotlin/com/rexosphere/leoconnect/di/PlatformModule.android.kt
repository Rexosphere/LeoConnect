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
}

actual val platformModule: Module
    get() = throw IllegalStateException(
        "Android platformModule should be created using androidPlatformModule(context). " +
        "Make sure you're initializing Koin properly from MainActivity."
    )
