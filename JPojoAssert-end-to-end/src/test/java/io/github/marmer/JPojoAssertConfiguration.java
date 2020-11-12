package io.github.marmer;

import io.github.marmer.testutils.annotationprocessing.jpojoassert.GenerateAsserter;

@GenerateAsserter({
        "io.github.marmer.SomePojo",
        "io.github.marmer.AnotherPojo"
})
class JPojoAssertConfiguration {

}
