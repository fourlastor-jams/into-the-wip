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
        maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots/") }
        maven { url = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/") }
        maven { url = uri("https://jitpack.io") }
        maven { url = uri("https://teavm.org/maven/repository/") }
    }
}
