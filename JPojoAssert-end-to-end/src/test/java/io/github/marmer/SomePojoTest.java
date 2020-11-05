package io.github.marmer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SomePojoTest {
    @Test
    @DisplayName("Simple assertions")
    void assertThat_SimpleAssertions() {
        // Preparation

        final var assertionError = assertThrows(AssertionError.class,
                // Execution
                () -> SomePojoAsserter.prepareFor(new SomePojo("Helge"))
                        .with(it -> org.junit.jupiter.api.Assertions.assertEquals("HelgeX", it.getFirstName(), "firstName"))
                        .with(it -> {
                            throw new Exception("Fancy Exception");
                        })
                        .withFirstName(it -> org.junit.jupiter.api.Assertions.assertEquals("HelgeY", it))
                        .assertSoftly());
        // Assertion
        assertAll(
                () -> assertThat(assertionError.toString(), containsString("HelgeX")),
                () -> assertThat(assertionError.toString(), containsString("HelgeY")),
                () -> assertThat(assertionError.toString(), containsString("Fancy Exception"))
        );

    }

}
