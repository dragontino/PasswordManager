plugins {
    id("com.android.library")
    kotlin("android")
    id("com.google.devtools.ksp")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.security.passwordmanager.data"
    compileSdk = 34

    defaultConfig {
        minSdk = 26
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
    }
}

dependencies {

    implementation(project(mapOf("path" to ":domain")))
    implementation("com.google.android.gms:play-services-auth:21.0.0")

    //Retrofit
    implementation(Dependencies.Retrofit.retrofit)
    implementation(Dependencies.Retrofit.gsonConverter)
    implementation(Dependencies.Retrofit.scalarisConverter)

    // Room
    implementation(Dependencies.Room.runtime)
    implementation(Dependencies.Room.ktx)
    ksp(Dependencies.Room.compiler)
    androidTestImplementation(Dependencies.Room.testing)

    //Firebase
    implementation(platform(Dependencies.Firebase.bom))
    implementation(Dependencies.Firebase.analytics)
    implementation(Dependencies.Firebase.auth)
    implementation(Dependencies.Firebase.database)
    implementation(Dependencies.Firebase.core)
    implementation(Dependencies.Firebase.functions)
}