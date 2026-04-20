pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        exclusiveContent {
            forRepository {
                maven("https://repo.spongepowered.org/repository/maven-public")
            }
            filter {
                includeGroupAndSubgroups("org.spongepowered")
            }
        }
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

rootProject.name = "create-gravitated"

include("all-neoforge")
include("simulated:common")
include("simulated:neoforge")
include("aeronautics:common")
include("aeronautics:neoforge")
include("aeronautics-addon:common")
include("aeronautics-addon:neoforge")
include("offroad:common")
include("offroad:neoforge")

include("aeronautics-bundled")
