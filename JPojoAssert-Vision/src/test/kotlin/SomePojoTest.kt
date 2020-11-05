import org.hamcrest.Matcher
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test

data class SomePojo(
    val firstName: String,
    val noseCount: Int
)


internal class SomePojoTest {
    @Test
    fun `just some test`() {
        // Preparation

        val pojo = SomePojo("Some value", 42)

        // Execution

        // Assertion
        SomePojoAsserter.prepareFor(pojo)
            .isInstanceOfSomePojo()
            .hasPropertyFirstName()
            .hasPropertyFirstName("Some value")
            .hasPropertyFirstName(equalTo("Some value"))
            .withFirstName { assertThat(it, equalTo("Some value")) }
            .hasNoseCount()
            .hasNoseCount(42)
            .hasNoseCount(equalTo(42))
            .withNoseCount { assertThat(it, equalTo(42)) }
//            .with(hasProperty("notExistingProperty"))
//            .with { assertThat(it, hasProperty("notExistingProperty")) }
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

    fun with(matcher: Matcher<SomePojo>) =
        plusAssertion { assertThat(pojo, matcher) }

    fun with(dynamicAssertion: (SomePojo) -> Unit) =
        plusAssertion { dynamicAssertion(pojo) }

    // ###### First Property ###########
    fun hasPropertyFirstName() =
        plusAssertion {
            assertThat(
                additionalMessage,
                pojo,
                hasProperty("firstName")
            )
        }

    fun hasPropertyFirstName(value: String?) =
        plusAssertion {
            assertThat(
                additionalMessage,
                pojo,
                hasProperty("firstName", equalTo(value))
            )
        }

    fun hasPropertyFirstName(matcher: Matcher<String>) =
        plusAssertion {
            assertThat(
                additionalMessage,
                pojo,
                hasProperty("firstName", matcher)
            )
        }

    fun withFirstName(dynamicAssertion: (String) -> Unit) =
        plusAssertion { dynamicAssertion(pojo.firstName) }

    // ###### Second Property ###########
    fun hasNoseCount() =
        plusAssertion {
            assertThat(
                additionalMessage,
                pojo,
                hasProperty("noseCount")
            )
        }

    fun hasNoseCount(value: Int?) =
        plusAssertion {
            assertThat(
                additionalMessage,
                pojo,
                hasProperty("noseCount", equalTo(value))
            )
        }

    fun hasNoseCount(matcher: Matcher<Int>) =
        plusAssertion {
            assertThat(
                additionalMessage,
                pojo,
                hasProperty("noseCount", matcher)
            )
        }

    fun withNoseCount(dynamicAssertion: (Int) -> Unit) =
        plusAssertion { dynamicAssertion(pojo.noseCount) }


    private fun plusAssertion(newAssertion: () -> Unit) =
        SomePojoAsserter(pojo, additionalMessage, assertions + newAssertion)

    fun isInstanceOfSomePojo() = plusAssertion { assertThat(pojo, instanceOf(SomePojo::class.java)) }

    companion object {
        @JvmStatic
        fun prepareFor(pojo: SomePojo) = SomePojoAsserter(pojo)
    }
}

