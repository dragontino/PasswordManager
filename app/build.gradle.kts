@file:Suppress("UnstableApiUsage")

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("com.google.gms.google-services")
}


android {
    namespace = "com.security.passwordmanager"
    compileSdk = 33
    buildToolsVersion = "30.0.3"

    defaultConfig {
        applicationId = "com.security.passwordmanager"
        minSdk = 29
        targetSdk = 33
        versionCode = 6
        versionName = "6.3.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs = freeCompilerArgs + "-Xjvm-default=all"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.5"
    }

    packagingOptions {
        resources {
            excludes.add("/META-INF/{AL2.0,LGPL2.1}")
        }
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.annotation:annotation:1.6.0")
    implementation("com.google.android.material:material:1.8.0")
    implementation("androidx.preference:preference-ktx:1.2.0")
    implementation("androidx.core:core-ktx:1.10.0")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("com.google.android.gms:play-services-auth:20.5.0")

    implementation("com.squareup.picasso:picasso:2.71828")
    implementation("io.coil-kt:coil-compose:2.3.0")

    // Firebase
    implementation(platform(Dependencies.Firebase.bom))
    implementation(Dependencies.Firebase.analytics)
    implementation(Dependencies.Firebase.auth)
    implementation(Dependencies.Firebase.database)
    implementation(Dependencies.Firebase.core)


    //Lifecycle
    implementation(Dependencies.Lifecycle.liveData)
    implementation(Dependencies.Lifecycle.viewModel)
    implementation(Dependencies.Lifecycle.runtime)


    // Compose
    implementation(Dependencies.Compose.ui)
    implementation(Dependencies.Compose.tooling)
    implementation(Dependencies.Compose.toolingPreview)
    implementation(Dependencies.Compose.foundation)
    implementation(Dependencies.Compose.material3)
    implementation(Dependencies.Compose.material)
    implementation(Dependencies.Compose.icons)
    implementation(Dependencies.Compose.iconsExtended)
    implementation(Dependencies.Compose.navigation)
    implementation(Dependencies.Compose.activity)
    implementation(Dependencies.Compose.viewModel)
    implementation(Dependencies.Compose.liveData)
    implementation(Dependencies.Compose.rxJava)
    implementation(Dependencies.Compose.systemUiController)
    implementation(Dependencies.Compose.animation)
    implementation(Dependencies.Compose.constraintLayout)

    debugImplementation(Dependencies.Compose.uiTooling)


    //Room
    implementation(Dependencies.Room.runtime)
    implementation(Dependencies.Room.ktx)
    kapt(Dependencies.Room.compiler)
    androidTestImplementation(Dependencies.Room.testing)


    // UI Tests
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(Dependencies.Compose.jUnit)
    testImplementation("org.testng:testng:7.7.1")

    Dependencies.Coroutines.values().forEach {
        api(it.path)
    }

    // Other Libs

    implementation("io.github.vanpra.compose-material-dialogs:datetime:0.9.0")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("me.onebone:toolbar-compose:2.3.5")
}