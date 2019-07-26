plugins {
    base
    kotlin("jvm") version "1.3.41" apply false
}

allprojects {
    group = "org.brianbrown"

    repositories {
        jcenter()
        mavenCentral()
    }
}