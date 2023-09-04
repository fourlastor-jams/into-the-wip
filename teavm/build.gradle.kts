import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

@Suppress(
  // known false positive: https://youtrack.jetbrains.com/issue/KTIJ-19369
  "DSL_SCOPE_VIOLATION"
)
plugins {
  java
  alias(libs.plugins.kotlin.jvm)
  alias(libs.plugins.gretty)
}

gretty {
  contextPath = "/"
  extraResourceBase("build/dist/webapp")
}

val assetsDir = rootProject.files("assets")
sourceSets.main.configure {
  resources.srcDir(assetsDir)
}
val mainClassName = "io.github.fourlastor.game.teavm.TeaVMBuilder"
java {
  targetCompatibility = JavaVersion.VERSION_11
  sourceCompatibility = JavaVersion.VERSION_11
}

tasks.withType<KotlinCompile>().configureEach {
  compilerOptions { jvmTarget.set(JvmTarget.JVM_11) }
}
dependencies {
  implementation (libs.gdx.backend.teavm)
  implementation(libs.google.gson)
  implementation(project(":core"))
}

val buildJavaScript = tasks.register<JavaExec>("buildJavaScript") {
  dependsOn(tasks.classes)
  description = "Transpile bytecode to JavaScript via TeaVM"
  mainClass.set(mainClassName)
  setClasspath(sourceSets.main.get().runtimeClasspath)
}

tasks.build.configure { dependsOn(buildJavaScript) }
val run = tasks.register("run") {
  description = "Run the JavaScript application hosted via a local Jetty server at http://localhost:8080/"
  dependsOn(buildJavaScript, ":teavm:jettyRun")
}