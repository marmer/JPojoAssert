package io.github.marmer.testutils.annotationprocessing.jpojoassert

import com.google.common.truth.Truth
import com.google.testing.compile.JavaFileObjects
import com.google.testing.compile.JavaSourcesSubjectFactory
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.util.*

internal class AssertionGeneratorProcessorTest {
    @Test
    fun `something should be generated`() {
        // Preparation
        val configurationClass = JavaFileObjects.forSourceLines(
            "some.pck.JPojoAssertConfiguration", """package some.pck;

import io.github.marmer.testutils.annotationprocessing.jpojoassert.GenerateAsserter;

@GenerateAsserter("some.other.pck.SimplePojoInterface")
public class JPojoAssertConfiguration{}
"""
        )
        val javaFileObject = JavaFileObjects.forSourceLines(
            "some.other.pck.SimplePojoInterface",
            """package some.other.pck;

public interface SimplePojoInterface{
    String getSomeStringProperty();
}"""
        )
        val today = LocalDate.now().toString()
        val expectedOutput = JavaFileObjects.forSourceString(
            "some.other.pck.SimplePojoInterfaceAsserter", """package some.other.pck;

import javax.annotation.processing.Generated;

@Generated(
value = "io.github.marmer.testutils.annotationprocessing.jpojoassert.AssertionGeneratorProcessor",
date = "$today")
public class SimplePojoInterfaceAsserter{
}"""
        )

        // Execution
        Truth.assert_()
            .about(JavaSourcesSubjectFactory.javaSources())
            .that(Arrays.asList(configurationClass, javaFileObject))
            .processedWith(AssertionGeneratorProcessor())
            // Assertion
            .compilesWithoutError()
            .and()
            .generatesSources(expectedOutput)
    }
}
