package io.github.marmer

import org.opentest4j.AssertionFailedError
import org.opentest4j.MultipleFailuresError

class PojoAssertionBuilder<out T>(private val pojo: T, private val assertionCallbacks: List<() -> Unit> = emptyList()) {

    fun add(assertionCallback: (T) -> Unit) =
        PojoAssertionBuilder(pojo, assertionCallbacks + { assertionCallback(pojo) })

    fun assertHardly() =
        assertionCallbacks.forEach {
            try {
                it()
            } catch (e: Exception) {
                throw AssertionFailedError("Something bad happend", e)
            }
        }

    fun assertSoftly() =
        with(assertionCallbacks.map {
            it.toThrownExceptionOrNull()
        }.filterNotNull()) {
            if (isNotEmpty()) throw MultipleFailuresError("Something bad happend", this)
        }

    fun (() -> Unit).toThrownExceptionOrNull() =
        try {
            this()
            null
        } catch (e: Exception) {
            e
        } catch (e: AssertionError) {
            e
        }
}
