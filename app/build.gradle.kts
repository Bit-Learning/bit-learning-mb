import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.jetbrains.kotlin.plugin.compose)
    id("com.google.devtools.ksp")
    // id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
}

android {
    val appId = "com.app.bitlearning"
    val compileSdkVersion = 35
    val targetSdkVersion = 35
    val minSdkVersion = 24

    namespace = appId
    compileSdk = compileSdkVersion

    defaultConfig {
        applicationId = appId
        minSdk = minSdkVersion
        targetSdk = targetSdkVersion
        versionCode = 1
        versionName = "0.0.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        manifestPlaceholders["appAuthRedirectScheme"] = appId

        // Read from local.properties
        val properties = Properties()
        if (rootProject.file("local.properties").exists()) {
            properties.load(project.rootProject.file("local.properties").inputStream())
        } else {
            throw RuntimeException("local.properties file not found")
        }

        val error = "variable not found in local.properties"

        // Define BuildConfig fields without revealing fallback values
        buildConfigField(
            "String",
            "YOUR_API_KEY",
            "\"${properties.getProperty("my.api.key") ?: throw RuntimeException(error)}\"",
        )
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
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
    buildFeatures {
        compose = true

        // Environment variable configuration
        buildConfig = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.play.services.auth) // hoặc bản mới nhất
    implementation(libs.kotlinx.coroutines.play.services)

    // compose platform
    implementation(platform(libs.androidx.compose.bom))

    // ui, preview & material
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.navigation.compose)

    implementation(libs.logging.interceptor)
    implementation(libs.androidx.work.runtime.ktx)

    ksp(libs.hilt.android.compiler)
    ksp(libs.androidx.room.compiler)

    implementation(libs.hilt.android)
    implementation(libs.androidx.hilt.navigation.compose)

    implementation(libs.composeIcons.fontAwesome)

    implementation(libs.jackson.datatype.jsr310)
    implementation(libs.jackson.annotations)
    implementation(libs.jackson.core)
    implementation(libs.jackson.databind)

    implementation(libs.okhttp)

    implementation(libs.gson)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.okhttp.urlconnection)

    // accompanist
    implementation(libs.accompanist.systemuicontroller)
    implementation(libs.accompanist.pager)

    implementation(libs.androidx.core.splashscreen)
    implementation(libs.coil.compose)

    implementation(libs.coil.network.okhttp)

    // QR Code Scanning
    implementation(libs.androidx.camera.view)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.barcode.scanning)

    // splashscreen
    implementation(libs.androidx.core.splashscreen)

    implementation(libs.appauth)
    implementation(libs.androidx.browser)
    implementation(libs.androidx.animation)
    implementation(libs.stripe.android.v2250)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)

    // debug libraries
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
