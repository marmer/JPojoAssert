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
    // Object Type
    String getFirst();
    // Number primitive
    int getSecond();
    // boolean primitive
    boolean isThird();
    //identifier mix 1
    boolean isGetFourth();
    //identifier mix 2
    boolean getIsFifth();
    //identifier mix 3
    String isGetSixth();
    //identifier mix 4
    String getIsSeventh();
    // TODO boolean wrapper
    // TODO primitive array
    // TODO object array
    // TODO multidimensional array
    // TODO case after prefix ( getsomething and issomething should not be a property)
    // TODO For Property: Generics
    // TODO For Property: Type With Generics
    // TODO For Property: Type With nested Generics
    // TODO For Property: Nested Types
    // TODO For Property: static 
    // TODO For Property: (all) kinds of Modifiers (not just private, public, protected, package)
    // TODO For Property: Fields
    // TODO For Property: property like methods with parameters
    // TODO For Property: what else edge cases we found already in hamcrest-matcher-generator
    // TODO For nested Types: Generics
    // TODO For nested Types: Type With Generics
    // TODO For nested Types: Type With nested Generics
    // TODO For nested Types: Nested Types
    // TODO For nested Types: static/non static 
    // TODO For nested Types: (all) kinds of Modifiers (not just private, public, protected, package)
    // TODO For nested Types: Fields
    // TODO For nested Types: property like methods with parameters
    // TODO For nested Types: what else edge cases we found already in hamcrest-matcher-generator
    // TODO Inheritance: Direct inherited Props
    // TODO Inheritance: Indirect inherited Props
    // TODO General: claimed names
    // TODO General: different Methods

}"""
        )
        val now = LocalDateTime.of(1985, 1, 2, 3, 4, 5, 123000000)
        val expectedOutput = JavaFileObjects.forSourceString(
            "some.other.pck.SimplePojoInterfaceAsserter", """package some.other.pck;

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
public class SimplePojoInterfaceAsserter{
    private final PojoAssertionBuilder<SimplePojoInterface> pojoAssertionBuilder;

    private SimplePojoInterfaceAsserter(final SimplePojoInterface base) {
        this(new PojoAssertionBuilder<SimplePojoInterface>(base, Collections.emptyList(), "SimplePojoInterface"));
    }

    private SimplePojoInterfaceAsserter(final PojoAssertionBuilder<SimplePojoInterface> pojoAssertionBuilder) {
        this.pojoAssertionBuilder = pojoAssertionBuilder;
    }

    public static SimplePojoInterfaceAsserter prepareFor(final SimplePojoInterface base) {
        return new SimplePojoInterfaceAsserter(base);
    }

    public SimplePojoInterfaceAsserter with(final AssertionCallback<SimplePojoInterface> assertionCallback) {
        return new SimplePojoInterfaceAsserter(pojoAssertionBuilder.add(assertionCallback));
    }
    
    public SimplePojoInterfaceAsserter withFirst(final AssertionCallback<String> assertionCallback) {
        return new SimplePojoInterfaceAsserter(pojoAssertionBuilder.add(base -> assertionCallback.accept(base.getFirst())));
    }
    
    public SimplePojoInterfaceAsserter withSecond(final AssertionCallback<Integer> assertionCallback) {
        return new SimplePojoInterfaceAsserter(pojoAssertionBuilder.add(base -> assertionCallback.accept(base.getSecond())));
    }
    
    public SimplePojoInterfaceAsserter withThird(final AssertionCallback<Boolean> assertionCallback) {
        return new SimplePojoInterfaceAsserter(pojoAssertionBuilder.add(base -> assertionCallback.accept(base.isThird())));
    }
    
    public SimplePojoInterfaceAsserter withGetFourth(final AssertionCallback<Boolean> assertionCallback) {
        return new SimplePojoInterfaceAsserter(pojoAssertionBuilder.add(base -> assertionCallback.accept(base.isGetFourth())));
    }
    
    public SimplePojoInterfaceAsserter withIsFifth(final AssertionCallback<Boolean> assertionCallback) {
        return new SimplePojoInterfaceAsserter(pojoAssertionBuilder.add(base -> assertionCallback.accept(base.getIsFifth())));
    }
    
    public SimplePojoInterfaceAsserter withGetSixth(final AssertionCallback<String> assertionCallback) {
        return new SimplePojoInterfaceAsserter(pojoAssertionBuilder.add(base -> assertionCallback.accept(base.isGetSixth())));
    }
    
    public SimplePojoInterfaceAsserter withIsSeventh(final AssertionCallback<String> assertionCallback) {
        return new SimplePojoInterfaceAsserter(pojoAssertionBuilder.add(base -> assertionCallback.accept(base.getIsSeventh())));
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

// TODO: marmer 05.11.2020 don't stop generation on errors. Just warn and generate the rest (as far as possible)
// TODO: marmer 02.11.2020 test elements in root package
// TODO: marmer 02.11.2020 test all types used as base for generation (primitives, Objects, Arrays, Void?, Same name, Generics, inner Types)
// TODO: marmer 02.11.2020 test all types for fields at least (primitives, Objects, Arrays, Void?, Same name, Generics, inner Types)
// TODO: marmer 02.11.2020 add Test from experience of Hamcrest Matcher generator
// TODO: marmer 02.11.2020 test Elements in different modules
