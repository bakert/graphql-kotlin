package com.expedia.graphql.schema.extensions

import com.expedia.graphql.schema.hooks.NoopSchemaGeneratorHooks
import com.expedia.graphql.schema.hooks.SchemaGeneratorHooks
import org.junit.jupiter.api.Test
import kotlin.reflect.KFunction
import kotlin.reflect.KProperty
import kotlin.test.assertEquals

class KClassExtensionsTest {

    @Suppress("Detekt.FunctionOnlyReturningConstant", "Detekt.UnusedPrivateMember")
    class MyTestClass(
        val publicProperty: String = "public",
        val filteredProperty: String = "filtered",
        private val privateVal: String = "hidden"
    ) {
        fun publicFunction() = "public function"

        fun filteredFunction() = "filtered function"

        private fun privateTestFunction() = "private function"
    }

    class FilterHooks : SchemaGeneratorHooks {
        override fun isValidProperty(property: KProperty<*>) =
            property.name.contains("filteredProperty").not()

        override fun isValidFunction(function: KFunction<*>) =
            function.name.contains("filteredFunction").not()
    }

    private val noopHooks = NoopSchemaGeneratorHooks()

    @Test
    fun `test getting valid properties with no hooks`() {
        val properties = MyTestClass::class.getValidProperties(noopHooks)
        assertEquals(listOf("filteredProperty", "publicProperty"), properties.map { it.name })
    }

    @Test
    fun `test getting valid properties with filter hooks`() {
        val properties = MyTestClass::class.getValidProperties(FilterHooks())
        assertEquals(listOf("publicProperty"), properties.map { it.name })
    }

    @Test
    fun `test getting valid functions with no hooks`() {
        val properties = MyTestClass::class.getValidFunctions(noopHooks)
        assertEquals(listOf("filteredFunction", "publicFunction"), properties.map { it.name })
    }

    @Test
    fun `test getting valid functions with filter hooks`() {
        val properties = MyTestClass::class.getValidFunctions(FilterHooks())
        assertEquals(listOf("publicFunction"), properties.map { it.name })
    }
}
