package io.github.marmer;

import io.github.marmer.SomePojoAsserter.AddressAsserter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

class SomePojoTest {
    @Test
    @DisplayName("Simple assertions")
    void simpleAssertions() {
        // Preparation
        final var assertionError = assertThrows(AssertionError.class,
                // Execution
                () -> SomePojoAsserter.prepareFor(new SomePojo<>("Helge", List.of("Prof.", "Dr."), new SomePojo.Address("x", "y")) {
                })
                        .with(it -> assertEquals("HelgeX", it.getFirstName(), "firstName"))
                        .with(it -> {
                            throw new Exception("Fancy Exception");
                        })
                        .withFirstName(it -> assertEquals("HelgeY", it))
                        .withTitles(it -> assertThat(it, contains("Prof.", "Dr.")))
                        .assertAll());
        // Assertion
        assertAll(
                () -> assertThat(assertionError.toString(), containsString("HelgeX")),
                () -> assertThat(assertionError.toString(), containsString("HelgeY")),
                () -> assertThat(assertionError.toString(), containsString("Fancy Exception"))
        );
    }

    @Test
    @DisplayName("Nested asserters")
    void NestedAsserters()
            throws Exception {
        // Preparation
        final SomePojo<String> pojo = new SomePojo<>("Helge", List.of("Prof.", "Dr."), new SomePojo.Address("x", "y")) {
        };
        // TODO: Idea
        //        public SomePojoAsserter<T> hasAddress(
        //        final Function<AddressAsserter,AddressAsserter> asserterFunction) {
        //            return new SomePojoAsserter<T>(pojoAssertionBuilder.add("address", base -> asserterFunction.apply(AddressAsserter.prepareFor(base.getAddress()))));
        //        }
        final var assertionError = assertThrows(AssertionError.class,
                // Execution
                () -> SomePojoAsserter.prepareFor(pojo)
                        .hasAddress(it -> it
                                .hasCity("BadStreed")
                                .hasStreet("Gotham"))
                        .hasFirstName("Holge")
                        .assertAll());
        // Assertion
        assertAll(
                () -> assertThat(assertionError.toString(), containsString("Holge")),
                () -> assertThat(assertionError.toString(), containsString("BadStreed")),
                () -> assertThat(assertionError.toString(), containsString("Gotham"))
        );
    }

    @Test
    @DisplayName("Nested types")
    void NestedTypes() {
        // Preparation

        final var assertionError = assertThrows(AssertionError.class,
                // Execution
                () -> AddressAsserter.prepareFor(new SomePojo.Address("Smurf Village", "Mushroom"))
                        .hasCity("Somewhere")
                        .assertAll());
        // Assertion
        assertAll(
                () -> assertThat(assertionError.toString(), containsString("Smurf Village")),
                () -> assertThat(assertionError.toString(), containsString("Somewhere")),
                () -> assertThat(assertionError.toString(), containsString("city"))
        );
    }

    @Test
    @DisplayName("Type and property names withi error messages")
    void typeAndPropertyNamesWithiErrorMessages() {
        // Preparation
        final var assertionError = assertThrows(AssertionError.class,
                // Execution
                () -> SomePojoAsserter.prepareFor(new SomePojo<>("Helge", List.of("Prof.", "Dr."), new SomePojo.Address("x", "y")) {
                })
                        .with(it -> assertEquals("HelgeX", it.getFirstName()))
                        .with(it -> {
                            throw new Exception("Fancy Exception");
                        })
                        .withFirstName(it -> assertEquals("HelgeY", it))
                        .matches(hasProperty("notExistingProp"))
                        .withTitles(it -> assertThat(it, contains("Prof.", "Dr.", "Dr.")))
                        .assertAll());
        // Assertion
        assertAll(
                () -> assertThat(assertionError.toString(), containsString("SomePojo")),
                () -> assertThat(assertionError.toString(), containsString("firstName")),
                () -> assertThat(assertionError.toString(), containsString("titles")),
                () -> assertThat(assertionError.toString(), containsString("notExistingProp"))
        );
    }

    @Test
    @DisplayName("Convenience property comparison methods should work as expected")
    void conveniencePropertyComparisonMethodsShouldWorkAsExpected() {
        // Preparation
        final var assertionError = assertThrows(AssertionError.class,
                // Execution
                () -> SomePojoAsserter.prepareFor(new SomePojo<>("Helge", List.of("Prof.", "Dr."), new SomePojo.Address("x", "y")) {
                })
                        .hasTitles(contains("Prof."))
                        .hasFirstName("Holge")
                        .hasClass(SomePojo.class)
                        .assertAll());
        // Assertion
        assertAll(
                () -> assertThat(assertionError.toString(), containsString("Dr.")),
                () -> assertThat(assertionError.toString(), containsString("Prof.")),
                () -> assertThat(assertionError.toString(), containsString("Helge")),
                () -> assertThat(assertionError.toString(), containsString("Holge"))
        );
    }

}
