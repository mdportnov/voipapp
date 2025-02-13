plugins {
    id("kotlin-android")
    id("com.android.application")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("com.google.gms.google-services")
}

android {
    compileSdk = 31

    defaultConfig {
        applicationId = "ru.mephi.voip"
        minSdk = 23
        targetSdk = 30 // 31 causes error with pendingIntents and abto (should be 30)
        versionCode = 28
        versionName = "2.0.4"
        multiDexEnabled = true
        signingConfig = signingConfigs.getByName("debug")
    }

    buildTypes {
        getByName("release") {
            isShrinkResources = true
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            isDebuggable = false
        }

        getByName("debug") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            isDebuggable = true
            applicationIdSuffix = ".debug"
        }
    }

    buildFeatures {
        compose = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.1.0-beta04"
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        kotlinOptions.freeCompilerArgs += "-opt-in=kotlin.RequiresOptIn"
    }
}

dependencies {
    // Androidx
    implementation("androidx.core:core-ktx:1.8.0")
    implementation("androidx.appcompat:appcompat:1.4.2")
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.4.1")

    // Kotlinx
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.6.21")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.3.2")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.1")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime-jvm:0.3.2")

    // Compose
    implementation("androidx.compose.ui:ui-tooling:1.1.1")
    implementation("androidx.activity:activity-compose:1.4.0")
    implementation("androidx.compose.compiler:compiler:1.2.0-beta01")
    implementation("androidx.compose.material:material:1.1.1")
    implementation("androidx.compose.material3:material3:1.0.0-alpha11")
    implementation("androidx.compose.material:material-icons-extended:1.1.1")
    implementation("androidx.compose.foundation:foundation:1.2.0-beta01")
    implementation("androidx.navigation:navigation-compose:2.4.2")
    implementation("androidx.constraintlayout:constraintlayout-compose:1.0.1")

    implementation("com.google.accompanist:accompanist-swiperefresh:0.23.1")
    implementation("com.google.accompanist:accompanist-permissions:0.23.1")
    implementation("com.google.accompanist:accompanist-navigation-animation:0.23.1")
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.23.1")
    implementation("com.google.accompanist:accompanist-permissions:0.23.1")
    implementation("com.google.android.material:material:1.6.1")

    implementation("com.github.judemanutd:autostarter:1.1.0")

    // Network
    implementation("io.ktor:ktor-client-okhttp:1.6.2")
    implementation("io.ktor:ktor-client-serialization:1.6.7")
    implementation("io.ktor:ktor-client-core:1.6.8")
    implementation("io.ktor:ktor-client-json:1.6.7")

    // Database
    implementation("com.squareup.sqldelight:runtime:1.5.3")
    implementation("com.squareup.sqldelight:coroutines-extensions:1.5.3")

    // Coil
    implementation("io.coil-kt:coil:2.1.0")
    implementation("io.coil-kt:coil-compose:2.1.0")

    // DI
    implementation("io.insert-koin:koin-android:3.1.5")
    implementation("io.insert-koin:koin-androidx-compose:3.1.5")

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:29.0.0"))
    implementation("com.google.firebase:firebase-analytics-ktx")

    // Preferences
    implementation("com.github.JamalMulla:ComposePrefs:1.0.0")
    implementation("androidx.preference:preference-ktx:1.2.0")
    implementation("androidx.datastore:datastore-preferences:1.0.0")
    implementation("androidx.datastore:datastore-preferences-core:1.0.0")

    implementation("com.jakewharton.timber:timber:5.0.1")
    implementation("com.github.leonardoxh:keystore-ultimate:1.3.0")

    // Splash
    implementation("androidx.core:core-splashscreen:1.0.0-beta01")

    implementation("androidx.compose.runtime:runtime-livedata:1.1.1")

    implementation(project(":shared"))
    implementation(files("aars/abto_android_voip_sdk.aar"))
    implementation(kotlin("reflect"))
}