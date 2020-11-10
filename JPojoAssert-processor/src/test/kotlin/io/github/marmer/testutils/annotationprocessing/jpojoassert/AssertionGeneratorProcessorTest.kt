package io.github.marmer.testutils.annotationprocessing.jpojoassert

import com.google.common.truth.Truth
import com.google.testing.compile.JavaFileObjects
import com.google.testing.compile.JavaSourcesSubjectFactory
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

internal class AssertionGeneratorProcessorTest {
    @Test
    fun `simple asserters should be generated with a default configuration`() {
        // Preparation
        @Language("JAVA") val configurationClass = JavaFileObjects.forSourceLines(
            "some.pck.JPojoAssertConfiguration", """
                package some.pck;
                
                import io.github.marmer.testutils.annotationprocessing.jpojoassert.GenerateAsserter;
                
                @GenerateAsserter("some.other.pck.ExampleType")
                public class JPojoAssertConfiguration{}
                """.trimIndent()
        )
        @Language("JAVA") val javaFileObject = JavaFileObjects.forSourceLines(
            "some.other.pck.ExampleType", """
                package some.other.pck;
                
                import java.util.List;
                import java.util.Map;
                
                public abstract class ExampleType{
                    public String getObjectProperty(){return null;}
                    public int getPrimitiveProperty(){return 0;}
                    public boolean isPrimitiveBooleanProperty(){return false;}
                    public boolean isGetRightBooleanMixProperty(){return false;}
                    public boolean getIsWrongPrimitiveBooleanProperty(){return false;}
                    public Boolean isGetWrongBooleanWrapperProperty(){return false;}
                    public Boolean getIsRightBooleanWrapperProperty(){return false;}
                    public int[] getPrimitiveArrayProperty(){return null;}
                    public String[] getObjectArrayProperty(){return null;}
                    public String[][] getMultidimensionalArrayProperty(){return null;}
                    public void getVoidPropertyLike(){}
                    public String getParameterizedPropertyLike(String prop){return null;}
                    String getPackagePrivateProperty(){return null;}
                    protected String getProtectedProperty(){return null;}
                    private String getPrivateProperty(){return null;}
                    public abstract String getAbstractProperty();
                    public final String getFinalProperty(){return null;}
                    
                    // TODO: marmer 08.11.2020 Type is Interface 
                    // TODO: marmer 08.11.2020 Type is Abstract class
                    // TODO: marmer 08.11.2020 Type is Enum class
                    // TODO: marmer 08.11.2020 Type is at least package private 
                    // TODO: marmer 08.11.2020 Type with Generics 
                    // TODO: marmer 08.11.2020 Type with wildcard generics 
                    // TODO: marmer 08.11.2020 No Generation for self generated types 
                
                }""".trimIndent()
        )

        val now = LocalDateTime.of(1985, 1, 2, 3, 4, 5, 123000000)
        @Language("JAVA") val expectedOutput = JavaFileObjects.forSourceString(
            "some.other.pck.ExampleTypeAsserter", """
                package some.other.pck;
                
                import io.github.marmer.testutils.annotationprocessing.jpojoassert.AssertionCallback;
                import io.github.marmer.testutils.annotationprocessing.jpojoassert.PojoAssertionBuilder;
                import java.lang.Boolean;
                import java.lang.Integer;
                import java.lang.String;
                import java.util.Collections;
                import javax.annotation.processing.Generated;
                
                @Generated(
                        value = "io.github.marmer.testutils.annotationprocessing.jpojoassert.AssertionGeneratorProcessor",
                        date = "$now")
                public class ExampleTypeAsserter{
                    private final PojoAssertionBuilder<ExampleType> pojoAssertionBuilder;
                
                    private ExampleTypeAsserter(final ExampleType base) {
                        this(new PojoAssertionBuilder<ExampleType>(base, Collections.emptyList(), "ExampleType"));
                    }
                
                    private ExampleTypeAsserter(final PojoAssertionBuilder<ExampleType> pojoAssertionBuilder) {
                        this.pojoAssertionBuilder = pojoAssertionBuilder;
                    }
                
                    public static ExampleTypeAsserter prepareFor(final ExampleType base) {
                        return new ExampleTypeAsserter(base);
                    }
                
                    public ExampleTypeAsserter with(final AssertionCallback<ExampleType> assertionCallback) {
                        return new ExampleTypeAsserter(pojoAssertionBuilder.add(assertionCallback));
                    }
                    
                    public ExampleTypeAsserter withObjectProperty(final AssertionCallback<String> assertionCallback) {
                        return new ExampleTypeAsserter(pojoAssertionBuilder.add(base -> assertionCallback.accept(base.getObjectProperty())));
                    }
                    
                    public ExampleTypeAsserter withPrimitiveProperty(final AssertionCallback<Integer> assertionCallback) {
                        return new ExampleTypeAsserter(pojoAssertionBuilder.add(base -> assertionCallback.accept(base.getPrimitiveProperty())));
                    }
                    
                    public ExampleTypeAsserter withPrimitiveBooleanProperty(final AssertionCallback<Boolean> assertionCallback) {
                        return new ExampleTypeAsserter(pojoAssertionBuilder.add(base -> assertionCallback.accept(base.isPrimitiveBooleanProperty())));
                    }
                    
                    public ExampleTypeAsserter withGetRightBooleanMixProperty(final AssertionCallback<Boolean> assertionCallback) {
                        return new ExampleTypeAsserter(pojoAssertionBuilder.add(base -> assertionCallback.accept(base.isGetRightBooleanMixProperty())));
                    }
                    
                    public ExampleTypeAsserter withIsWrongPrimitiveBooleanProperty(final AssertionCallback<Boolean> assertionCallback) {
                        return new ExampleTypeAsserter(pojoAssertionBuilder.add(base -> assertionCallback.accept(base.getIsWrongPrimitiveBooleanProperty())));
                    }
                    
                    public ExampleTypeAsserter withGetWrongBooleanWrapperProperty(final AssertionCallback<Boolean> assertionCallback) {
                        return new ExampleTypeAsserter(pojoAssertionBuilder.add(base -> assertionCallback.accept(base.isGetWrongBooleanWrapperProperty())));
                    }
                    
                    public ExampleTypeAsserter withIsRightBooleanWrapperProperty(final AssertionCallback<Boolean> assertionCallback) {
                        return new ExampleTypeAsserter(pojoAssertionBuilder.add(base -> assertionCallback.accept(base.getIsRightBooleanWrapperProperty())));
                    }
                    
                    public ExampleTypeAsserter withPrimitiveArrayProperty(final AssertionCallback<int[]> assertionCallback) {
                        return new ExampleTypeAsserter(pojoAssertionBuilder.add(base -> assertionCallback.accept(base.getPrimitiveArrayProperty())));
                    }
                    
                    public ExampleTypeAsserter withObjectArrayProperty(final AssertionCallback<String[]> assertionCallback) {
                        return new ExampleTypeAsserter(pojoAssertionBuilder.add(base -> assertionCallback.accept(base.getObjectArrayProperty())));
                    }
                    
                    public ExampleTypeAsserter withMultidimensionalArrayProperty(final AssertionCallback<String[][]> assertionCallback) {
                        return new ExampleTypeAsserter(pojoAssertionBuilder.add(base -> assertionCallback.accept(base.getMultidimensionalArrayProperty())));
                    }
                    
                    public ExampleTypeAsserter withPackagePrivateProperty(final AssertionCallback<String> assertionCallback) {
                        return new ExampleTypeAsserter(pojoAssertionBuilder.add(base -> assertionCallback.accept(base.getPackagePrivateProperty())));
                    }
                    
                    public ExampleTypeAsserter withProtectedProperty(final AssertionCallback<String> assertionCallback) {
                        return new ExampleTypeAsserter(pojoAssertionBuilder.add(base -> assertionCallback.accept(base.getProtectedProperty())));
                    }
                    
                    public ExampleTypeAsserter withAbstractProperty(final AssertionCallback<String> assertionCallback) {
                        return new ExampleTypeAsserter(pojoAssertionBuilder.add(base -> assertionCallback.accept(base.getAbstractProperty())));
                    }
                    
                    public ExampleTypeAsserter withFinalProperty(final AssertionCallback<String> assertionCallback) {
                        return new ExampleTypeAsserter(pojoAssertionBuilder.add(base -> assertionCallback.accept(base.getFinalProperty())));
                    }
                
                    public void assertToFirstFail() {
                        pojoAssertionBuilder.assertToFirstFail();
                    }
                
                    public void assertAll() {
                        pojoAssertionBuilder.assertAll();
                    }
                }""".trimIndent()
        )

        // Execution
        Truth.assert_()
            .about(JavaSourcesSubjectFactory.javaSources())
            .that(listOf(configurationClass, javaFileObject))
            .processedWith(AssertionGeneratorProcessor { now })
            // Assertion
            .compilesWithoutWarnings()
            .and()
            .generatesSources(expectedOutput)
    }

