package io.github.marmer.testutils.annotationprocessing.jpojoassert

import org.opentest4j.MultipleFailuresError

private typealias LocalAssertionCallback = (() -> Unit)

class PojoAssertionBuilder<T>(
    private val pojo: T,
    private val assertionCallbacks: List<LocalAssertionCallback> = emptyList(),
    private val heading: String = "Unexpected exceptions thrown"
) {

    fun add(assertionCallback: AssertionCallback<T>) =
        PojoAssertionBuilder(pojo, assertionCallbacks + { assertionCallback.accept(pojo) }, "What a good day to throw")

    fun add(assertionCallback: (T) -> Unit) = add(AssertionCallback { assertionCallback(it) })

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

    private fun LocalAssertionCallback.toThrownExceptionOrNull() =
        try {
            this()
            null
        } catch (e: Exception) {
            e
        } catch (e: AssertionError) {
            e
        }
}
