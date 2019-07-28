tasks {
    register("declarePlugin") {
        gradlePlugin {
            plugins {
                register("module-dsl") {
                    id = "my-plugin"
                    implementationClass = "ModuleDslPlugin"
                }
            }
        }
    }
}