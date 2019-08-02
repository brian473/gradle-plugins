import org.gradle.api.InvalidUserCodeException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ConfigurationContainer
import org.gradle.api.attributes.Category
import org.gradle.api.attributes.Usage
import org.gradle.api.component.SoftwareComponentFactory
import org.gradle.api.internal.artifacts.JavaEcosystemSupport
import org.gradle.api.internal.artifacts.dsl.dependencies.PlatformSupport
import org.gradle.api.internal.java.DefaultJavaPlatformExtension
import org.gradle.api.model.ObjectFactory
import org.gradle.api.plugins.BasePlugin
import org.gradle.api.plugins.JavaPlatformExtension
import org.gradle.api.plugins.internal.JavaConfigurationVariantMapping
import javax.inject.Inject

const val API_CONFIGURATION_NAME = "api"
const val RUNTIME_CONFIGURATION_NAME = "runtime"

// Consumable configurations
const val API_ELEMENTS_CONFIGURATION_NAME = "apiElements"
const val RUNTIME_ELEMENTS_CONFIGURATION_NAME = "runtimeElements"
//val ENFORCED_API_ELEMENTS_CONFIGURATION_NAME = "enforcedApiElements"
//val ENFORCED_RUNTIME_ELEMENTS_CONFIGURATION_NAME = "enforcedRuntimeElements"

// Resolvable configurations
const val CLASSPATH_CONFIGURATION_NAME = "classpath"

private fun Configuration.asConsumableConfiguration() = apply {
    isCanBeResolved = false
    isCanBeConsumed = true
}

private fun Configuration.asBucket() = apply {
    isCanBeResolved = false
    isCanBeConsumed = false
}

private fun Configuration.asResolveableConfiguration() = apply {
    isCanBeResolved = true
    isCanBeConsumed = false
}

private const val DISALLOW_DEPENDENCIES = "Adding dependencies to platforms is not allowed by default.\n" +
        "Most likely you want to add constraints instead.\n" +
        "If you did this intentionally, you need to configure the platform extension to allow dependencies:\n" +
        "    javaPlatform.allowDependencies()\n" +
        "Found dependencies in the '%s' configuration."

@Suppress("UnstableApiUsage")
abstract class BrianPlatformPlugin : Plugin<Project> { // <1>

    @get:Inject
    abstract val softwareComponentFactory: SoftwareComponentFactory

    private fun createSoftwareComponent(project: Project, apiElements: Configuration, runtimeElements: Configuration) {
        val component = softwareComponentFactory.adhoc("brianPlatform")
        project.components.add(component)
        component.addVariantsFromConfiguration(
                apiElements,
                JavaConfigurationVariantMapping("compile", false)
        )
        component.addVariantsFromConfiguration(
                runtimeElements,
                JavaConfigurationVariantMapping("runtime", false)
        )
    }

    override fun apply(project: Project) {
        project.pluginManager.apply(BasePlugin::class.java)
        createConfigurations(project)
        configureExtension(project)
        addPlatformDisambiguationRule(project)
        JavaEcosystemSupport.configureSchema(project.dependencies.attributesSchema, project.objects)
    }

    private fun addPlatformDisambiguationRule(project: Project) {
        project.dependencies
                .attributesSchema
                .getMatchingStrategy(Category.CATEGORY_ATTRIBUTE)
                .disambiguationRules
                .add(PlatformSupport.PreferRegularPlatform::class.java)
    }

    private fun createConfigurations(project: Project) {
        val configurations = project.configurations
        val api = configurations
                .maybeCreate(API_CONFIGURATION_NAME)
                .asConsumableConfiguration()

        val apiElements = createConsumableApi(project, configurations, api)
//        val enforcedApiElements = createConsumableApi(project, configurations, api, ENFORCED_API_ELEMENTS_CONFIGURATION_NAME, Category.ENFORCED_PLATFORM)

        val runtime = configurations
                .maybeCreate(RUNTIME_CONFIGURATION_NAME)
                .asBucket()
        runtime.extendsFrom(api)

        val runtimeElements = createConsumableRuntime(project, runtime)
//        val enforcedRuntimeElements = createConsumableRuntime(project, runtime, ENFORCED_RUNTIME_ELEMENTS_CONFIGURATION_NAME, Category.ENFORCED_PLATFORM)

        val classpath = configurations
                .maybeCreate(CLASSPATH_CONFIGURATION_NAME)
                .asResolveableConfiguration()
        classpath.extendsFrom(runtimeElements)
        declareConfigurationUsage(project.objects, classpath, Usage.JAVA_RUNTIME)

        createSoftwareComponent(project, apiElements, runtimeElements)
    }

    private fun createConsumableRuntime(project: Project, apiElements: Configuration): Configuration {
        val runtimeElements = project.configurations
                .maybeCreate(RUNTIME_ELEMENTS_CONFIGURATION_NAME)
                .asConsumableConfiguration()

        runtimeElements.extendsFrom(apiElements)
        declareConfigurationUsage(project.objects, runtimeElements, Usage.JAVA_RUNTIME)
        declareConfigurationCategory(project.objects, runtimeElements)
        return runtimeElements
    }

    private fun createConsumableApi(project: Project,
                                    configurations: ConfigurationContainer,
                                    api: Configuration): Configuration {
        val apiElements = configurations
                .maybeCreate(API_ELEMENTS_CONFIGURATION_NAME)
                .asConsumableConfiguration()

        apiElements.extendsFrom(api)
        declareConfigurationUsage(project.objects, apiElements, Usage.JAVA_API)
        declareConfigurationCategory(project.objects, apiElements)
        return apiElements
    }

    private fun declareConfigurationCategory(objectFactory: ObjectFactory, configuration: Configuration) {
        configuration.attributes.attribute(
                Category.CATEGORY_ATTRIBUTE,
                objectFactory.named(Category::class.java, Category.REGULAR_PLATFORM))
    }

    private fun declareConfigurationUsage(objectFactory: ObjectFactory, configuration: Configuration, usage: String) {
        configuration.attributes.attribute(Usage.USAGE_ATTRIBUTE, objectFactory.named(Usage::class.java, usage))
    }

    private fun configureExtension(project: Project) {
        project.extensions.create(
                JavaPlatformExtension::class.java,
                "brianPlatform",
                DefaultJavaPlatformExtension::class.java
        ) as DefaultJavaPlatformExtension

//        project.afterEvaluate {
//            if (!platformExtension.isAllowDependencies) {
//                checkNoDependencies(it)
//            }
//        }
    }

    private fun checkNoDependencies(project: Project) {
        checkNoDependencies(project.configurations.getByName(RUNTIME_CONFIGURATION_NAME), mutableSetOf())
    }

    private fun checkNoDependencies(configuration: Configuration, visited: MutableSet<Configuration>) {
        if (visited.add(configuration)) {
            if (!configuration.dependencies.isEmpty()) {
                throw InvalidUserCodeException(String.format(DISALLOW_DEPENDENCIES, configuration.name))
            }
            val extendsFrom = configuration.extendsFrom
            for (parent in extendsFrom) {
                checkNoDependencies(parent, visited)
            }
        }
    }

}