package io.github.marmer.testutils.annotationprocessing.jpojoassert;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PojoAssertionBuilderJavaTest {

    @Test
    @DisplayName("Callbacks should work with java")
    void assertHard_CallbacksShouldWorkWithJava() {

        // Preparation
        final PojoAssertionBuilder<SomeType> builder = new PojoAssertionBuilder<>(new SomeType(), emptyList(), "someHeading")
                .add(it -> {
                    assertEquals(43, it.getValue());
                });

        // Assertion
        assertThrows(AssertionError.class, builder::assertHardly);
    }

    private static class SomeType {
        public int getValue() {
            return 42;
        }

    }
}
