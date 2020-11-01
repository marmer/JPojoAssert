package io.github.marmer

import io.github.marmer.testutils.annotationprocessing.jpojoassert.GenerateAsserter

data class SomeType(val someProp: String)

@GenerateAsserter("SomeType")
class JPojoAssertConfiguration
