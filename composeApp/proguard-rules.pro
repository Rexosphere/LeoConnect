# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Enable aggressive optimization - allow R8 to shorten class names
-allowaccessmodification
-repackageclasses ''

# Keep Compose runtime - only what's needed for reflection
-keep class androidx.compose.runtime.** { *; }
-keep class androidx.compose.ui.platform.** { *; }
-dontwarn androidx.compose.**

# Keep Kotlin metadata for reflection
-keep class kotlin.Metadata { *; }
-keepattributes *Annotation*, InnerClasses, Signature, Exception
-dontnote kotlinx.serialization.AnnotationsKt

# Kotlinx Serialization - only keep serializers
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

# Ktor - minimal keep rules
-keep class io.ktor.client.** { *; }
-keep class io.ktor.http.** { *; }
-keep class kotlinx.coroutines.** { *; }
-dontwarn kotlinx.atomicfu.**
-dontwarn io.netty.**
-dontwarn com.typesafe.**
-dontwarn org.slf4j.**
-dontwarn java.lang.management.**
-dontwarn javax.management.**

# Koin - only keep what's accessed via reflection
-keep class org.koin.core.** { *; }
-keep class org.koin.dsl.** { *; }
-dontwarn org.koin.**

# Firebase - keep only essential classes
-keep class com.google.firebase.messaging.** { *; }
-keep class com.google.firebase.auth.** { *; }
-keep class com.google.android.gms.common.** { *; }
-keep class com.google.android.gms.tasks.** { *; }
-dontwarn com.google.firebase.**
-dontwarn com.google.android.gms.**

# KMPNotifier - keep interfaces and essential classes
-keep class com.mmk.kmpnotifier.notification.** { *; }
-keep interface com.mmk.kmpnotifier.** { *; }
-dontwarn com.mmk.kmpnotifier.**

# Keep only serializable data models (others can be obfuscated)
-keep @kotlinx.serialization.Serializable class com.rexosphere.leoconnect.data.model.** { *; }

# Voyager Navigator - minimal keep
-keep class cafe.adriel.voyager.core.screen.Screen { *; }
-keep class cafe.adriel.voyager.navigator.** { *; }
-dontwarn cafe.adriel.voyager.**

# Kamel Image Loading
-keep class media.kamel.core.** { *; }
-dontwarn media.kamel.**

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
    public static *** w(...);
}

# Aggressive optimization settings
-optimizationpasses 7
-dontusemixedcaseclassnames
-verbose
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
-overloadaggressively
