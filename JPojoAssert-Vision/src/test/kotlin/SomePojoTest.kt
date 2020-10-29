import org.hamcrest.Matcher
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test

data class SomePojo(
    val firstProperty: String,
    val secondProperty: Int
)


internal class SomePojoTest {
    @Test
    fun `just some test`() {
        // Preparation

        val pojo = SomePojo("Some value", 42)

        // Execution

        // Assertion
        SomePojoAsserter.assertThat(pojo)
//            .matches(hasProperty("notExistingProperty"))
            .isInstanceOfSomePojo()
            .withFirstProperty()
            .withFirstProperty("Some value")
            .withFirstProperty(equalTo("Some value"))
            .withFirstProperty { assertThat(it, equalTo("Some value")) }
            .withSecondProperty()
            .withSecondProperty(42)
            .withSecondProperty(equalTo(42))
            .withSecondProperty { assertThat(it, equalTo("42")) }
//            .assertHardly()
            .assertSoftly()
    }
}


class SomePojoAsserter private constructor(
    val pojo: SomePojo,
    val additionalMessage: String = "SomePojo",
    val assertions: List<() -> Unit> = emptyList()
) {

    fun assertHardly() {
        assertions.forEach { it() }
    }

    private fun (() -> Unit).toAssertionError(): AssertionError? =
        try {
            this()
            null
        } catch (e: AssertionError) {
            e
        }

    fun assertSoftly() =
        with(assertions
            .map { it.toAssertionError() }
            .filterNotNull()
        ) {
            if (isNotEmpty()) throw AssertionError(map {
                //to readable assertion

                it.message + "\n" + it.stackTrace.map { "\t$it" }.joinToString("\n")
            }.joinToString(separator = "\n\n"))
        }

    fun matches(matcher: Matcher<SomePojo>) =
        plusAssertion { assertThat(pojo, matcher) }

    // ###### First Property ###########
    fun withFirstProperty() =
        plusAssertion {
            assertThat(
                additionalMessage,
                pojo,
                hasProperty("firstProperty")
            )
        }

    fun withFirstProperty(value: String?) =
        plusAssertion {
            assertThat(
                additionalMessage,
                pojo,
                hasProperty("firstProperty", equalTo(value))
            )
        }

    fun withFirstProperty(matcher: Matcher<String>) =
        plusAssertion {
            assertThat(
                additionalMessage,
                pojo,
                hasProperty("firstProperty", matcher)
            )
        }

    fun withFirstProperty(dynamicAssertion: (String) -> Unit) =
        plusAssertion { dynamicAssertion(pojo.firstProperty) }

    // ###### Second Property ###########
    fun withSecondProperty() =
        plusAssertion {
            assertThat(
                additionalMessage,
                pojo,
                hasProperty("secondProperty")
            )
        }

    fun withSecondProperty(value: Int?) =
        plusAssertion {
            assertThat(
                additionalMessage,
                pojo,
                hasProperty("secondProperty", equalTo(value))
            )
        }

    fun withSecondProperty(matcher: Matcher<Int>) =
        plusAssertion {
            assertThat(
                additionalMessage,
                pojo,
                hasProperty("secondProperty", matcher)
            )
        }

    fun withSecondProperty(dynamicAssertion: (Int) -> Unit) =
        plusAssertion { dynamicAssertion(pojo.secondProperty) }


    private fun plusAssertion(newAssertion: () -> Unit) =
        SomePojoAsserter(pojo, additionalMessage, assertions + newAssertion)

    fun isInstanceOfSomePojo() = plusAssertion { assertThat(pojo, instanceOf(SomePojo::class.java)) }

    companion object {
        @JvmStatic
        fun assertThat(pojo: SomePojo) = SomePojoAsserter(pojo)
    }
}

