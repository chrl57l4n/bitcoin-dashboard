plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "de.bitcoindashboard"
    compileSdk = 34

    defaultConfig {
        applicationId = "de.bitcoindashboard"
        minSdk = 29          // Android 10+ – alle Snapdragon 8 Geräte
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        // ABI Filter: nur ARM64 für Snapdragon 8 (kein x86 Overhead)
        ndk {
            abiFilters += listOf("arm64-v8a")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            // Snapdragon-optimiert: volle R8-Optimierung
            isDebuggable = false
        }
        debug {
            isDebuggable = true
            applicationIdSuffix = ".debug"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
        // Optimierungen für ARM64
        freeCompilerArgs += listOf("-Xopt-in=kotlin.RequiresOptIn")
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

    // Snapdragon 8 nutzt hardware-accelerated rendering
    // Edge-to-edge Display (punch-hole / curved screens)
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.swiperefreshlayout)
}
