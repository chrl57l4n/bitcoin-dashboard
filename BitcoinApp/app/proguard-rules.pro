# Bitcoin Dashboard ProGuard Rules

# WebView JavaScript Interface beibehalten
-keepclassmembers class de.bitcoindashboard.AndroidBridge {
    @android.webkit.JavascriptInterface <methods>;
}
-keepattributes JavascriptInterface

# Kotlin
-keep class kotlin.** { *; }
-keepclassmembers class **$WhenMappings { *; }

# AndroidX
-keep class androidx.** { *; }
-dontwarn androidx.**

# Snapdragon WebView Optimierung: WebGL & Canvas beibehalten
-keep class android.webkit.** { *; }
