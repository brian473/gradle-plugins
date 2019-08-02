plugins {
    `kotlin-dsl`
}

gradlePlugin {
    plugins {
        register("brian-platform-plugin") {
            id = "brian-platform"
            implementationClass = "BrianPlatformPlugin"
        }
    }
}

repositories {
    jcenter()
}