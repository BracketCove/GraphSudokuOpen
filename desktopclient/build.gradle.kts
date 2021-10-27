import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("multiplatform") // kotlin("jvm") doesn't work well in IDEA/AndroidStudio (https://github.com/JetBrains/compose-jb/issues/22)
    id("org.jetbrains.compose")
}

kotlin {
    jvm {
        withJava()
    }
    sourceSets {
        named("jvmMain") {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation(compose.ui)
                implementation(compose.material)
                implementation(project(":common"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-debug:1.5.2")
            }
        }
    }
}

compose.desktop {
    application {
        mainClass = "com.bracketcove.graphsudoku.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "GraphSudoku"
            packageVersion = "1.0.0"

            modules("java.sql")

            windows {
                menuGroup = "Graph Sudoku Multiplatform"
                upgradeUuid = "7f73d4cd-91bb-4ca1-9982-2ee6633ec43e"
            }
        }
    }
}
