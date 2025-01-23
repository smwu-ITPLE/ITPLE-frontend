plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    // Kotlin serialization plugin for type safe routes and navigation arguments
    kotlin("plugin.serialization") version "2.0.21"
}

android {
    namespace = "com.smwuitple.maeumgil"
:    compileSdk = 34
    buildFeatures {
        viewBinding = true
    }

    defaultConfig {
        applicationId = "com.smwuitple.maeumgil"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.annotation)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation ("com.google.android.material:material:1.9.0")

    val nav_version = "2.8.4"

    // Jetpack Compose integration
    implementation("androidx.navigation:navigation-compose:$nav_version")

    // Views/Fragments integration
    implementation("androidx.navigation:navigation-fragment:$nav_version")
    implementation("androidx.navigation:navigation-ui:$nav_version")

    // Feature module support for Fragments
    implementation("androidx.navigation:navigation-dynamic-features-fragment:$nav_version")

    // Testing Navigation
    androidTestImplementation("androidx.navigation:navigation-testing:$nav_version")

    // JSON serialization library, works with the Kotlin serialization plugin
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
    implementation("com.anychart:AnyChart-Android:1.1.2")
}