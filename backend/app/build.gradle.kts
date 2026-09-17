plugins {
  id("java-conventions")
  alias(libs.plugins.spring.boot)
}

dependencies {
  implementation(project(":domain"))
  implementation(project(":infrastructure"))
  implementation(platform(libs.spring.boot.bom))
  implementation(libs.spring.web)
  testImplementation(libs.spring.test)
  testImplementation(libs.archunit)
  testImplementation(libs.testcontainers.postgresql)
  testRuntimeOnly(libs.junit.launcher)
}

tasks.bootJar { archiveFileName.set("app.jar") }

val integrationTest by
    tasks.registering(Test::class) {
      description = "Runs HTTP contract tests against real PostgreSQL (requires Docker)."
      group = "verification"
      testClassesDirs = sourceSets.test.get().output.classesDirs
      classpath = sourceSets.test.get().runtimeClasspath
      useJUnitPlatform {
        excludeTags.clear()
        includeTags("integration")
      }
      shouldRunAfter(tasks.test)
    }

tasks.check { dependsOn(integrationTest) }
