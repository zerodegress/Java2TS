plugins {
    kotlin("jvm")
}

group = "ink.zerodegress.java2ts"
version = "0.1.0"

dependencies {
    testImplementation("org.jetbrains.kotlin:kotlin-test")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(11)
}