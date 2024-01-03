plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}
rootProject.name = "Java2TS"
val jvmToolchainVersion = 11
include("Core")
include("CLI")
