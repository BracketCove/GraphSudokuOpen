// github.com/JetBrains/compose-jb
object Deps {

    object JetBrains {
        object Kotlin {
            // __KOTLIN_COMPOSE_VERSION__
            private const val VERSION = "1.5.21"
            const val gradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$VERSION"
            const val testCommon = "org.jetbrains.kotlin:kotlin-test-common:$VERSION"
            const val testJunit = "org.jetbrains.kotlin:kotlin-test-junit:$VERSION"
            const val testJs = "org.jetbrains.kotlin:kotlin-test-js:$VERSION"
            const val coroutinesJVM = "org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.5.0"
            const val coroutinesCore = "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2"
            const val testAnnotationsCommon = "org.jetbrains.kotlin:kotlin-test-annotations-common:$VERSION"
        }

        object Compose {
            // __LATEST_COMPOSE_RELEASE_VERSION__
            private const val VERSION = "1.0.0-alpha2"
            const val gradlePlugin = "org.jetbrains.compose:compose-gradle-plugin:$VERSION"
            const val constraintLayout = "androidx.constraintlayout:constraintlayout-compose:1.0.0-beta02"

        }
    }

    object ImageLoader {
        const val imageLoader = "com.alialbaali.kamel:kamel-image:0.2.2"
    }

    object Android {
        object Tools {
            object Build {
                const val gradlePlugin = "com.android.tools.build:gradle:4.1.0"
            }
        }
    }

    object AndroidX {
        object AppCompat {
            const val appCompat = "androidx.appcompat:appcompat:1.3.0"
        }

        object Activity {
            const val activityCompose = "androidx.activity:activity-compose:1.3.0"
        }
    }

    object Squareup {
        object SQLDelight {
            private const val VERSION = "1.5.0"

            const val gradlePlugin = "com.squareup.sqldelight:gradle-plugin:$VERSION"
            const val androidDriver = "com.squareup.sqldelight:android-driver:$VERSION"
            const val sqliteDriver = "com.squareup.sqldelight:sqlite-driver:$VERSION"
            const val nativeDriver = "com.squareup.sqldelight:native-driver:$VERSION"
            const val sqljsDriver = "com.squareup.sqldelight:sqljs-driver:$VERSION"
        }
    }
}
