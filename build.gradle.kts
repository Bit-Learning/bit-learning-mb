// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
  alias(libs.plugins.android.application) apply false
  alias(libs.plugins.jetbrains.kotlin.android) apply false
  alias(libs.plugins.compose.compiler) apply false
  id("com.google.dagger.hilt.android") version "2.48" apply false
  id("com.google.devtools.ksp") version "2.0.21-1.0.27" apply false
  id("com.diffplug.spotless") version "8.1.0"
}

subprojects {
  apply(plugin = "com.diffplug.spotless")
  configure<com.diffplug.gradle.spotless.SpotlessExtension> {
    kotlin {
      target("**/*.kt")
      targetExclude("${rootProject.layout.buildDirectory}/**/*.kt")

      ktlint()
        .editorConfigOverride(
          mapOf(
            "ktlint_standard_filename" to "disabled", // cho phép tên file tự do hơn
            "ij_kotlin_imports_layout" to "*", // optimize imports
            "ij_kotlin_allow_trailing_comma" to "true",
            "ktlint_standard_no-wildcard-imports" to "disabled", // cho phép wildcard imports
            "ktlint_function_naming_ignore_when_annotated_with" to "Composable",
            "ktlint_standard_property-naming" to "disable",
            "ktlint_standard_no-empty-file" to
              "disabled", // cho phép file rỗng (ví dụ: để chứa các extension functions mà chưa có
            // nội dung nào
          )
        )
      licenseHeaderFile(rootProject.file("spotless/license-header.kt"))
    }

    kotlinGradle {
      target("*.gradle.kts")
      ktlint()
    }
  }

  // Tự động chạy spotlessApply trước khi build
  //  afterEvaluate { tasks.named("preBuild") { dependsOn("spotlessApply") } }
}

spotless {
  kotlinGradle {
    target("*.gradle.kts") // chỉ root gradle scripts
    ktfmt().googleStyle()
  }
  yaml {
    target("*.yml", ".github/**/*.yml")
    jackson()
  }
}
