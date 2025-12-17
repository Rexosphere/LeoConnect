package com.rexosphere.leoconnect.di

import com.rexosphere.leoconnect.domain.service.AuthService
import org.koin.core.module.Module
import org.koin.dsl.module

// TODO: Implement iOS AuthService when needed
private class IosAuthService : AuthService {
    override suspend fun signInWithGoogle(): Result<String> {
        return Result.failure(Exception("iOS Sign-In not implemented yet"))
    }

    override suspend fun createUserWithEmailPassword(email: String, password: String): Result<String> {
        return Result.failure(Exception("iOS Email Sign-Up not implemented yet"))
    }

    override suspend fun signInWithEmailPassword(email: String, password: String): Result<String> {
        return Result.failure(Exception("iOS Email Sign-In not implemented yet"))
    }

    override suspend fun sendEmailVerification(): Result<Unit> {
        return Result.failure(Exception("iOS Email Verification not implemented yet"))
    }

    override suspend fun isEmailVerified(): Boolean = false

    override suspend fun reloadUser(): Result<Unit> {
        return Result.failure(Exception("iOS not implemented yet"))
    }

    override suspend fun getCurrentToken(forceRefresh: Boolean): String? = null
    override suspend fun signOut() {}
    override fun getCurrentUserId(): String? = null
    override fun isSignedIn(): Boolean = false
}

actual val platformModule: Module = module {
    single<AuthService> { IosAuthService() }
    single<com.rexosphere.leoconnect.domain.service.CryptoService> {
        com.rexosphere.leoconnect.data.service.CryptoServiceImpl()
    }
    single<com.rexosphere.leoconnect.data.source.local.LeoPreferences> {
        com.rexosphere.leoconnect.data.source.local.IosPreferences()
    }
    single<com.rexosphere.leoconnect.data.source.local.LocalDataSource> {
        com.rexosphere.leoconnect.data.source.local.PreferencesDataSource(get())
    }
    single<com.rexosphere.leoconnect.util.NetworkMonitor> {
        com.rexosphere.leoconnect.util.IosNetworkMonitor()
    }
}
