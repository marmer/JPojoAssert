package io.github.marmer.testutils.annotationprocessing.jpojoassert

import org.opentest4j.MultipleFailuresError

private typealias Callback = (() -> Unit)

class PojoAssertionBuilder<out T>(
    private val pojo: T,
    private val assertionCallbacks: List<Callback> = emptyList(),
    private val heading: String = "Unexpected exceptions thrown"
) {

    fun add(assertionCallback: (T) -> Unit) =
        PojoAssertionBuilder(pojo, assertionCallbacks + { assertionCallback(pojo) }, "What a good day to throw")

    fun assertHardly() =
        assertionCallbacks.forEach { callback ->
            callback.toThrownExceptionOrNull()
                .let {
                    if (it != null) throw MultipleFailuresError(heading, listOf(it))
                }
        }

    fun assertSoftly() =
        with(assertionCallbacks.map {
            it.toThrownExceptionOrNull()
        }.filterNotNull()) {
            if (isNotEmpty()) throw MultipleFailuresError(heading, this)
        }

    private fun Callback.toThrownExceptionOrNull() =
        try {
            this()
            null
        } catch (e: Exception) {
            e
        } catch (e: AssertionError) {
            e
        }
}
