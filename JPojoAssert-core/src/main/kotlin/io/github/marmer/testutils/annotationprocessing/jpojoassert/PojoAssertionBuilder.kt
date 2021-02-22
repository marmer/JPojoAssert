package io.github.marmer.testutils.annotationprocessing.jpojoassert

import org.opentest4j.MultipleFailuresError
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

private typealias LocalAssertionCallback = (() -> Unit)

private class AssertionCallbackAsserter(private val assertionCallback: LocalAssertionCallback) : Asserter {
    override fun assertToFirstFail() {
        assertionCallback()
    }

    override fun assertAll() {
        assertionCallback()
    }
}

data class AssertionConfiguration(val asserter: Asserter, val additionalHeading: String)

class PojoAssertionBuilder<T>(
    private val pojo: T,
    private val assertionConfigurations: List<AssertionConfiguration> = emptyList(),
    private val heading: String = "Unexpected exceptions thrown"
) : PojoAsserter<T> {

    @JvmOverloads
    fun add(additionalHeading: String = "", assertionCallback: AssertionCallback<T>) =
        PojoAssertionBuilder(
            pojo,
            assertionConfigurations +
                    AssertionConfiguration(AssertionCallbackAsserter {
                        assertionCallback.assertFor(
                            pojo
                        )
                    }, additionalHeading),
            heading
        )

    // TODO: marmer 01.02.2021 Add for "hasPropertyName" methods as well
    fun <P> addForProperty(propertyName: String, assertionCallback: AssertionCallback<P>) =
        add(propertyName) { assertionCallback.assertFor(getPropertyValue(pojo as Any, propertyName)) }

    fun addAsserter(
        additionalHeading: String,
        pojoAssertionCallback: (T) -> PojoAsserter<*>
    ): PojoAssertionBuilder<T> =
        PojoAssertionBuilder(
            pojo,
            assertionConfigurations + AssertionConfiguration(pojoAssertionCallback(pojo), additionalHeading),
            heading
        )


    override fun assertToFirstFail() =
        assertionConfigurations.forEach { assertionConfiguration ->
            assertionConfiguration.assertToFirstFail()
                .let {
                    if (it != null) throw MultipleFailuresError(
                        heading,
                        listOf(it)
                    )
                }
        }

    private fun AssertionConfiguration.assertToFirstFail() =
        try {
            asserter.assertToFirstFail()
            null
        } catch (e: Exception) {
            java.lang.AssertionError(additionalHeading + ": " + e.message, e)
        } catch (e: AssertionError) {
            java.lang.AssertionError(additionalHeading + ": " + e.message, e)
        }

    override fun assertAll() {
        with(assertionConfigurations.map {
            it.assertAll()
        }.filterNotNull()) {
            if (isNotEmpty()) throw MultipleFailuresError(heading, this)
        }
    }

    private fun AssertionConfiguration.assertAll() = try {
        asserter.assertAll()
        null
    } catch (e: Exception) {
        java.lang.AssertionError(additionalHeading + ": " + e.message, e)
    } catch (e: AssertionError) {
        java.lang.AssertionError(additionalHeading + ": " + e.message, e)
    }

}

// TODO: marmer 01.02.2021 care about transitive inheritance
// TODO: marmer 01.02.2021 property like methods
// TODO: marmer 01.02.2021 not existing property
// TODO: marmer 22.02.2021 care about extension functions
// TODO: marmer 22.02.2021 care about extension properties
// TODO: marmer 22.02.2021 care about java property like methods in kotlin
private fun <P> getPropertyValue(pojo: Any, propertyName: String): P? {
    val methodProp = pojo::class.memberFunctions.firstOrNull { it.name == "get${propertyName.capitalize()}" }
    if (methodProp != null) {
        synchronized(pojo) {
            val accessable = methodProp.isAccessible
            methodProp.isAccessible = true
            val propValue = methodProp.call(pojo) as P?
            methodProp.isAccessible = accessable

            return propValue
        }
    }

    val fieldProp = pojo::class.memberProperties.first {
        it.name == propertyName
    } as KProperty1<Any, P>
    synchronized(pojo) {
        val accessable = fieldProp.isAccessible
        fieldProp.isAccessible = true
        val propValue = fieldProp.get(pojo)
        fieldProp.isAccessible = accessable
        return propValue
    }
}

