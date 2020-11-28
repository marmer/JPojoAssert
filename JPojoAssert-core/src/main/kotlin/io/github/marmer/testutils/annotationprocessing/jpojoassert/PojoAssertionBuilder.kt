package io.github.marmer.testutils.annotationprocessing.jpojoassert

import org.opentest4j.MultipleFailuresError

private typealias LocalAssertionCallback = (() -> Unit)

data class AssertionConfiguration(val assertionCallback: LocalAssertionCallback, val additionalHeading: String)

class PojoAssertionBuilder<T>(
    private val pojo: T,
    private val assertionConfigurations: List<AssertionConfiguration> = emptyList(),
    private val heading: String = "Unexpected exceptions thrown"
) : PojoAsserter<T> {

    @JvmOverloads
    fun add(additionalHeading: String = "", assertionCallback: AssertionCallback<T>) =
        PojoAssertionBuilder(
            pojo,
            assertionConfigurations + AssertionConfiguration({ assertionCallback.assertFor(pojo) }, additionalHeading),
            heading
        )

    override fun assertToFirstFail() =
        assertionConfigurations.forEach { assertionConfiguration ->
            assertionConfiguration.toThrownExceptionOrNull()
                .let {
                    if (it != null) throw MultipleFailuresError(
                        heading,
                        listOf(it)
                    )
                }
        }

    override fun assertAll() {
        with(assertionConfigurations.map {
            it.toThrownExceptionOrNull()
        }.filterNotNull()) {
            if (isNotEmpty()) throw MultipleFailuresError(heading, this)
        }
    }

    private fun AssertionConfiguration.toThrownExceptionOrNull() =
        try {
            assertionCallback()
            null
        } catch (e: Exception) {
            java.lang.AssertionError(additionalHeading + ": " + e.message, e)
        } catch (e: AssertionError) {
            java.lang.AssertionError(additionalHeading + ": " + e.message, e)
        }
}
