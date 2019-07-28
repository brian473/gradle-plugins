plugins {
    base
    kotlin("jvm") version "1.3.41" apply false
    id("module-generator") version "0.0.1"
}

allprojects {
    group = "org.brianbrown"

    repositories {
        jcenter()
        mavenCentral()
    }
}

domains.create("books") {
    this.setArtifacts(listOf(DomainArtifact("harry potter", project.objects)))
}