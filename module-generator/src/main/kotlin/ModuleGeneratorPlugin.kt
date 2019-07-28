import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.component.PublishableComponent
import org.gradle.api.internal.artifacts.Module
import org.gradle.api.internal.artifacts.ivyservice.projectmodule.ProjectComponentPublication
import org.gradle.api.publish.Publication
import org.gradle.api.publish.internal.GradleModuleMetadataWriter
import org.gradle.api.publish.tasks.GenerateModuleMetadata
import org.gradle.internal.id.UniqueId
import org.gradle.internal.impldep.com.google.gson.stream.JsonWriter
import org.gradle.internal.scopeids.id.BuildInvocationScopeId
import org.gradle.kotlin.dsl.accessors.projectSchemaResourcePath
import org.gradle.kotlin.dsl.support.GradleApiMetadata
import java.io.FileWriter
import java.util.*

class ModuleGeneratorPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        val domainVariantComponentContainer = target.container(DomainComponent::class.java) { name ->
            DomainComponent(name, target.objects)
        }


        target.extensions.add("domains", domainVariantComponentContainer)

        target.components.addAll(domainVariantComponentContainer.asSequence())
    }
}