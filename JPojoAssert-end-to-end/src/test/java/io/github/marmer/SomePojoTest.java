package io.github.marmer;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SomePojoTest {
    @Test
    @DisplayName("Simple assertions")
    void assertThat_SimpleAssertions() {
        // Preparation

        final var assertionError = assertThrows(AssertionError.class,
                // Execution
                () -> SomePojoAsserter.assertThat(new SomePojo("Helge"))
                        .add(somePojo -> assertEquals("HelgeX", somePojo.getFirstName(), "firstName"))
                        .assertSoftly());
        // Assertion
        MatcherAssert.assertThat(assertionError.toString(), containsString("HelgeX"));
    }

}
