object Dependencies {
    object Compose {
        const val bom = "androidx.compose:compose-bom:2024.03.00"
        const val ui = "androidx.compose.ui:ui"
        const val tooling = "androidx.compose.ui:ui-tooling"
        const val toolingPreview = "androidx.compose.ui:ui-tooling-preview"
        const val foundation = "androidx.compose.foundation:foundation"
        const val material3 = "androidx.compose.material3:material3-android"
        const val icons = "androidx.compose.material:material-icons-core"
        const val iconsExtended = "androidx.compose.material:material-icons-extended"
        const val liveData = "androidx.compose.runtime:runtime-livedata"
        const val uiTooling = "androidx.compose.ui:ui-test-manifest"

        const val navigation = "androidx.navigation:navigation-compose:2.7.7"
        const val activity = "androidx.activity:activity-compose:1.8.2"
        const val viewModel = "androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0"
        const val jUnit = "androidx.compose.ui:ui-test-junit4:1.6.4"
        const val constraintLayout = "androidx.constraintlayout:constraintlayout-compose:1.0.1"
        const val coil = "io.coil-kt:coil-compose:2.6.0"
    }


    data object Room {
        private const val version = "2.6.1"
        const val runtime = "androidx.room:room-runtime:$version"
        const val ktx = "androidx.room:room-ktx:$version"
        const val compiler = "androidx.room:room-compiler:$version"
        const val testing = "androidx.room:room-testing:$version"
    }


    data object Firebase {
        const val bom = "com.google.firebase:firebase-bom:32.7.4"
        const val analytics = "com.google.firebase:firebase-analytics"
        const val auth = "com.google.firebase:firebase-auth"
        const val database = "com.google.firebase:firebase-database"
        const val core = "com.google.firebase:firebase-core:21.1.1"
        const val functions = "com.google.firebase:firebase-functions"
    }


    object Retrofit {
        private const val version = "2.10.0"
        const val retrofit = "com.squareup.retrofit2:retrofit:$version"
        const val gsonConverter = "com.squareup.retrofit2:converter-gson:$version"
        const val scalarisConverter = "com.squareup.retrofit2:converter-scalars:$version"
    }


    object Lifecycle {
        private const val version = "2.6.2"

        const val liveData = "androidx.lifecycle:lifecycle-livedata-ktx:$version"
        const val viewModel = "androidx.lifecycle:lifecycle-viewmodel-ktx:$version"
        const val runtime = "androidx.lifecycle:lifecycle-runtime-ktx:$version"
    }

    object Dagger {
        private const val version = "2.50"

        const val dagger = "com.google.dagger:dagger:$version"
        const val compiler = "com.google.dagger:dagger-compiler:$version"
    }
}