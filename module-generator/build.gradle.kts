plugins {
    `kotlin-dsl`
    `maven-publish`
}

version = "0.0.1"

gradlePlugin {
    plugins {
        create("module-generator") {
            group = "org.brianbrown"
            id = "module-generator"
            version = "0.0.1"
            implementationClass = "ModuleGeneratorPlugin"
        }
    }
}

publishing {
    repositories {
        mavenLocal()
    }
}