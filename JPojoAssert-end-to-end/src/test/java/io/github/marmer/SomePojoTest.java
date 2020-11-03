package io.github.marmer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;

class SomePojoTest {
    @Test
    @DisplayName("Simple assertions")
    void assertThat_SimpleAssertions() {
        // Preparation

        final var assertionError = assertThrows(AssertionError.class,
                // Execution
                () -> SomePojoAsserter.assertThat(new SomePojo("Helge"))
                        .add(somePojo -> assertEquals("HelgeX", somePojo.getFirstName(), "firstName"))
                        .add(toConsume -> {
                            throw new Exception("Fancy Exception");
                        })
                        .assertSoftly());
        // Assertion
        assertAll(
                () -> assertThat(assertionError.toString(), containsString("HelgeX")),
                () -> assertThat(assertionError.toString(), containsString("Fancy Exception"))
        );

    }

}
