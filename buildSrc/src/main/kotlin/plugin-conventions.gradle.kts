@file:Suppress("UnstableApiUsage")

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("gradle-kotlin-conventions")
    id("com.gradleup.shadow")
    `java-gradle-plugin`
}

dependencies {
}

configure<GradlePluginDevelopmentExtension> {
    plugins {
        register("gradle2nix") {
            id = "org.nixos.gradle2nix"
            displayName = "gradle2nix"
            description = "Expose Gradle tooling model for the gradle2nix tool"
            implementationClass = "org.nixos.gradle2nix.Gradle2NixPlugin"
        }
    }
}

// The shims compile against per-Gradle-version API stubs. java-gradle-plugin
// also puts the running Gradle's own API jar on the classpath, where the same
// internal types have a different shape, so drop it and let the stubs win.
sourceSets.configureEach {
    val apiJarName = "gradle-api-${project.gradle.gradleVersion}.jar"
    compileClasspath = compileClasspath.filter { it.name != apiJarName }
}

tasks {
    "jar" {
        enabled = false
    }

    named("shadowJar", ShadowJar::class) {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        isPreserveFileTimestamps = false
        isReproducibleFileOrder = true
        filePermissions { unix("644") }
        dirPermissions { unix("755") }
        filesMatching("**/bin/*") {
            filePermissions { unix("755") }
        }
        filesMatching("**/bin/*.bat") {
            filePermissions { unix("644") }
        }

        relocate("kotlinx", "${project.group}.shadow.kotlinx")
        relocate("org.intellij", "${project.group}.shadow.intellij")
        relocate("org.jetbrains", "${project.group}.shadow.jetbrains")

        dependencies {
            exclude { it.moduleGroup == "org.jetbrains.kotlin" && it.moduleName == "kotlin-stdlib" }
            exclude { it.moduleGroup == "org.jetbrains.kotlin" && it.moduleName == "kotlin-stdlib-common" }
            exclude { it.moduleGroup == "org.jetbrains.kotlin" && it.moduleName == "kotlin-stdlib-jdk7" }
            exclude { it.moduleGroup == "org.jetbrains.kotlin" && it.moduleName == "kotlin-stdlib-jdk8" }
            exclude { it.moduleGroup == "org.jetbrains.kotlin" && it.moduleName == "kotlin-reflect" }
            exclude { it.moduleGroup == "org.jetbrains.kotlin" && it.moduleName == "kotlin-script-runtime" }
        }
    }
}
