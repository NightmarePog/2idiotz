plugins {
  `java-library`
  id("com.diffplug.spotless")
}

group = "cloud.twoidiotz"
version = "0.0.1-SNAPSHOT"

java {
  toolchain { languageVersion.set(JavaLanguageVersion.of(21)) }
}

tasks.withType<Test>().configureEach {
  useJUnitPlatform { excludeTags("integration") }
}

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
