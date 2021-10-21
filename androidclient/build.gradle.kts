import org.jetbrains.compose.compose

plugins {
    id("com.android.application")
    kotlin("android")
    id("org.jetbrains.compose")
    id("com.google.protobuf") version "0.8.12"
}

android {
    compileSdk = 30

    defaultConfig {
        applicationId = "com.example.samsarakmm.android"
        minSdk = 21
        targetSdk = 30
        versionCode = 1
        versionName = "1.0"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation(project(":common:compose-ui"))
    implementation(project(":common:jvm"))

    implementation(compose.material)
    implementation(Deps.AndroidX.AppCompat.appCompat)
    implementation(Deps.AndroidX.Activity.activityCompose)
    implementation(Deps.JetBrains.Kotlin.coroutinesJVM)
    implementation(Deps.JetBrains.Kotlin.coroutinesCore)

}