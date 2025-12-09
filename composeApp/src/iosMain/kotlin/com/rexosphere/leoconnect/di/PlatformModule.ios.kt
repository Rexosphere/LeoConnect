package com.rexosphere.leoconnect.di

import com.rexosphere.leoconnect.domain.service.AuthService
import org.koin.core.module.Module
import org.koin.dsl.module

// TODO: Implement iOS AuthService when needed
private class IosAuthService : AuthService {
    override suspend fun signInWithGoogle(): Result<String> {
        return Result.failure(Exception("iOS Sign-In not implemented yet"))
    }

    override suspend fun getCurrentToken(forceRefresh: Boolean): String? = null
    override suspend fun signOut() {}
    override fun getCurrentUserId(): String? = null
    override fun isSignedIn(): Boolean = false
}

actual val platformModule: Module = module {
    single<AuthService> { IosAuthService() }
    single<com.rexosphere.leoconnect.data.source.local.LeoPreferences> {
        com.rexosphere.leoconnect.data.source.local.IosPreferences()
    }
    single<com.rexosphere.leoconnect.data.source.local.LocalDataSource> {
        com.rexosphere.leoconnect.data.source.local.PreferencesDataSource(get())
    }
}
