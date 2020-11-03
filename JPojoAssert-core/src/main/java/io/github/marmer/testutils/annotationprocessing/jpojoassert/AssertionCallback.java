package io.github.marmer.testutils.annotationprocessing.jpojoassert;

public interface AssertionCallback<T> {
    void accept(T value) throws Exception;
}
