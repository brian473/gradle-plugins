import javax.inject.Inject

@CacheableTask
abstract class NestedModuleTask : DefaultTask() {

    @get:Inject
    abstract val softwareComponentFactory: SoftwareComponentFactory

    @get:InputDirectory
    @get:SkipWhenEmpty
    @get:PathSensitive(PathSensitivity.RELATIVE)
    val directory: DirectoryProperty = project.objects.directoryProperty()

    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    val configFiles: ConfigurableFileCollection = project.files()

    @get:OutputFile
    val module: RegularFileProperty = project.objects.fileProperty()

    @TaskAction
    fun generateModule() {

    }
}

tasks.register("nestedModule", NestedModuleTask::class)