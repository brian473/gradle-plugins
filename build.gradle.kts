tasks {
    val moduleGeneratorPlugin by registering(GradleBuild::class) {
        dir = file("module-generator")
    }
}