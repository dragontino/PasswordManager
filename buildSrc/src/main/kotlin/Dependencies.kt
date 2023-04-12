object Dependencies {
    object Compose {
        private const val version = "1.4.1"
        private const val materialVersion = "1.4.1"
        private const val accompanistVersion = "0.30.1"

        const val ui = "androidx.compose.ui:ui:$version"
        const val tooling = "androidx.compose.ui:ui-tooling:$version"
        const val toolingPreview = "androidx.compose.ui:ui-tooling-preview:$version"
        const val foundation = "androidx.compose.foundation:foundation:$version"
        const val material3 = "androidx.compose.material3:material3-android:1.1.0-beta02"
        const val material = "androidx.compose.material:material:$materialVersion"
        const val icons = "androidx.compose.material:material-icons-core:$materialVersion"
        const val iconsExtended = "androidx.compose.material:material-icons-extended:$materialVersion"
        const val navigation = "androidx.navigation:navigation-compose:2.5.3"
        const val activity = "androidx.activity:activity-compose:1.7.0"
        const val viewModel = "androidx.lifecycle:lifecycle-viewmodel-compose:2.6.1"
        const val liveData = "androidx.compose.runtime:runtime-livedata:$version"
        const val rxJava = "androidx.compose.runtime:runtime-rxjava2:$version"
        const val systemUiController = "com.google.accompanist:accompanist-systemuicontroller:$accompanistVersion"
        const val animation = "com.google.accompanist:accompanist-navigation-animation:$accompanistVersion"
        const val jUnit = "androidx.compose.ui:ui-test-junit4:$version"
        const val uiTooling = "androidx.compose.ui:ui-test-manifest:$version"
        const val constraintLayout = "androidx.constraintlayout:constraintlayout-compose:1.0.1"
    }


    object Room {
        private const val version = "2.5.0"

        const val runtime = "androidx.room:room-runtime:$version"
        const val ktx = "androidx.room:room-ktx:$version"
        const val compiler = "androidx.room:room-compiler:$version"
        const val testing = "androidx.room:room-testing:$version"
    }


    object Firebase {
        const val bom = "com.google.firebase:firebase-bom:31.2.3"
        const val analytics = "com.google.firebase:firebase-analytics-ktx"
        const val auth = "com.google.firebase:firebase-auth-ktx"
        const val database = "com.google.firebase:firebase-database-ktx"
        const val core = "com.google.firebase:firebase-core:21.1.1"
    }


    object Lifecycle {
        private const val version = "2.6.0"

        const val liveData = "androidx.lifecycle:lifecycle-livedata-ktx:$version"
        const val viewModel = "androidx.lifecycle:lifecycle-viewmodel-ktx:$version"
        const val runtime = "androidx.lifecycle:lifecycle-runtime-ktx:$version"
    }


    enum class Coroutines(val path: String) {
        Android("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4"),
        Core("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    }
}