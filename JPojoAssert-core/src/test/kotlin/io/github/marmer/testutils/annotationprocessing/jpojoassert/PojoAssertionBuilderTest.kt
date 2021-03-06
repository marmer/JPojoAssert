package io.github.marmer.testutils.annotationprocessing.jpojoassert

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsString
import org.hamcrest.Matchers.not
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.fail

internal class PojoAssertionBuilderTest {

    private data class Type1(val value: Int)


    @Test
    fun `exceptions within an assertion should throw an AssertionError at hard asserts`() {
        // Preparation
        val builder = PojoAssertionBuilder(Type1(42))
            .add { throw Exception("something ugly happend") }

        // Assertion
        assertThrows(AssertionError::class.java) {
            // Execution
            builder.assertToFirstFail()
        }
    }

    @Test
    fun `failing nexted assertions should throw an AssertionError at hard asserts`() {
        // Preparation
        val builder = PojoAssertionBuilder(Type1(42))
            .add { fail("well, something may be wrong") }

        // Assertion
        assertThrows(AssertionError::class.java) {
            // Execution
            builder.assertToFirstFail()
        }
    }

    @Test
    fun `nothing shold happen if nothing fails at hard asserts`() {
        // Preparation
        val builder = PojoAssertionBuilder(Type1(42))
            .add { assertTrue(true, "Well, when this fails, we've got a problem") }

        // Execution
        builder.assertToFirstFail()

        // Assertion
        //well nothing shou happen ;)
    }

    @Test
    fun `exceptions within an assertion should throw an AssertionError at soft asserts`() {
        // Preparation
        val builder = PojoAssertionBuilder(Type1(42))
            .add { throw Exception("something ugly happend") }

        // Assertion
        assertThrows(AssertionError::class.java) {
            // Execution
            builder.assertAll()
        }
    }

    @Test
    fun `failing nexted assertions should throw an AssertionError at soft asserts`() {
        // Preparation
        val builder = PojoAssertionBuilder(Type1(42))
            .add { assertEquals(15, it.value) }

        // Assertion
        assertThrows(AssertionError::class.java) {
            // Execution
            builder.assertAll()
        }
    }

    @Test
    fun `nothing shold happen if nothing fails at soft asserts`() {
        // Preparation
        val builder = PojoAssertionBuilder(Type1(42))
            .add { assertEquals(42, it.value) }

        // Execution
        builder.assertAll()

        // Assertion
        //well nothing shou happen ;)
    }

    @Test
    fun `on hard asserts only the first given failed assertion should be part of the output`() {
        // Preparation
        val builder = PojoAssertionBuilder(Type1(42))
            .add { fail("first") }
            .add { throw Exception("second") }
            .add { fail("third") }

        // Execution
        val result = assertThrows(AssertionError::class.java) {
            builder.assertToFirstFail()
        }

        // Assertion
        assertAll(
            { assertThat(result.message, containsString("first")) },
            { assertThat(result.message, not(containsString("second"))) },
            { assertThat(result.message, not(containsString("third"))) }
        )
    }

    @Test
    fun `on soft asserts all the given failed assertion should be part of the output`() {
        // Preparation
        val builder = PojoAssertionBuilder(Type1(42))
            .add { fail("first") }
            .add { throw Exception("second") }
            .add { fail("third") }

        // Execution
        val result = assertThrows(AssertionError::class.java) {
            builder.assertAll()
        }

        // Assertion
        assertAll(
            { assertThat(result.message, containsString("first")) },
            { assertThat(result.message, containsString("second")) },
            { assertThat(result.message, containsString("third")) }
        )
    }

    @Test
    fun `on soft asserts the inner soft asserts should be called`() {
        // Preparation
        val builder = PojoAssertionBuilder(Type1(42))
            .addAsserter("outerHeading") {
                object : PojoAsserter<Type1> {
                    override fun assertToFirstFail() {
                        throw RuntimeException("totally unexpected so ... not fun")
                    }

                    override fun assertAll() {
                        throw RuntimeException("a little expected inner fun ${it.value}")
                    }

                }
            }

        // Execution
        val result = assertThrows(AssertionError::class.java) {
            builder.assertAll()
        }

        // Assertion
        assertAll(
            { assertThat(result.message, containsString("outerHeading")) },
            { assertThat(result.message, containsString("a little expected inner fun 42")) },
            { assertThat(result.message, not(containsString("totally unexpected so ... not fun"))) }
        )
    }

    @Test
    fun `on hard asserts the inner hard asserts should be called`() {
        // Preparation
        val builder = PojoAssertionBuilder(Type1(42))
            .addAsserter("outerHeading") {
                object : PojoAsserter<Type1> {
                    override fun assertToFirstFail() {
                        throw RuntimeException("a little expected inner fun ${it.value}")
                    }

                    override fun assertAll() {
                        throw RuntimeException("totally unexpected so ... not fun")
                    }

                }
            }

        // Execution
        val result = assertThrows(AssertionError::class.java) {
            builder.assertToFirstFail()
        }

        // Assertion
        assertAll(
            { assertThat(result.message, containsString("outerHeading")) },
            { assertThat(result.message, containsString("a little expected inner fun 42")) },
            { assertThat(result.message, not(containsString("totally unexpected so ... not fun"))) }
        )
    }

    @Test
    fun `on hard asserts a given heading should be used for the assertion error`() {
        // Preparation
        val builder = PojoAssertionBuilder(pojo = Type1(42), heading = "Some nice Failure Heading")
            .add { fail("first") }

        // Execution
        val result = assertThrows(AssertionError::class.java) {
            builder.assertToFirstFail()
        }

        // Assertion
        assertThat(result.message, containsString("Some nice Failure Heading"))
    }

    @Test
    fun `on soft asserts a given heading should be used for the assertion error`() {
        // Preparation
        val builder = PojoAssertionBuilder(pojo = Type1(42), heading = "Some nice Failure Heading")
            .add { fail("first") }

        // Execution
        val result = assertThrows(AssertionError::class.java) {
            builder.assertAll()
        }

        // Assertion
        assertThat(result.message, containsString("Some nice Failure Heading"))
    }

    @Test
    fun `on soft asserts if a postfix heading is given for a specifig assertion it should be added to the heading`() {
        // Preparation
        val builder = PojoAssertionBuilder(pojo = Type1(42), heading = "Some nice Failure Heading")
            .add("additional heading 1") { fail("first") }
            .add("additional heading 2") { fail("second") }

        // Execution
        val result = assertThrows(AssertionError::class.java) {
            builder.assertAll()
        }

        // Assertion
        assertAll(
            { assertThat(result.message, containsString("Some nice Failure Heading")) },
            { assertThat(result.message, containsString("additional heading 1")) },
            { assertThat(result.message, containsString("additional heading 2")) }
        )
    }

    @Test
    fun `on hard asserts if a postfix heading is given for a specifig assertion it should be added to the heading`() {
        // Preparation
        val builder = PojoAssertionBuilder(pojo = Type1(42), heading = "Some nice Failure Heading")
            .add("additional heading 1") { fail("first") }
            .add("additional heading 2") { fail("second") }

        // Execution
        val result = assertThrows(AssertionError::class.java) {
            builder.assertToFirstFail()
        }

        // Assertion
        assertAll(
            { assertThat(result.message, containsString("Some nice Failure Heading")) },
            { assertThat(result.message, containsString("additional heading 1")) },
            { assertThat(result.message, not(containsString("additional heading 2"))) }
        )
    }

}
