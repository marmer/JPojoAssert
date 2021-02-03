package io.github.marmer.testutils.annotationprocessing.jpojoassert;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static java.util.Collections.emptyList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

class PojoAssertionBuilderJavaTest {

    @Test
    @DisplayName("it should be possible to perform assertions for properties")
    void itShouldBePossibleToPerformAssertionsForProperties()
            throws Exception {
        // Preparation
        final var builder = new PojoAssertionBuilder<>(new SomeType(), emptyList(), "someBaseHeading")
                .addForProperty("value", (Integer it) -> {
                    assertThat(it, is(42));
                });

        // Execution
        builder.assertAll();

        // Assertion
        fail("Finish implementation");
        fail("Add test for appropriate error message!");
    }

    @Test
    @DisplayName("Callbacks should work with java and contain the expected outputs")
    void callbacksShouldWorkWithJava() {

        // Preparation
        final PojoAssertionBuilder<SomeType> builder = new PojoAssertionBuilder<>(new SomeType(), emptyList(), "someBaseHeading")
                .add(it -> assertEquals(43, it.getValue()))
                .add("someProp", it -> assertEquals(44, it.getValue()))
                .addAsserter("bla", it -> new PojoAsserter<String>() {
                    @Override
                    public void assertToFirstFail() {
                        fail("totally unexpected Exception of a nested failure");
                    }

                    @Override
                    public void assertAll() {
                        fail("a little expected inner fun " + it.getValue());
                    }
                });

        // Assertion
        final AssertionError assertionError = assertThrows(AssertionError.class, builder::assertAll);
        assertAll(
            () -> assertThat(assertionError.toString(), containsString("42")),
            () -> assertThat(assertionError.toString(), containsString("43")),
            () -> assertThat(assertionError.toString(), containsString("44")),
            () -> assertThat(assertionError.toString(), containsString("someProp")),
            () -> assertThat(assertionError.toString(), containsString("someBaseHeading")),
            () -> assertThat(assertionError.toString(), not(containsString("totally unexpected Exception of a nested failure"))),
            () -> assertThat(assertionError.toString(), containsString("a little expected inner fun 42"))
        );
    }


    private static class SomeType {
        public int getValue() {
            return 42;
        }

    }
}
