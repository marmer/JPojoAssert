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

import org.jetbrains.annotations.NotNull;

import javax.annotation.processing.Generated;
import java.util.function.Consumer;

import static java.util.Collections.emptyList;

@Generated(
value = "io.github.marmer.testutils.annotationprocessing.jpojoassert.AssertionGeneratorProcessor",
date = "$today")
public class SimplePojoInterfaceAsserter{
    private final PojoAssertionBuilder<SimplePojoInterface> pojoAssertionBuilder;

    public SimplePojoInterfaceAsserter(SimplePojoInterface pojo) {
        this.pojoAssertionBuilder = new PojoAssertionBuilder<SimplePojoInterface>(pojo, emptyList(), "SimplePojo");
    }

    private SimplePojoInterfaceAsserter(PojoAssertionBuilder<SimplePojoInterface> pojoPojoAssertionBuilder) {
        this.pojoAssertionBuilder = pojoPojoAssertionBuilder;
    }

    public SimplePojoInterfaceAsserter add(Consumer<SimplePojoInterface> assertionCallback) {
        return new SimplePojoInterfaceAsserter(pojoAssertionBuilder.add(pojo -> {
            assertionCallback.accept(pojo);
            return null;
        }));
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
            .that(Arrays.asList(configurationClass, javaFileObject))
            .processedWith(AssertionGeneratorProcessor())
            // Assertion
            .compilesWithoutError()
            .and()
            .generatesSources(expectedOutput)
    }
}
