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

@GenerateAsserter("some.other.pck.ExampleType")
public class JPojoAssertConfiguration{}
"""
        )
        val javaFileObject = JavaFileObjects.forSourceLines(
            "some.other.pck.ExampleType",
            """package some.other.pck;

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
    public Map<String, List<Integer>> getGenericProperty(){return null;}
    public abstract String getAbstractProperty();
    public final String getFinalProperty(){return null;}
    
    // TODO: marmer 08.11.2020 Type is Interface 
    // TODO: marmer 08.11.2020 Type is Abstract class
    // TODO: marmer 08.11.2020 Type is Enum class
    // TODO: marmer 08.11.2020 Type is at least package private 
    // TODO: marmer 08.11.2020 Type with Generics 
    // TODO: marmer 08.11.2020 Type with wildcard generics 
    // TODO: marmer 08.11.2020 No Generation for self generated types 

}"""
        )
        val now = LocalDateTime.of(1985, 1, 2, 3, 4, 5, 123000000)
        val expectedOutput = JavaFileObjects.forSourceString(
            "some.other.pck.ExampleTypeAsserter", """package some.other.pck;

import io.github.marmer.testutils.annotationprocessing.jpojoassert.AssertionCallback;
import io.github.marmer.testutils.annotationprocessing.jpojoassert.PojoAssertionBuilder;
import java.lang.Boolean;
import java.lang.Integer;
import java.lang.String;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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
    
    public ExampleTypeAsserter withGenericProperty(final AssertionCallback<Map<String, List<Integer>>> assertionCallback) {
        return new ExampleTypeAsserter(pojoAssertionBuilder.add(base -> assertionCallback.accept(base.getGenericProperty())));
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
}"""
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
}
