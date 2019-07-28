import org.gradle.api.Plugin
import org.gradle.api.Project

class ModuleGeneratorPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        val domainVariantComponentContainer = target.container(DomainComponent::class.java) { name ->
            DomainComponent(name, target.objects)
        }

        target.extensions.add("domains", domainVariantComponentContainer)

        target.components.addAll(domainVariantComponentContainer.asSequence())
    }
}