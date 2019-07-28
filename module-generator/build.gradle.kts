plugins {
    `kotlin-dsl`
    `maven-publish`
}

group = "org.brianbrown"
version = "0.0.1"

project.projectDir
        .listFiles { _, name -> name.matches(Regex("(?!settings)^([a-z\\.]+)(\\.gradle{1})(\\.kts{0,1})?")) }
        .forEach { apply(from = it.name) }