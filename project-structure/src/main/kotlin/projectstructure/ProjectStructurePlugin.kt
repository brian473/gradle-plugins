package projectstructure

import org.gradle.api.Plugin
import org.gradle.kotlin.dsl.SettingsScriptApi

class ProjectStructurePlugin : Plugin<SettingsScriptApi> {
    override fun apply(target: SettingsScriptApi) {
        target.extensions.add("projectstructure.projectStructure", ProjectRoot())
    }
}