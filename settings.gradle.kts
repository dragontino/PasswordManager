@file:Suppress("UnstableApiUsage")

include(":domain")
include(":data")
include(":app")


rootProject.name = "Password Manager"


pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}