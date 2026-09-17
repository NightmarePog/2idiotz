plugins { id("java-conventions") }

dependencies {
  testImplementation(platform(libs.spring.boot.bom))
  testImplementation(libs.junit.jupiter)
  testRuntimeOnly(libs.junit.launcher)
}
