plugins { id("java-conventions") }

dependencies {
  implementation(project(":domain"))
  implementation(platform(libs.spring.boot.bom))
  implementation(libs.spring.jdbc)
  runtimeOnly(libs.postgresql)
  testImplementation(libs.spring.test)
  testRuntimeOnly(libs.junit.launcher)
}
