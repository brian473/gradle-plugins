import io.kotlintest.matchers.collections.shouldContain
import io.kotlintest.matchers.collections.shouldContainAll
import io.kotlintest.matchers.collections.shouldContainInOrder
import io.kotlintest.specs.StringSpec
import org.gradle.internal.impldep.org.testng.AssertJUnit.assertEquals
import org.gradle.internal.impldep.org.testng.annotations.Test

class StructuresTests : StringSpec({
    "when building deeply nested structure, modules should return the full paths" {
        projectStructure {
            libs {
                projects("testing!", "123")
                customGroup("huh") {
                    customGroup("huh2") {
                        projects("a", "b", "c")
                    }
                }
            }
        }.modules() shouldContainAll listOf(
                "libs",
                "libs:testing!",
                "libs:123",
                "huh",
                "huh2",
                "libs:huh:huh2:a",
                "libs:huh:huh2:b",
                "libs:huh:huh2:c"
        )
    }

    "project structure should contain all predefined groups when used" {
        projectStructure {
            libs        { }
            services    { }
            domains     { }
            api         { }
        }.modules() shouldContainAll listOf("libs", "services", "domains", "api")
    }
})