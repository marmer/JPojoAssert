package io.github.marmer.testutils.annotationprocessing.jpojoassert

import com.google.common.truth.Truth
import com.google.testing.compile.JavaFileObjects
import com.google.testing.compile.JavaSourcesSubjectFactory
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

internal class AssertionGeneratorProcessorTest {
    @Test
    fun `simple asserters should be generated with a default configuration`() {
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
        val now = LocalDateTime.of(1985, 1, 2, 3, 4, 5, 123000000)
        val expectedOutput = JavaFileObjects.forSourceString(
            "some.other.pck.SimplePojoInterfaceAsserter", """package some.other.pck;

import io.github.marmer.testutils.annotationprocessing.jpojoassert.AssertionCallback;import io.github.marmer.testutils.annotationprocessing.jpojoassert.PojoAssertionBuilder;
import java.util.Collections;
import javax.annotation.processing.Generated;

@Generated(
value = "io.github.marmer.testutils.annotationprocessing.jpojoassert.AssertionGeneratorProcessor",
date = "$now")
public class SimplePojoInterfaceAsserter{
    private final PojoAssertionBuilder<SimplePojoInterface> pojoAssertionBuilder;

    private SimplePojoInterfaceAsserter(final SimplePojoInterface base) {
        this(new PojoAssertionBuilder<SimplePojoInterface>(base, Collections.emptyList(), "SimplePojoInterface"));
    }

    private SimplePojoInterfaceAsserter(final PojoAssertionBuilder<SimplePojoInterface> pojoAssertionBuilder) {
        this.pojoAssertionBuilder = pojoAssertionBuilder;
    }

    public static SimplePojoInterfaceAsserter assertThat(final SimplePojoInterface base) {
        return new SimplePojoInterfaceAsserter(base);
    }

    public SimplePojoInterfaceAsserter with(final AssertionCallback<SimplePojoInterface> assertionCallback) {
        return new SimplePojoInterfaceAsserter(pojoAssertionBuilder.add(assertionCallback));
    }

    public void assertHardly() {
        pojoAssertionBuilder.assertHardly();
    }

    public void assertSoftly() {
        pojoAssertionBuilder.assertSoftly();
    }
}"""
        )

        // Execution
        Truth.assert_()
            .about(JavaSourcesSubjectFactory.javaSources())
            .that(listOf(configurationClass, javaFileObject))
            .processedWith(AssertionGeneratorProcessor { now })
            // Assertion
            .compilesWithoutError()
            .and()
            .generatesSources(expectedOutput)
    }
}

// TODO: marmer 02.11.2020 test elements in root package
// TODO: marmer 02.11.2020 test all types used as base for generation (primitives, Objects, Arrays, Void?, Same name, Generics, inner Types)
// TODO: marmer 02.11.2020 test all types for fields at least (primitives, Objects, Arrays, Void?, Same name, Generics, inner Types)
// TODO: marmer 02.11.2020 add Test from experience of Hamcrest Matcher generator
// TODO: marmer 02.11.2020 test Elements in different modules
