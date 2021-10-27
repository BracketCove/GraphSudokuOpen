import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.android.library")
    kotlin("multiplatform")
    id("org.jetbrains.compose")
    id("com.squareup.sqldelight")
}

sqldelight {
    database("AppDatabase") {
        packageName = "com.bracketcove.graphsuduoku.persistence"
    }
}

kotlin {
    android()
    jvm("desktop")

    sourceSets {
        named("commonMain") {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material)
                implementation(compose.ui)
                implementation(compose.materialIconsExtended)
            }
        }

        named("androidMain") {
            dependencies {
                implementation(Deps.Squareup.SQLDelight.androidDriver)
                implementation(Deps.JetBrains.Kotlin.coroutinesCore)
            }
        }

        named("desktopMain") {
            dependencies {
                implementation(Deps.Squareup.SQLDelight.sqliteDriver)
                implementation(Deps.JetBrains.Kotlin.coroutinesJVM)
            }
        }
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
    }
}

android {
    compileSdkVersion(30)

    defaultConfig {
        minSdkVersion(21)
        targetSdkVersion(30)
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    sourceSets {
        named("main") {
            manifest.srcFile("src/androidMain/AndroidManifest.xml")
        }
    }
}
