plugins { base }

tasks.named("check") {
  dependsOn(":app:check", ":domain:check", ":infrastructure:check")
}
