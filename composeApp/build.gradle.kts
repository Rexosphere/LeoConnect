import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.googleServices)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
            export(libs.kmpnotifier)
        }
    }

    jvm()

    // Fix KLIB resolver conflicts by preferring Compose Multiplatform versions
    targets.configureEach {
        compilations.configureEach {
            compileTaskProvider.configure {
                compilerOptions {
                    freeCompilerArgs.add("-Xexpect-actual-classes")
                }
            }
        }
    }

    sourceSets {
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.ktor.client.okhttp)
            implementation(libs.androidx.credentials)
            implementation(libs.androidx.credentials.play.services)
            implementation(libs.googleid)
            implementation("androidx.core:core-splashscreen:1.0.1")
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.materialIconsExtended)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)

            // Koin
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)

            // Voyager
            implementation(libs.voyager.navigator)
            implementation(libs.voyager.screenModel)
            implementation(libs.voyager.tabNavigator)
            implementation(libs.voyager.transitions)
            implementation(libs.voyager.koin)

            // Ktor
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)

            // Kotlinx DateTime
            implementation(libs.kotlinx.datetime)

            // Kamel
            implementation(libs.kamel.image)
            implementation(libs.kamel.image.default)

            // Haze (Glassmorphic effects)
            implementation(libs.haze.materials)

            // Firebase
            implementation(libs.firebase.auth)
            implementation(libs.firebase.common)

            // KMPNotifier for push notifications
            api(libs.kmpnotifier)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutinesSwing)
        }
    }
}

android {
    namespace = "com.rexosphere.leoconnect"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.rexosphere.leoconnect"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 2
        versionName = "2.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    signingConfigs {
        create("release") {
            val storeFilePath = System.getenv("SIGNING_STORE_FILE")
            if (storeFilePath != null) {
                storeFile = file(storeFilePath)
                storePassword = System.getenv("SIGNING_STORE_PASSWORD")
                keyAlias = System.getenv("SIGNING_KEY_ALIAS")
                keyPassword = System.getenv("SIGNING_KEY_PASSWORD")
            }
        }
    }

    buildTypes {
        getByName("debug") {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-DEBUG"
        }
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            if (System.getenv("SIGNING_STORE_FILE") != null) {
                signingConfig = signingConfigs.getByName("release")
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

// Fix library version conflicts by excluding conflicting androidx dependencies
configurations.all {
    resolutionStrategy {
        // Prefer Compose Multiplatform versions over androidx versions
        force("org.jetbrains.compose.runtime:runtime:1.9.1")
        force("org.jetbrains.compose.runtime:runtime-saveable:1.9.1")
        force("org.jetbrains.androidx.lifecycle:lifecycle-common:2.9.5")
        force("org.jetbrains.androidx.lifecycle:lifecycle-runtime:2.9.5")
        force("org.jetbrains.androidx.lifecycle:lifecycle-runtime-compose:2.9.5")
        force("org.jetbrains.androidx.lifecycle:lifecycle-viewmodel:2.9.5")
        force("org.jetbrains.androidx.lifecycle:lifecycle-viewmodel-savedstate:2.9.5")
        force("org.jetbrains.androidx.savedstate:savedstate:1.2.0")
        force("org.jetbrains.compose.annotation-internal:annotation:1.9.1")
        force("org.jetbrains.compose.collection-internal:collection:1.9.1")
    }
}

dependencies {
    debugImplementation(compose.uiTooling)

    // Firebase dependencies for Android
    "implementation"(platform(libs.firebase.bom))
    "implementation"(libs.firebase.auth.android)
    "implementation"(libs.firebase.common.android)
    "implementation"("com.google.firebase:firebase-messaging")
}

compose.desktop {
    application {
        mainClass = "com.rexosphere.leoconnect.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "com.rexosphere.leoconnect"
            packageVersion = "2.0.0"
        }
    }
}
