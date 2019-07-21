plugins {
    base
    kotlin("jvm") version "1.3.41" apply false
}

allprojects {
    group = "io.smart-life"
    version = "1.0"

    repositories {
        jcenter()
        mavenCentral()
    }
}