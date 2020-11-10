package io.github.marmer.spike;

import io.github.marmer.testutils.annotationprocessing.jpojoassert.AssertionCallback;
import io.github.marmer.testutils.annotationprocessing.jpojoassert.PojoAssertionBuilder;

import javax.annotation.processing.Generated;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Generated(
        value = "io.github.marmer.testutils.annotationprocessing.jpojoassert.AssertionGeneratorProcessor",
        date = "now")
public class ExampleTypeAsserter<T, E> {
    private final PojoAssertionBuilder<ExampleType<T, E>> pojoAssertionBuilder;

    private ExampleTypeAsserter(final ExampleType<T, E> base) {
        this(new PojoAssertionBuilder<ExampleType<T, E>>(base, Collections.emptyList(), "ExampleType"));
    }

    private ExampleTypeAsserter(final PojoAssertionBuilder<ExampleType<T, E>> pojoAssertionBuilder) {
        this.pojoAssertionBuilder = pojoAssertionBuilder;
    }

    public static <T, E> ExampleTypeAsserter<T, E> prepareFor(final ExampleType<T, E> base) {
        return new ExampleTypeAsserter<>(base);
    }

    public ExampleTypeAsserter<T, E> with(final AssertionCallback<ExampleType<T, E>> assertionCallback) {
        return new ExampleTypeAsserter<>(pojoAssertionBuilder.add(assertionCallback));
    }

    public ExampleTypeAsserter<T, E> withGenericProperty(final AssertionCallback<Map<String, List<Integer>>> assertionCallback) {
        return new ExampleTypeAsserter<>(pojoAssertionBuilder.add(base -> assertionCallback.accept(base.getGenericProperty())));
    }

    public void assertToFirstFail() {
        pojoAssertionBuilder.assertToFirstFail();
    }

    public void assertAll() {
        pojoAssertionBuilder.assertAll();
    }
}
