@Suppress(
    // known false positive: https://youtrack.jetbrains.com/issue/KTIJ-19369
    "DSL_SCOPE_VIOLATION"
)
plugins {
    java
    application
    alias(libs.plugins.spotless)
    alias(libs.plugins.shadow)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}

spotless {
    isEnforceCheck = false
    java {
        palantirJavaFormat()
    }
}


application {
    val className = "io.github.fourlastor.game.DesktopLauncher"
    mainClass.set(className)
}

dependencies {
    implementation(project(":core"))
    nativesDesktop(libs.gdx.platform)
    implementation(libs.gdx.backend.lwjgl3)
    implementation(libs.gdx.controllers.desktop)
}

fun DependencyHandlerScope.nativesDesktop(
    provider: Provider<MinimalExternalModuleDependency>,
) = runtimeOnly(variantOf(provider) { classifier("natives-desktop") })
