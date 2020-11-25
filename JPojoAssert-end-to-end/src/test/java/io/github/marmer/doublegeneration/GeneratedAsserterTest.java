package io.github.marmer.doublegeneration;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.*;

class GeneratedAsserterTest {
    @Test
    @DisplayName("There should nothing be generated for the already self generated type")
    void thereShouldNothingBeGeneratedForTheAlreadySelfGeneratedType() {
        // Execution
        Assertions.assertThrows(ClassNotFoundException.class, () -> Class.forName("io.github.marmer.doublegeneration.GeneratedAsserterAsserter"));
    }

    @Test
    @DisplayName("Generation for types of other generated types should work")
    void GenerationForTypesOfOtherGeneratedTypesShouldWork()
            throws Exception {
        // Preparation

        // Execution
        final var clazz = Class.forName("io.github.marmer.doublegeneration.FromDifferentGeneratorsGeneratedTypeAsserter");

        // Assertion
        MatcherAssert.assertThat("Class should have been generated", clazz, is(not(nullValue())));
    }

}
