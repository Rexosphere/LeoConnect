package com.rexosphere.leoconnect.di

import com.rexosphere.leoconnect.data.repository.LeoRepositoryImpl
import com.rexosphere.leoconnect.data.source.remote.KtorRemoteDataSource
import com.rexosphere.leoconnect.domain.repository.LeoRepository
import com.rexosphere.leoconnect.domain.service.AuthService
import com.rexosphere.leoconnect.presentation.auth.LoginScreenModel
import com.rexosphere.leoconnect.presentation.home.HomeScreenModel
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val commonModule = module {
    // Firebase Auth
    single { Firebase.auth }

    // HttpClient with JSON support
    single {
        HttpClient {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    prettyPrint = true
                    isLenient = true
                })
            }
        }
    }

    // Data sources and repository
    single {
        KtorRemoteDataSource(
            client = get(),
            getToken = { get<AuthService>().getCurrentToken() }
        )
    }
    single<LeoRepository> { LeoRepositoryImpl(get(), get(), get()) }

    // Leo AI Service
    single {
        com.rexosphere.leoconnect.data.source.remote.LeoAiService(get())
    }

    // Notification service and repository
    single<com.rexosphere.leoconnect.data.service.NotificationService> {
        com.rexosphere.leoconnect.data.service.NotificationServiceImpl(
            httpClient = get(),
            baseUrl = "https://leoconnect.rexosphere.com",
            getToken = { get<AuthService>().getCurrentToken() }
        )
    }
    single<com.rexosphere.leoconnect.data.repository.NotificationRepository> {
        com.rexosphere.leoconnect.data.repository.NotificationRepositoryImpl(get())
    }

    // ViewModels
    factory { LoginScreenModel(get()) }
    factory { com.rexosphere.leoconnect.presentation.auth.OnboardingScreenModel(get()) }
    factory { HomeScreenModel(get()) }
    factory { com.rexosphere.leoconnect.presentation.tabs.ClubsScreenModel(get()) }
    factory { com.rexosphere.leoconnect.presentation.tabs.ProfileScreenModel(get()) }
    factory { com.rexosphere.leoconnect.presentation.postdetail.PostDetailScreenModel(get()) }
    factory { com.rexosphere.leoconnect.presentation.districtdetail.DistrictDetailScreenModel() }
    factory { com.rexosphere.leoconnect.presentation.clubdetail.ClubDetailScreenModel(get()) }
    factory { com.rexosphere.leoconnect.presentation.userprofile.UserProfileScreenModel(get()) }
    factory { com.rexosphere.leoconnect.presentation.search.SearchScreenModel(get()) }
    factory { com.rexosphere.leoconnect.presentation.createpost.CreatePostScreenModel(get()) }
    factory { com.rexosphere.leoconnect.presentation.createevent.CreateEventScreenModel(get()) }
    factory { com.rexosphere.leoconnect.presentation.chat.ChatScreenModel(get()) }
    factory { com.rexosphere.leoconnect.presentation.chat.LeoAiChatScreenModel(get()) }
    factory { com.rexosphere.leoconnect.presentation.tabs.MessagesScreenModel(get()) }
    factory { com.rexosphere.leoconnect.presentation.notifications.NotificationsScreenModel(get()) }
}

// Platform-specific modules will be added via expect/actual
expect val platformModule: Module
