plugins {
  java
  alias(libs.plugins.spotless)
  alias(libs.plugins.spring.boot)
}

group = "cloud.twoidiotz"

version = "0.0.1-SNAPSHOT"

java { toolchain { languageVersion.set(JavaLanguageVersion.of(21)) } }

tasks.test { useJUnitPlatform { excludeTags("openapi") } }

spotless {
  java {
    googleJavaFormat("1.25.2")
    removeUnusedImports()
    trimTrailingWhitespace()
    endWithNewline()
  }
  kotlinGradle {
    ktfmt("0.54")
    trimTrailingWhitespace()
    endWithNewline()
  }
}

dependencies {
  testImplementation(libs.commons.csv)
  implementation(platform(libs.spring.boot.bom))
  compileOnly(libs.lombok)
  annotationProcessor(platform(libs.spring.boot.bom))
  annotationProcessor(libs.lombok)
  implementation(libs.spring.jpa)
  implementation(libs.spring.flyway)
  runtimeOnly(libs.flyway.postgresql)
  implementation(libs.postgresql)
  implementation(libs.spring.web)
  implementation(libs.spring.validation)
  implementation(libs.springdoc)
  testImplementation(libs.spring.test)
  testRuntimeOnly(libs.junit.launcher)
}

tasks.bootJar { archiveFileName.set("app.jar") }

tasks.register<Test>("exportOpenApi") {
  description = "Exports the OpenAPI contract and a live health response from this checkout."
  group = "verification"
  testClassesDirs = sourceSets.test.get().output.classesDirs
  classpath = sourceSets.test.get().runtimeClasspath
  useJUnitPlatform { includeTags("openapi") }
  val output = layout.buildDirectory.dir("openapi")
  systemProperty("openapi.output", output.get().asFile.absolutePath)
  outputs.dir(output)
}
