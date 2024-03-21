plugins {
    id("com.android.application")
    kotlin("android")
    id("com.google.devtools.ksp")
}


val appVersionName by extra("7.0.0")
val appVersionDate by extra("25/02/2024")
val debugVersionExt by extra("beta3")


android {
    namespace = "com.security.passwordmanager"
    compileSdk = 34

    defaultConfig {
        minSdk = 26
        versionCode = appVersionName[0].toString().toInt()
        versionName = appVersionName

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

            buildConfigField("String", "VERSION_NAME", "\"$appVersionName\"")
            buildConfigField("String", "VERSION_DATE", "\"$appVersionDate\"")
        }

        debug {
            buildConfigField("String", "VERSION_NAME", "\"$appVersionName-$debugVersionExt\"")
            buildConfigField("String", "VERSION_DATE", "\"$appVersionDate\"")
        }
    }


    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_19
        targetCompatibility = JavaVersion.VERSION_19
    }

    kotlinOptions {
        jvmTarget = "19"
        freeCompilerArgs = freeCompilerArgs + "-Xjvm-default=all"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.10"
    }

    packaging {
        resources {
            excludes.add("/META-INF/{AL2.0,LGPL2.1}")
        }
    }
}

dependencies {
    implementation(project(mapOf("path" to ":domain")))
    implementation(project(mapOf("path" to ":data")))

    implementation("androidx.annotation:annotation:1.7.1")
    implementation("androidx.preference:preference-ktx:1.2.1")
    implementation("androidx.core:core-ktx:1.12.0")


    //Lifecycle
    implementation(Dependencies.Lifecycle.liveData)
    implementation(Dependencies.Lifecycle.viewModel)
    implementation(Dependencies.Lifecycle.runtime)


    // Compose
    implementation(platform(Dependencies.Compose.bom))
    implementation(Dependencies.Compose.ui)
    implementation(Dependencies.Compose.tooling)
    implementation(Dependencies.Compose.toolingPreview)
    implementation(Dependencies.Compose.foundation)
    implementation(Dependencies.Compose.material3)
    implementation(Dependencies.Compose.icons)
    implementation(Dependencies.Compose.iconsExtended)
    implementation(Dependencies.Compose.navigation)
    implementation(Dependencies.Compose.activity)
    implementation(Dependencies.Compose.viewModel)
    implementation(Dependencies.Compose.liveData)
    implementation(Dependencies.Compose.constraintLayout)
    implementation(Dependencies.Compose.coil)

    debugImplementation(Dependencies.Compose.uiTooling)

    //Firebase
    implementation(platform(Dependencies.Firebase.bom))
    implementation(Dependencies.Firebase.database)
    implementation(Dependencies.Firebase.core)
    implementation(Dependencies.Firebase.auth)
    implementation(Dependencies.Firebase.functions)

    //Retrofit
    implementation(Dependencies.Retrofit.retrofit)

    //DI
    implementation(Dependencies.Dagger.dagger)
    ksp(Dependencies.Dagger.compiler)

    implementation("com.google.code.gson:gson:2.10.1")


    // UI Tests
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(Dependencies.Compose.jUnit)
    testImplementation("org.testng:testng:7.8.0")

    Dependencies.Coroutines.values().forEach {
        api(it.path)
    }
}