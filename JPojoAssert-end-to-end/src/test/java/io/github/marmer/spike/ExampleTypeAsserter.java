
package io.github.marmer.spike;

import io.github.marmer.testutils.annotationprocessing.jpojoassert.AssertionCallback;
import io.github.marmer.testutils.annotationprocessing.jpojoassert.PojoAssertionBuilder;

import javax.annotation.processing.Generated;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@Generated(
        value = "io.github.marmer.testutils.annotationprocessing.jpojoassert.AssertionGeneratorProcessor",
        date = "now")
public class ExampleTypeAsserter<A extends CharSequence, B extends Consumer<A>, C extends Consumer<String>, D> {
    private final PojoAssertionBuilder<ExampleType<A, B, C, D>> pojoAssertionBuilder;

    private ExampleTypeAsserter(final ExampleType<A, B, C, D> base) {
        this(new PojoAssertionBuilder<ExampleType<A, B, C, D>>(base, Collections.emptyList(), "ExampleType"));
    }

    private ExampleTypeAsserter(final PojoAssertionBuilder<ExampleType<A, B, C, D>> pojoAssertionBuilder) {
        this.pojoAssertionBuilder = pojoAssertionBuilder;
    }

    public static <A extends CharSequence, B extends Consumer<A>, C extends Consumer<String>, D> ExampleTypeAsserter<A, B, C, D> prepareFor(final ExampleType<A, B, C, D> base) {
        return new ExampleTypeAsserter<A, B, C, D>(base);
    }

    public ExampleTypeAsserter<A, B, C, D> with(final AssertionCallback<ExampleType<A, B, C, D>> assertionCallback) {
        return new ExampleTypeAsserter<A, B, C, D>(pojoAssertionBuilder.add(assertionCallback));
    }

    public ExampleTypeAsserter<A, B, C, D> withGenericProperty(final AssertionCallback<Map<String, List<Integer>>> assertionCallback) {
        return new ExampleTypeAsserter<A, B, C, D>(pojoAssertionBuilder.add(base -> assertionCallback.accept(base.getGenericProperty())));
    }

    public ExampleTypeAsserter<A, B, C, D> withGenericFromTypeDefinitionProperty(final AssertionCallback<C> assertionCallback) {
        return new ExampleTypeAsserter<A, B, C, D>(pojoAssertionBuilder.add(base -> assertionCallback.accept(base.getGenericFromTypeDefinitionProperty())));
    }

    public ExampleTypeAsserter<A, B, C, D> withGenericFromTypeDefinitionPropertyAsGeneric(final AssertionCallback<List<C>> assertionCallback) {
        return new ExampleTypeAsserter<A, B, C, D>(pojoAssertionBuilder.add(base -> assertionCallback.accept(base.getGenericFromTypeDefinitionPropertyAsGeneric())));
    }

    public void assertToFirstFail() {
        pojoAssertionBuilder.assertToFirstFail();
    }

    public void assertAll() {
        pojoAssertionBuilder.assertAll();
    }
}
