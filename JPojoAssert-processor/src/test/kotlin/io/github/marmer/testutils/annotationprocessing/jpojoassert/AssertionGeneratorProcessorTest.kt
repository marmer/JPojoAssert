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

public class ExampleType{
    // Object Type
    public String getFirst(){return null;}
    // Number primitive
    public int getSecond(){return 0;}
    // boolean primitive
    public boolean isThird(){return false;}
    //identifier mix 1
    public boolean isGetFourth(){return false;}
    //identifier mix 2
    public boolean getIsFifth(){return false;}
    //identifier mix 3
    public Boolean isGetSixth(){return false;}
    //identifier mix 4
    public Boolean getIsSeventh(){return false;}
    // primitive array
    public int[] getEight(){return null;}
    // object array
    public String[] getNinth(){return null;}
    // multidimensional array
    public String[][] getTenth(){return null;}
    // property like method no return
    public void getEleventh(){}
    // property like method with parameters
    public String getEleventh(String prop){return null;}
    // package private properties shold work too
    String getTwelfth(){return null;}
    // protected private properties shold not work
    protected String getThirteenth(){return null;}
    // private private properties shold not work
    private String getFourteenth(){return null;}

    // TODO For Property: Generics
    // TODO For Property: static 
    // TODO For Property: abstract
    // TODO For Property: final
    // TODO For Property: what else edge cases we found already in hamcrest-matcher-generator

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
    
    public ExampleTypeAsserter withFirst(final AssertionCallback<String> assertionCallback) {
        return new ExampleTypeAsserter(pojoAssertionBuilder.add(base -> assertionCallback.accept(base.getFirst())));
    }
    
    public ExampleTypeAsserter withSecond(final AssertionCallback<Integer> assertionCallback) {
        return new ExampleTypeAsserter(pojoAssertionBuilder.add(base -> assertionCallback.accept(base.getSecond())));
    }
    
    public ExampleTypeAsserter withThird(final AssertionCallback<Boolean> assertionCallback) {
        return new ExampleTypeAsserter(pojoAssertionBuilder.add(base -> assertionCallback.accept(base.isThird())));
    }
    
    public ExampleTypeAsserter withGetFourth(final AssertionCallback<Boolean> assertionCallback) {
        return new ExampleTypeAsserter(pojoAssertionBuilder.add(base -> assertionCallback.accept(base.isGetFourth())));
    }
    
    public ExampleTypeAsserter withIsFifth(final AssertionCallback<Boolean> assertionCallback) {
        return new ExampleTypeAsserter(pojoAssertionBuilder.add(base -> assertionCallback.accept(base.getIsFifth())));
    }
    
    public ExampleTypeAsserter withGetSixth(final AssertionCallback<Boolean> assertionCallback) {
        return new ExampleTypeAsserter(pojoAssertionBuilder.add(base -> assertionCallback.accept(base.isGetSixth())));
    }
    
    public ExampleTypeAsserter withIsSeventh(final AssertionCallback<Boolean> assertionCallback) {
        return new ExampleTypeAsserter(pojoAssertionBuilder.add(base -> assertionCallback.accept(base.getIsSeventh())));
    }
    
    public ExampleTypeAsserter withEight(final AssertionCallback<int[]> assertionCallback) {
        return new ExampleTypeAsserter(pojoAssertionBuilder.add(base -> assertionCallback.accept(base.getEight())));
    }
    
    public ExampleTypeAsserter withNinth(final AssertionCallback<String[]> assertionCallback) {
        return new ExampleTypeAsserter(pojoAssertionBuilder.add(base -> assertionCallback.accept(base.getNinth())));
    }
    
    public ExampleTypeAsserter withTenth(final AssertionCallback<String[][]> assertionCallback) {
        return new ExampleTypeAsserter(pojoAssertionBuilder.add(base -> assertionCallback.accept(base.getTenth())));
    }
    
    public ExampleTypeAsserter withTwelfth(final AssertionCallback<String> assertionCallback) {
        return new ExampleTypeAsserter(pojoAssertionBuilder.add(base -> assertionCallback.accept(base.getTwelfth())));
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
            .compilesWithoutError()
            // TODO: marmer 05.11.2020 No warning should be given here!
//            .compilesWithoutWarnings()
            .and()
            .generatesSources(expectedOutput)
    }
}
