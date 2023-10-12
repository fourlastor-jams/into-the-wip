import com.badlogic.gdx.tools.texturepacker.TexturePacker

@Suppress(
    // known false positive: https://youtrack.jetbrains.com/issue/KTIJ-19369
    "DSL_SCOPE_VIOLATION"
)
plugins {
    `java-library`
    idea
    alias(libs.plugins.spotless)
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.kapt)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}

val assetsDir = rootProject.files("assets")

sourceSets.main.configure {
    resources.srcDir(assetsDir)
}

spotless {
    isEnforceCheck = false
    java {
        palantirJavaFormat()
    }
    kotlin {
        ktlint()
    }
}

val packTextures = tasks.register("packTextures") {
    val inputDir = "$rootDir/assets/images/included"
    val outputDir = "$rootDir/assets/images/packed"
    inputs.dir(inputDir)
    outputs.dir(outputDir)
    doLast {
        delete(outputDir)
        TexturePacker.process(TexturePacker.Settings().apply {
            combineSubdirectories = true
        }, inputDir, outputDir, "images.pack")
    }
}

tasks.processResources.configure {
    dependsOn(packTextures)
}
tasks.compileJava.configure {
    options.encoding = "UTF-8"
}

dependencies {
    implementation(libs.harlequin.core)
    implementation(libs.harlequin.ashley)
    implementation(libs.perceptual)
    api(libs.gdx.core)
    implementation(libs.gdx.ai)
    implementation(libs.gdx.controllers.core)
    implementation(libs.ashley)
    implementation(libs.jdkgdxds)
    implementation(libs.simpleGraphs)
    implementation(libs.squidLib.core)
    implementation(libs.textratypist)
    implementation(libs.dagger.core)
    kapt(libs.dagger.compiler)
    implementation(libs.ktx.actors)
    implementation(libs.ktx.ai)
    implementation(libs.ktx.app)
    implementation(libs.ktx.collections)
    implementation(libs.ktx.graphics)
    implementation(libs.ktx.i18n)
    implementation(libs.ktx.json)
    implementation(libs.ktx.log)
    implementation(libs.ktx.math)
    implementation(libs.ktx.preferences)
    implementation(libs.ktx.scene2d)
    implementation(libs.ktx.style)
    implementation(libs.ktx.tiled)
}
