package io.github.marmer.testutils.annotationprocessing.jpojoassert

@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
/**
 * Configuration for the generation of Asserter for types.
 */
annotation class GenerateAsserter(
    /** Package or full qualified Type name for which an asserter shall be generated */
    val value: Array<String>
)

