tasks {
    register("publish") {
        publishing {
            repositories {
                maven(url = "build/repository")
            }
        }
    }
}