package io.github.marmer.testutils.annotationprocessing.jpojoassert

import com.google.common.truth.Truth
import com.google.testing.compile.JavaFileObjects
import com.google.testing.compile.JavaSourcesSubjectFactory
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

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
        val now = LocalDateTime.of(1985, 1, 2, 3, 4, 5, 123000000)
        val expectedOutput = JavaFileObjects.forSourceString(
            "some.other.pck.SimplePojoInterfaceAsserter", """package some.other.pck;

import org.jetbrains.annotations.NotNull;

import javax.annotation.processing.Generated;
import java.util.function.Consumer;

import static java.util.Collections.emptyList;

@Generated(
value = "io.github.marmer.testutils.annotationprocessing.jpojoassert.AssertionGeneratorProcessor",
date = "$now")
public class SimplePojoInterfaceAsserter{
    private final PojoAssertionBuilder<SimplePojoInterface> pojoAssertionBuilder;

    private SimplePojoInterfaceAsserter(SimplePojoInterface base) {
        this(new PojoAssertionBuilder<SimplePojoInterface>(base, emptyList(), "SimplePojo"));
    }

    private SimplePojoInterfaceAsserter(PojoAssertionBuilder<SimplePojoInterface> pojoPojoAssertionBuilder) {
        this.pojoAssertionBuilder = pojoPojoAssertionBuilder;
    }

    public static SimplePojoInterfaceAsserter assertThat(SimplePojoInterface base) {
        return new SimplePojoInterfaceAsserter(base);
    }

    public SimplePojoInterfaceAsserter add(@NotNull Consumer<SimplePojoInterface> assertionCallback) {
        return new SimplePojoInterfaceAsserter(pojoAssertionBuilder.add(base -> {
            assertionCallback.accept(base);
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
            .that(listOf(configurationClass, javaFileObject))
            .processedWith(AssertionGeneratorProcessor { now })
            // Assertion
            .compilesWithoutError()
            .and()
            .generatesSources(expectedOutput)
    }
}
