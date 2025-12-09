# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Keep all Compose runtime classes
-keep class androidx.compose.** { *; }
-keep class androidx.compose.runtime.** { *; }
-keep class androidx.compose.ui.** { *; }

# Keep Kotlin metadata
-keep class kotlin.Metadata { *; }
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

# Kotlinx Serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keep,includedescriptorclasses class com.rexosphere.leoconnect.**$$serializer { *; }
-keepclassmembers class com.rexosphere.leoconnect.** {
    *** Companion;
}
-keepclasseswithmembers class com.rexosphere.leoconnect.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Keep data classes for serialization
-keep @kotlinx.serialization.Serializable class * { *; }

# Ktor
-keep class io.ktor.** { *; }
-keep class kotlinx.coroutines.** { *; }
-dontwarn kotlinx.atomicfu.**
-dontwarn io.netty.**
-dontwarn com.typesafe.**
-dontwarn org.slf4j.**
-dontwarn java.lang.management.**
-dontwarn javax.management.**

# Koin
-keep class org.koin.** { *; }
-keep class org.koin.core.** { *; }
-keep class org.koin.dsl.** { *; }

# Firebase
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.firebase.**

# Firebase Cloud Messaging (for notifications)
-keep class com.google.firebase.messaging.** { *; }
-keep class com.google.firebase.iid.** { *; }

# KMPNotifier
-keep class com.mmk.kmpnotifier.** { *; }
-keep interface com.mmk.kmpnotifier.** { *; }
-dontwarn com.mmk.kmpnotifier.**

# Keep notification data models
-keep class com.rexosphere.leoconnect.data.model.Notification { *; }
-keep class com.rexosphere.leoconnect.data.model.NotificationListResponse { *; }
-keep class com.rexosphere.leoconnect.data.model.NotificationPreferences { *; }
-keep class com.rexosphere.leoconnect.data.model.NotificationResponse { *; }
-keep class com.rexosphere.leoconnect.data.model.NotificationTokenRequest { *; }

# Voyager Navigator
-keep class cafe.adriel.voyager.** { *; }

# Kamel Image Loading
-keep class media.kamel.** { *; }

# Keep application classes
-keep class com.rexosphere.leoconnect.** { *; }

# Keep native methods
-keepclasseswithmembernames class * {
    native <methods>;
}

# Keep custom view constructors
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

# Keep enum classes
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Keep Parcelable implementations
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# Remove logging in release builds
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}

# Optimize
-optimizationpasses 5
-dontusemixedcaseclassnames
-verbose
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
