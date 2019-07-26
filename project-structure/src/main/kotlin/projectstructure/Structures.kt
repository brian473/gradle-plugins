package projectstructure

import javax.naming.Name

interface NodeType
class Library : NodeType
class Service : NodeType
class Domain : NodeType
class Api : NodeType

interface NodeTemplate<T : NodeType>

@DslMarker
annotation class StructureMarker

@StructureMarker
abstract class Node(val name: String)

abstract class Component<T : NodeType>(name: String, val nodeTemplate: NodeTemplate<T>) : Node(name)

abstract class ComponentGroup<T : NodeType>(name: String) : Node(name) {
    val members = mutableListOf<Component<T>>()

    fun initComponent(component: Component<T>, init: Component<T>.() -> Unit) : ComponentGroup<T> {
        component.init()
        members.add(component)
        return this
    }

    abstract fun moduleNames(): List<String>
}

class NodeDirectory<T : NodeType>(name: String) : ComponentGroup<T>(name) {
    override fun moduleNames() = members.fold(listOf(name)) { acc, p -> acc + "$name:${p.name}" }
}

class Structure : ComponentGroup<Api>("rootProject") {
    private val directories = mutableListOf<NodeDirectory<*>>()

    private fun <T : NodeType> initDirectory(nodeDirectory: NodeDirectory<T>,
                                               init: NodeDirectory<T>.() -> Unit): Structure {
        nodeDirectory.init()
        directories.add(nodeDirectory)
        return this
    }

    override fun moduleNames() = members.map { it.name } + directories.flatMap { it.moduleNames() }

    fun libs(init: NodeDirectory<Library>.() -> Unit) = initDirectory(NodeDirectory("libs"), init)
    fun services(init: NodeDirectory<Service>.() -> Unit) = initDirectory(NodeDirectory("services"), init)
    fun domains(init: NodeDirectory<Domain>.() -> Unit) = initDirectory(NodeDirectory("domains"), init)
    fun api(apiComponent: ApiComponent, init: Component<Api>.() -> Unit) = initComponent(apiComponent, init)
}

class ApiComponent(nodeTemplate: NodeTemplate<Api>) : Component<Api>("api", nodeTemplate)

fun projectStructure(init: Structure.() -> Unit): Structure {
    val projectStructure = Structure()
    projectStructure.init()
    return projectStructure
}