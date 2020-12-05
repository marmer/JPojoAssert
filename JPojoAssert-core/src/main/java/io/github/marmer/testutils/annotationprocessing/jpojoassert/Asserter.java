package io.github.marmer.testutils.annotationprocessing.jpojoassert;

/**
 * Types implementing this interface are able to assert something. Either softly ({@link #assertAll()} or hardly {@link #assertToFirstFail()}.
 */
public interface Asserter {

    /**
     * Performs a hard assertion. If this asserters job is to assert many conditions, the assertion stops right when the first assertion fails. Should be used for non atomic assertions.
     */
    void assertToFirstFail();

    /**
     * Performs a soft assertion. If this asserters job is to assert many conditions, the assertion performs all of them and fails at the end. Should be used for atomic assertions.
     */
    void assertAll();
}
