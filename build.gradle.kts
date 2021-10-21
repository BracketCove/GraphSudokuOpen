plugins {
    id("org.gradle.kotlin.kotlin-dsl") version "2.1.7"
}

allprojects {
    repositories {
        google()
        mavenCentral()
        mavenLocal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}
