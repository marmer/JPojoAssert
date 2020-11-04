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
            .isInstanceOfSomePojo()
            .hasPropertyFirstProperty()
            .hasPropertyFirstProperty("Some value")
            .hasPropertyFirstProperty(equalTo("Some value"))
            .firstProperty { assertThat(it, equalTo("Some value")) }
            .hasSecondProperty()
            .hasSecondProperty(42)
            .hasSecondProperty(equalTo(42))
            .secondProperty { assertThat(it, equalTo(42)) }
//            .matches(hasProperty("notExistingProperty"))
//            .matches { assertThat(it, hasProperty("notExistingProperty")) }
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

    fun matches(dynamicAssertion: (SomePojo) -> Unit) =
        plusAssertion { dynamicAssertion(pojo) }

    // ###### First Property ###########
    fun hasPropertyFirstProperty() =
        plusAssertion {
            assertThat(
                additionalMessage,
                pojo,
                hasProperty("firstProperty")
            )
        }

    fun hasPropertyFirstProperty(value: String?) =
        plusAssertion {
            assertThat(
                additionalMessage,
                pojo,
                hasProperty("firstProperty", equalTo(value))
            )
        }

    fun hasPropertyFirstProperty(matcher: Matcher<String>) =
        plusAssertion {
            assertThat(
                additionalMessage,
                pojo,
                hasProperty("firstProperty", matcher)
            )
        }

    fun firstProperty(dynamicAssertion: (String) -> Unit) =
        plusAssertion { dynamicAssertion(pojo.firstProperty) }

    // ###### Second Property ###########
    fun hasSecondProperty() =
        plusAssertion {
            assertThat(
                additionalMessage,
                pojo,
                hasProperty("secondProperty")
            )
        }

    fun hasSecondProperty(value: Int?) =
        plusAssertion {
            assertThat(
                additionalMessage,
                pojo,
                hasProperty("secondProperty", equalTo(value))
            )
        }

    fun hasSecondProperty(matcher: Matcher<Int>) =
        plusAssertion {
            assertThat(
                additionalMessage,
                pojo,
                hasProperty("secondProperty", matcher)
            )
        }

    fun secondProperty(dynamicAssertion: (Int) -> Unit) =
        plusAssertion { dynamicAssertion(pojo.secondProperty) }


    private fun plusAssertion(newAssertion: () -> Unit) =
        SomePojoAsserter(pojo, additionalMessage, assertions + newAssertion)

    fun isInstanceOfSomePojo() = plusAssertion { assertThat(pojo, instanceOf(SomePojo::class.java)) }

    companion object {
        @JvmStatic
        fun assertThat(pojo: SomePojo) = SomePojoAsserter(pojo)
    }
}

