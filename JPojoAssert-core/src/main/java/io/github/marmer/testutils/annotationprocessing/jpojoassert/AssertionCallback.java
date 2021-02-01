package io.github.marmer.testutils.annotationprocessing.jpojoassert;

/**
 * This callback is used for lazy assertions.
 *
 * @param <T> The type
 */
@FunctionalInterface
public interface AssertionCallback<T> {
    /**
     * Prepares a lazy assertion.
     *
     * @param value Value used for the assertion
     * @throws Exception Is allowed to throw any kind of exception. Is handled by the evaluating asserter
     */
    @SuppressWarnings("java:S112")
    // This happeny by intention
    void assertFor(T value) throws Exception;
}
