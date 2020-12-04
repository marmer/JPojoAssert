package io.github.marmer.testutils.annotationprocessing.jpojoassert;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static java.util.Collections.emptyList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;

class PojoAssertionBuilderJavaTest {

    @Test
    @DisplayName("Callbacks should work with java and contain the expected outputs")
    void callbacksShouldWorkWithJava() {

        // Preparation
        final PojoAssertionBuilder<SomeType> builder = new PojoAssertionBuilder<>(new SomeType(), emptyList(), "someBaseHeading")
                .add(it -> assertEquals(43, it.getValue()))
                .add("someProp", it -> assertEquals(44, it.getValue()))
                .addAsserter("bla", new PojoAsserter<String>() {
                    @Override
                    public void assertToFirstFail() {
                        throw new UnsupportedOperationException("not implemented yet");
                    }

                    @Override
                    public void assertAll() {
                        fail("totally unexpected Exception of a nested failure");
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
                () -> assertThat(assertionError.toString(), containsString("totally unexpected Exception of a nested failure"))
        );
    }


    private static class SomeType {
        public int getValue() {
            return 42;
        }

    }
}