    @Test
    fun `generation should work for generic types and properties too`() {
        // Preparation
        @Language("JAVA") val configurationClass = JavaFileObjects.forSourceLines(
            "some.pck.JPojoAssertConfiguration", """
                package some.pck;
                
                import io.github.marmer.testutils.annotationprocessing.jpojoassert.GenerateAsserter;
                
                @GenerateAsserter("some.other.pck.ExampleType")
                public class JPojoAssertConfiguration{}
                """.trimIndent()
        )
        @Language("JAVA") val javaFileObject = JavaFileObjects.forSourceLines(
            "some.other.pck.ExampleType", """
                package some.other.pck;
                
                import java.util.List;
import java.util.Map;

                public abstract class ExampleType<T, E> {
                    public Map<String, List<Integer>> getGenericProperty(){return null;}
                    public final T getGenericFromTypeDefinitionProperty(){return null;}
                    public final List<T> getGenericFromTypeDefinitionPropertyAsGeneric(){return null;}
                }
                """.trimIndent()
        )
        val now = LocalDateTime.of(1985, 1, 2, 3, 4, 5, 123000000)
        @Language("JAVA") val expectedOutput = JavaFileObjects.forSourceString(
            "some.other.pck.ExampleTypeAsserter", """
                package some.other.pck;
                
                import io.github.marmer.testutils.annotationprocessing.jpojoassert.AssertionCallback;
                import io.github.marmer.testutils.annotationprocessing.jpojoassert.PojoAssertionBuilder;
                import java.lang.Integer;
                import java.lang.String;
                import java.util.Collections;
                import java.util.List;
                import java.util.Map;
                import javax.annotation.processing.Generated;
                
                @Generated(
                        value = "io.github.marmer.testutils.annotationprocessing.jpojoassert.AssertionGeneratorProcessor",
                        date = "$now")
                public class ExampleTypeAsserter<T, E> {
                    private final PojoAssertionBuilder<ExampleType<T, E>> pojoAssertionBuilder;
                
                    private ExampleTypeAsserter(final ExampleType<T, E> base) {
                        this(new PojoAssertionBuilder<ExampleType<T, E>>(base, Collections.emptyList(), "ExampleType"));
                    }
                
                    private ExampleTypeAsserter(final PojoAssertionBuilder<ExampleType<T, E>> pojoAssertionBuilder) {
                        this.pojoAssertionBuilder = pojoAssertionBuilder;
                    }
                
                    public static <T, E> ExampleTypeAsserter<T, E> prepareFor(final ExampleType<T, E> base) {
                        return new ExampleTypeAsserter<T, E>(base);
                    }
                
                    public ExampleTypeAsserter<T, E> with(final AssertionCallback<ExampleType<T, E>> assertionCallback) {
                        return new ExampleTypeAsserter<T, E>(pojoAssertionBuilder.add(assertionCallback));
                    }
                
                    public ExampleTypeAsserter<T, E> withGenericProperty(final AssertionCallback<Map<String, List<Integer>>> assertionCallback) {
                        return new ExampleTypeAsserter<T, E>(pojoAssertionBuilder.add(base -> assertionCallback.accept(base.getGenericProperty())));
                    }
                
                    public ExampleTypeAsserter<T, E> withGenericFromTypeDefinitionProperty(final AssertionCallback<T> assertionCallback) {
                        return new ExampleTypeAsserter<T, E>(pojoAssertionBuilder.add(base -> assertionCallback.accept(base.getGenericFromTypeDefinitionProperty())));
                    }
                
                    public ExampleTypeAsserter<T, E> withGenericFromTypeDefinitionPropertyAsGeneric(final AssertionCallback<List<T>> assertionCallback) {
                        return new ExampleTypeAsserter<T, E>(pojoAssertionBuilder.add(base -> assertionCallback.accept(base.getGenericFromTypeDefinitionPropertyAsGeneric())));
                    }
                
                    public void assertToFirstFail() {
                        pojoAssertionBuilder.assertToFirstFail();
                    }
                
                    public void assertAll() {
                        pojoAssertionBuilder.assertAll();
                    }
                }
                """.trimIndent()
        )

        // Execution
        Truth.assert_()
            .about(JavaSourcesSubjectFactory.javaSources())
            .that(listOf(configurationClass, javaFileObject))
            .processedWith(AssertionGeneratorProcessor { now })
            // Assertion
            .compilesWithoutWarnings()
//            .compilesWithoutError()
            .and()
            .generatesSources(expectedOutput)
    }

    @Test
    fun `generated files only from different generators without an appropriate warning should raise a warning`() {
        // Preparation
        val configurationClass = JavaFileObjects.forSourceLines(
            "some.pck.SomeGeneratedType", """package some.pck;

import io.github.marmer.testutils.annotationprocessing.jpojoassert.GenerateAsserter;

import javax.annotation.processing.Generated;

@Generated("some.unknown.Processor")
public class SomeGeneratedType{}
"""
        )
        // Preparation

        // Execution
        Truth.assert_()
            .about(JavaSourcesSubjectFactory.javaSources())
            .that(listOf(configurationClass))
            .processedWith(AssertionGeneratorProcessor())
            // Assertion
            .compilesWithoutError()
            .withWarningCount(1)
            .withWarningContaining("No processor claimed any of these annotations: java.compiler/javax.annotation.processing.Generated")
    }
}
