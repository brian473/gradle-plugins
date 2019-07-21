@DslMarker
annotation class ProjectNodeMarker

@ProjectNodeMarker
interface ProjectNode {
    val name: String
}

abstract class AbstractBranchNode : ProjectNode {
    val children = mutableListOf<ProjectNode>()

    protected fun <T : ProjectNode> initNode(node: T, init: T.() -> Unit) = node.also {
        it.init()
        children.add(it)
    }

    fun customGroup(name: String, init: BranchNode.() -> Unit) = initNode(BranchNode(name), init)
    fun projects(vararg names: String) = names.forEach { children.add(LeafNode(it)) }

    protected fun modulePaths(parentPath: String): List<String> = children.fold(listOf(name)) { acc, it ->
        when (it) {
            is BranchNode -> acc + it.modulePaths("$parentPath${it.name}:")
            is LeafNode -> acc + "$parentPath${it.name}"
            else -> throw IllegalStateException("child node is of invalid type ${it::class.java.name}")
        }
    }
}

class ProjectRoot : AbstractBranchNode() {
    override val name: String
        get() = "root"

    fun libs(init: LibNode.() -> Unit) = initNode(LibNode(), init)
    fun services(init: ServiceNode.() -> Unit) = initNode(ServiceNode(), init)
    fun domains(init: DomainNode.() -> Unit) = initNode(DomainNode(), init)
    fun api(init: ApiNode.() -> Unit) = initNode(ApiNode(), init)
    fun modules() = modulePaths("").drop(1)
}

open class BranchNode(override val name: String) : AbstractBranchNode()
class LibNode : BranchNode("libs")
class ServiceNode : BranchNode("services")
class DomainNode : BranchNode("domains")

open class LeafNode(override val name: String) : ProjectNode
class ApiNode : LeafNode("api")

fun projectStructure(init: ProjectRoot.() -> Unit): ProjectRoot {
    return ProjectRoot().also(init)
}