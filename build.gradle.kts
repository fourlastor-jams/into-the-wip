buildscript {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
    dependencies {
        classpath (libs.gdx.tools)
    }
}

allprojects {
    buildscript {
        repositories {
            mavenCentral()
            gradlePluginPortal()
        }
    }
    repositories {
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
