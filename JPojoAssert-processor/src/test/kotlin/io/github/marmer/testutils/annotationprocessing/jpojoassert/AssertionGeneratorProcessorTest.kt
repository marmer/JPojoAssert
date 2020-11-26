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
                abstract class JPojoAssertConfiguration{}
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
                }""".trimIndent()
        )

        val now = LocalDateTime.of(1985, 1, 2, 3, 4, 5, 123000000)
        @Language("JAVA") val expectedOutput = JavaFileObjects.forSourceString(
            "some.other.pck.ExampleTypeAsserter", """
                package some.other.pck;
                
                import io.github.marmer.testutils.annotationprocessing.jpojoassert.AssertionCallback;
                import io.github.marmer.testutils.annotationprocessing.jpojoassert.PojoAssertionBuilder;
                import java.lang.Boolean;
                import java.lang.Class;
                import java.lang.Integer;
                import java.lang.String;
                import java.util.Collections;
                import javax.annotation.processing.Generated;
                import org.hamcrest.Matcher;
                import org.hamcrest.MatcherAssert;
                import org.hamcrest.Matchers;
                
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
                    
                    public ExampleTypeAsserter matches(final Matcher<? super ExampleType> matcher) {
                        return new ExampleTypeAsserter(pojoAssertionBuilder.add(base -> MatcherAssert.assertThat(base, matcher)));
                    }
                    """.trimIndent() +
                    propertyMethodsFor("ExampleType", "objectProperty", "String") +
                    propertyMethodsFor("ExampleType", "primitiveProperty", "Integer") +
                    propertyMethodsFor("ExampleType", "primitiveBooleanProperty", "Boolean", "is", "") +
                    propertyMethodsFor("ExampleType", "getRightBooleanMixProperty", "Boolean", "is", "") +
                    propertyMethodsFor("ExampleType", "isWrongPrimitiveBooleanProperty", "Boolean") +
                    propertyMethodsFor("ExampleType", "getWrongBooleanWrapperProperty", "Boolean", "is", "") +
                    propertyMethodsFor("ExampleType", "isRightBooleanWrapperProperty", "Boolean") +
                    propertyMethodsFor("ExampleType", "primitiveArrayProperty", "int[]") +
                    propertyMethodsFor("ExampleType", "objectArrayProperty", "String[]") +
                    propertyMethodsFor("ExampleType", "multidimensionalArrayProperty", "String[][]") +
                    propertyMethodsFor("ExampleType", "packagePrivateProperty", "String") +
                    propertyMethodsFor("ExampleType", "protectedProperty", "String") +
                    propertyMethodsFor("ExampleType", "abstractProperty", "String") +
                    propertyMethodsFor("ExampleType", "finalProperty", "String") +
                    propertyMethodsFor("ExampleType", "class", "Class<?>") +
                    """
                    
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

    private fun propertyMethodsFor(
        baseTypeName: String,
        propertyName: String,
        propertyType: String,
        accessorPrefix: String = "get",
        generics: String = ""
    ) = """
                    public ${baseTypeName}Asserter${generics} with${propertyName.capitalize()}(final AssertionCallback<${propertyType}> assertionCallback) {
                        return new ${baseTypeName}Asserter${generics}(pojoAssertionBuilder.add("${propertyName}", base -> assertionCallback.accept(base.${accessorPrefix}${propertyName.capitalize()}())));
                    }
                    
                    public ${baseTypeName}Asserter${generics} has${propertyName.capitalize()}(final ${propertyType} value) {
                        return has${propertyName.capitalize()}(Matchers.equalTo(value));
                    }
                    
                    public ${baseTypeName}Asserter${generics} has${propertyName.capitalize()}(final Matcher<? super ${propertyType}> matcher) {
                        return new ${baseTypeName}Asserter${generics}(pojoAssertionBuilder.add(base -> MatcherAssert.assertThat(base, Matchers.hasProperty("${propertyName}", matcher))));
                    }
    """.trimIndent()

    @Test
    fun `generation should work for generic types and properties too`() {
        // Preparation
        @Language("JAVA") val configurationClass = JavaFileObjects.forSourceLines(
            "some.pck.JPojoAssertConfiguration", """
                package some.pck;
                
                import io.github.marmer.testutils.annotationprocessing.jpojoassert.GenerateAsserter;
                
                @GenerateAsserter("some.other.pck.ExampleType")
                public interface JPojoAssertConfiguration{}
                """.trimIndent()
        )
        @Language("JAVA") val javaFileObject = JavaFileObjects.forSourceLines(
            "some.other.pck.ExampleType", """
                package some.other.pck;
                
                import java.util.List;
                import java.util.Map;
                import java.util.function.Consumer;
                import java.lang.Runnable;
                
                public interface ExampleType<A extends CharSequence, B extends Consumer<A>, C extends Consumer<String> & Runnable, D> {
                    Map<String, List<Integer>> getGenericProperty();
                    C getGenericFromTypeDefinitionProperty();
                    List<C> getGenericFromTypeDefinitionPropertyAsGeneric();
                }
                """.trimIndent()
        )
        val now = LocalDateTime.of(1985, 1, 2, 3, 4, 5, 123000000)
        @Language("JAVA") val expectedOutput = JavaFileObjects.forSourceString(
            "some.other.pck.ExampleTypeAsserter", """
                package some.other.pck;
                
                import io.github.marmer.testutils.annotationprocessing.jpojoassert.AssertionCallback;
                import io.github.marmer.testutils.annotationprocessing.jpojoassert.PojoAssertionBuilder;
                import java.lang.CharSequence;
                import java.lang.Integer;
                import java.lang.Runnable;
                import java.lang.String;
                import java.util.Collections;
                import java.util.List;
                import java.util.Map;
                import java.util.function.Consumer;
                import javax.annotation.processing.Generated;
                import org.hamcrest.Matcher;
                import org.hamcrest.MatcherAssert;
                import org.hamcrest.Matchers;
                
                @Generated(
                        value = "io.github.marmer.testutils.annotationprocessing.jpojoassert.AssertionGeneratorProcessor",
                        date = "$now")
                public class ExampleTypeAsserter<A extends CharSequence, B extends Consumer<A>, C extends Consumer<String> & Runnable, D> {
                    private final PojoAssertionBuilder<ExampleType<A, B, C, D>> pojoAssertionBuilder;
                
                    private ExampleTypeAsserter(final ExampleType<A, B, C, D> base) {
                        this(new PojoAssertionBuilder<ExampleType<A, B, C, D>>(base, Collections.emptyList(), "ExampleType"));
                    }
                
                    private ExampleTypeAsserter(final PojoAssertionBuilder<ExampleType<A, B, C, D>> pojoAssertionBuilder) {
                        this.pojoAssertionBuilder = pojoAssertionBuilder;
                    }
                
                    public static <A extends CharSequence, B extends Consumer<A>, C extends Consumer<String> & Runnable, D> ExampleTypeAsserter<A, B, C, D> prepareFor(final ExampleType<A, B, C, D> base) {
                        return new ExampleTypeAsserter<A, B, C, D>(base);
                    }
                
                    public ExampleTypeAsserter<A, B, C, D> with(final AssertionCallback<ExampleType<A, B, C, D>> assertionCallback) {
                        return new ExampleTypeAsserter<A, B, C, D>(pojoAssertionBuilder.add(assertionCallback));
                    }
                    
                    public ExampleTypeAsserter<A, B, C, D> matches(final Matcher<? super ExampleType<A, B, C, D>> matcher) {
                        return new ExampleTypeAsserter<A, B, C, D>(pojoAssertionBuilder.add(base -> MatcherAssert.assertThat(base, matcher)));
                    }
                    """ +
                    propertyMethodsFor(
                        "ExampleType",
                        "genericProperty",
                        "Map<String, List<Integer>>",
                        generics = "<A, B, C, D>"
                    ) +
                    propertyMethodsFor(
                        "ExampleType",
                        "genericFromTypeDefinitionProperty",
                        "C",
                        generics = "<A, B, C, D>"
                    ) +
                    propertyMethodsFor(
                        "ExampleType",
                        "genericFromTypeDefinitionPropertyAsGeneric",
                        "List<C>",
                        generics = "<A, B, C, D>"
                    ) +
                    """
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
            .and()
            .generatesSources(expectedOutput)
    }

    @Test
    fun `generation shuold work for enums as well`() {
        // Preparation
        @Language("JAVA") val configurationClass = JavaFileObjects.forSourceLines(
            "some.pck.JPojoAssertConfiguration", """
                package some.pck;
                
                import io.github.marmer.testutils.annotationprocessing.jpojoassert.GenerateAsserter;
                
                @GenerateAsserter("some.other.pck.ExampleType")
                public interface JPojoAssertConfiguration{}
                """.trimIndent()
        )
        @Language("JAVA") val javaFileObject = JavaFileObjects.forSourceLines(
            "some.other.pck.ExampleType", """
                package some.other.pck;
                
                public enum ExampleType {
                    ONE;
                
                    public String getSomeValue() { return null; }
                }
                """.trimIndent()
        )
        val now = LocalDateTime.of(1985, 1, 2, 3, 4, 5, 123000000)
        @Language("JAVA") val expectedOutput = JavaFileObjects.forSourceString(
            "some.other.pck.ExampleTypeAsserter", """
                package some.other.pck;
                
                import io.github.marmer.testutils.annotationprocessing.jpojoassert.AssertionCallback;
                import io.github.marmer.testutils.annotationprocessing.jpojoassert.PojoAssertionBuilder;
                
                import java.lang.String;
                import java.util.Collections;
                import javax.annotation.processing.Generated;
                import org.hamcrest.Matcher;
                import org.hamcrest.MatcherAssert;
                import org.hamcrest.Matchers;
                
                @Generated(
                        value = "io.github.marmer.testutils.annotationprocessing.jpojoassert.AssertionGeneratorProcessor",
                        date = "$now")
                public class ExampleTypeAsserter {
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
                    
                    public ExampleTypeAsserter matches(final Matcher<? super ExampleType> matcher) {
                        return new ExampleTypeAsserter(pojoAssertionBuilder.add(base -> MatcherAssert.assertThat(base, matcher)));
                    }
                    """ +
                    propertyMethodsFor("ExampleType", "someValue", "String") +
                    """
                
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
            .and()
            .generatesSources(expectedOutput)
    }

    @Test
    fun `generation should work for transitive inherited properties`() {
        // Preparation
        @Language("JAVA") val configurationClass = JavaFileObjects.forSourceLines(
            "some.pck.JPojoAssertConfiguration", """
                package some.pck;
                
                import io.github.marmer.testutils.annotationprocessing.jpojoassert.GenerateAsserter;
                
                @GenerateAsserter("some.other.pck.ChildType")
                public interface JPojoAssertConfiguration{}
                """.trimIndent()
        )
        @Language("JAVA") val childType = JavaFileObjects.forSourceLines(
            "some.other.pck.ChildType", """
                package some.other.pck;
                
                public abstract class ChildType extends DirectParentClass implements DirectParentInterface{
                    public String getChildOnlyProperty(){ return null; }
                    public String getChildAndParentPropertyWithDifferentReturnTypes(){ return null; }
                }
                """.trimIndent()
        )
        @Language("JAVA") val directParentInterface = JavaFileObjects.forSourceLines(
            "some.other.pck.DirectParentInterface", """
                package some.other.pck;
                
                public interface DirectParentInterface extends IndirectParentInterface{
                    CharSequence getChildAndParentPropertyWithDifferentReturnTypes();
                    String getDirectInterfaceParentProperty();
                }
                """.trimIndent()
        )
        @Language("JAVA") val indirectParentInterface = JavaFileObjects.forSourceLines(
            "some.other.pck.IndirectParentInterface", """
                package some.other.pck;
                
                public interface IndirectParentInterface {
                    String getIndirectInterfaceParentProperty();
                }
                """.trimIndent()
        )
        @Language("JAVA") val directParentClass = JavaFileObjects.forSourceLines(
            "some.other.pck.DirectParentClass", """
                package some.other.pck;
                
                public class DirectParentClass {
                    protected String getDirectParentClassProperty(){ return null; }
                }
                """.trimIndent()
        )
        val now = LocalDateTime.of(1985, 1, 2, 3, 4, 5, 123000000)
        @Language("JAVA") val expectedOutput = JavaFileObjects.forSourceString(
            "some.other.pck.ChildTypeAsserter", """
                package some.other.pck;
                
                import io.github.marmer.testutils.annotationprocessing.jpojoassert.AssertionCallback;
                import io.github.marmer.testutils.annotationprocessing.jpojoassert.PojoAssertionBuilder;
                import java.lang.Class;                
                import java.lang.String;
                import java.util.Collections;
                import javax.annotation.processing.Generated;
                import org.hamcrest.Matcher;
                import org.hamcrest.MatcherAssert;
                import org.hamcrest.Matchers;
                
                @Generated(
                        value = "io.github.marmer.testutils.annotationprocessing.jpojoassert.AssertionGeneratorProcessor",
                        date = "$now")
                public class ChildTypeAsserter {
                    private final PojoAssertionBuilder<ChildType> pojoAssertionBuilder;
                
                    private ChildTypeAsserter(final ChildType base) {
                        this(new PojoAssertionBuilder<ChildType>(base, Collections.emptyList(), "ChildType"));
                    }
                
                    private ChildTypeAsserter(final PojoAssertionBuilder<ChildType> pojoAssertionBuilder) {
                        this.pojoAssertionBuilder = pojoAssertionBuilder;
                    }
                
                    public static ChildTypeAsserter prepareFor(final ChildType base) {
                        return new ChildTypeAsserter(base);
                    }
                
                    public ChildTypeAsserter with(final AssertionCallback<ChildType> assertionCallback) {
                        return new ChildTypeAsserter(pojoAssertionBuilder.add(assertionCallback));
                    }
                    
                    public ChildTypeAsserter matches(final Matcher<? super ChildType> matcher) {
                        return new ChildTypeAsserter(pojoAssertionBuilder.add(base -> MatcherAssert.assertThat(base, matcher)));
                    }
                    """ +
                    propertyMethodsFor("ChildType", "childOnlyProperty", "String") +
                    propertyMethodsFor(
                        "ChildType",
                        "childAndParentPropertyWithDifferentReturnTypes",
                        "String",
                        generics = ""
                    ) +
                    propertyMethodsFor("ChildType", "directParentClassProperty", "String") +
                    propertyMethodsFor("ChildType", "class", "Class<?>") +
                    propertyMethodsFor("ChildType", "directInterfaceParentProperty", "String") +
                    propertyMethodsFor("ChildType", "indirectInterfaceParentProperty", "String") +
                    """
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
            .that(
                listOf(
                    configurationClass,
                    childType,
                    directParentInterface,
                    indirectParentInterface,
                    directParentClass
                )
            )
            .processedWith(AssertionGeneratorProcessor { now })
            // Assertion
            .compilesWithoutWarnings()
            .and()
            .generatesSources(expectedOutput)
    }

    @Test
    fun `nothing should be generated for self generated types`() {
        // Preparation
        @Language("JAVA") val configurationClass = JavaFileObjects.forSourceLines(
            "some.pck.JPojoAssertConfiguration", """
                package some.pck;
                
                import io.github.marmer.testutils.annotationprocessing.jpojoassert.GenerateAsserter;
                
                @GenerateAsserter("some.other.pck.ExampleType")
                public interface JPojoAssertConfiguration{}
                """.trimIndent()
        )
        @Language("JAVA") val javaFileObject = JavaFileObjects.forSourceLines(
            "some.other.pck.ExampleType", """
                package some.other.pck;
                
                import javax.annotation.processing.Generated;
                
                @Generated(
                    value = "io.github.marmer.testutils.annotationprocessing.jpojoassert.AssertionGeneratorProcessor",
                    date = "2020-11-11T12:36:11.153728900")
                public class ExampleType {}
                """.trimIndent()
        )

        // Execution
        Truth.assert_()
            .about(JavaSourcesSubjectFactory.javaSources())
            .that(listOf(configurationClass, javaFileObject))
            .processedWith(AssertionGeneratorProcessor())
            // Assertion
            .compilesWithoutWarnings()
            .withNoteContaining("Generation skipped for: 'some.other.pck.ExampleType' because is is already generated by this processor")
            .`in`(javaFileObject)
            .onLine(6)
            .atColumn(13)
    }

    @Test
    fun `if the configured type to generate does neither exist as type nor as package a warning should be given`() {
        // Preparation
        @Language("JAVA") val configurationClass = JavaFileObjects.forSourceLines(
            "some.pck.JPojoAssertConfiguration", """
            package some.pck;
                            
            import io.github.marmer.testutils.annotationprocessing.jpojoassert.GenerateAsserter;
            
            @GenerateAsserter("not.existing.TypeName")
            public interface JPojoAssertConfiguration {}
                """.trimIndent()
        )
        // Execution
        Truth.assert_()
            .about(JavaSourcesSubjectFactory.javaSources())
            .that(listOf(configurationClass))
            .processedWith(AssertionGeneratorProcessor())
            // Assertion
            .compilesWithoutError()
            .withWarningContaining("Neither a type nor a type exists for 'not.existing.TypeName'")
            .`in`(configurationClass)
            .onLine(5)
            .atColumn(19)
    }

    @Test
    fun `not self generated files without a configuration should raise a warning because it's the default behavior`() {
        // Preparation
        @Language("JAVA") val fromOthersGeneratedType = JavaFileObjects.forSourceLines(
            "some.pck.SomeGeneratedType", """
            package some.pck;
            
            import io.github.marmer.testutils.annotationprocessing.jpojoassert.GenerateAsserter;
            
            import javax.annotation.processing.Generated;
            
            @Generated("some.unknown.Processor")
            public class SomeGeneratedType{}
            """.trimIndent()
        )
        // Preparation

        // Execution
        Truth.assert_()
            .about(JavaSourcesSubjectFactory.javaSources())
            .that(listOf(fromOthersGeneratedType))
            .processedWith(AssertionGeneratorProcessor())
            // Assertion
            .compilesWithoutError()
            .withWarningCount(1)
            .withWarningContaining("No processor claimed any of these annotations: java.compiler/javax.annotation.processing.Generated")
    }

    @Test
    fun `self generated files without a configuration don't need to raise a warning`() {
        // Preparation
        @Language("JAVA") val fromOthersGeneratedType = JavaFileObjects.forSourceLines(
            "some.pck.SomeGeneratedType", """
            package some.pck;
            
            import io.github.marmer.testutils.annotationprocessing.jpojoassert.GenerateAsserter;
            
            import javax.annotation.processing.Generated;
            
            @Generated("io.github.marmer.testutils.annotationprocessing.jpojoassert.AssertionGeneratorProcessor")
            public class SomeGeneratedType{}
            """.trimIndent()
        )
        // Preparation

        // Execution
        Truth.assert_()
            .about(JavaSourcesSubjectFactory.javaSources())
            .that(listOf(fromOthersGeneratedType))
            .processedWith(AssertionGeneratorProcessor())
            // Assertion
            .compilesWithoutWarnings()
    }

    @Test
    fun `generation should work for generated types from different generators`() {
        // Preparation
        @Language("JAVA") val configurationClass = JavaFileObjects.forSourceLines(
            "some.pck.JPojoAssertConfiguration", """
            package some.pck;
                            
            import io.github.marmer.testutils.annotationprocessing.jpojoassert.GenerateAsserter;
            
            @GenerateAsserter("some.other.pck.SomeGeneratedType")
            public interface JPojoAssertConfiguration {}
                """.trimIndent()
        )
        @Language("JAVA") val fromOthersGeneratedType = JavaFileObjects.forSourceLines(
            "some.other.pck.SomeGeneratedType", """
            package some.other.pck;
            
            import io.github.marmer.testutils.annotationprocessing.jpojoassert.GenerateAsserter;
            
            import javax.annotation.processing.Generated;
            
            @Generated("some.unknown.Processor")
            public interface SomeGeneratedType{}
            """.trimIndent()
        )

        val now = LocalDateTime.of(1985, 1, 2, 3, 4, 5, 123000000)
        @Language("JAVA") val output = JavaFileObjects.forSourceString(
            "some.other.pck.SomeGeneratedTypeAsserter",
            getEmptyAsserterStubForInterface(now, "SomeGeneratedType").trimIndent()
        )

        // Preparation

        // Execution
        Truth.assert_()
            .about(JavaSourcesSubjectFactory.javaSources())
            .that(listOf(configurationClass, fromOthersGeneratedType))
            .processedWith(AssertionGeneratorProcessor { now })
            // Assertion
            .compilesWithoutWarnings()
            .and()
            .generatesSources(output)
    }

    @Test
    fun `for each full qualified type should an asserter be generated`() {
        // Preparation
        @Language("JAVA") val configurationClass = JavaFileObjects.forSourceLines(
            "some.pck.JPojoAssertConfiguration", """
                package some.pck;
                
                import io.github.marmer.testutils.annotationprocessing.jpojoassert.GenerateAsserter;
                
                @GenerateAsserter({"some.other.pck.FirstType","some.other.pck.SecondType"})
                public interface JPojoAssertConfiguration{}
                """.trimIndent()
        )
        @Language("JAVA") val firstType = JavaFileObjects.forSourceLines(
            "some.other.pck.FirstType", """
                package some.other.pck;
                
                public interface FirstType {}
                """.trimIndent()
        )
        @Language("JAVA") val secondType = JavaFileObjects.forSourceLines(
            "some.other.pck.SecondType", """
                package some.other.pck;
                
                public interface SecondType {}
                """.trimIndent()
        )
        val now = LocalDateTime.of(1985, 1, 2, 3, 4, 5, 123000000)
        @Language("JAVA") val firstTypeOutput = JavaFileObjects.forSourceString(
            "some.other.pck.FirstTypeAsserter", getEmptyAsserterStubForInterface(now, "FirstType").trimIndent()
        )

        @Language("JAVA") val secondTypeOutput = JavaFileObjects.forSourceString(
            "some.other.pck.SecondTypeAsserter", getEmptyAsserterStubForInterface(now, "SecondType")
        )


        // Execution
        Truth.assert_()
            .about(JavaSourcesSubjectFactory.javaSources())
            .that(listOf(configurationClass, firstType, secondType))
            .processedWith(AssertionGeneratorProcessor { now })
            // Assertion
            .compilesWithoutWarnings()
            .and()
            .generatesSources(firstTypeOutput, secondTypeOutput)
    }

    @Test
    fun `it should be possible to generate for Types configured more than once`() {
        // Preparation
        @Language("JAVA") val configurationClass = JavaFileObjects.forSourceLines(
            "some.pck.JPojoAssertConfiguration", """
                package some.pck;
                
                import io.github.marmer.testutils.annotationprocessing.jpojoassert.GenerateAsserter;
                
                @GenerateAsserter({
                        "some.other.pck.SomeType",
                        "some.other.pck",
                        "some.other.pck.SomeType",
                        "some.other.pck"
                })
                public interface JPojoAssertConfiguration{}
                """.trimIndent()
        )
        @Language("JAVA") val someType = JavaFileObjects.forSourceLines(
            "some.other.pck.SomeType", """
                package some.other.pck;
                
                public interface SomeType {}
                """.trimIndent()
        )
        val now = LocalDateTime.of(1985, 1, 2, 3, 4, 5, 123000000)
        @Language("JAVA") val someTypeOutput = JavaFileObjects.forSourceString(
            "some.other.pck.SomeTypeAsserter", getEmptyAsserterStubForInterface(now, "SomeType").trimIndent()
        )

        // Execution
        Truth.assert_()
            .about(JavaSourcesSubjectFactory.javaSources())
            .that(listOf(configurationClass, someType))
            .processedWith(AssertionGeneratorProcessor { now })
            // Assertion
            .compilesWithoutWarnings()
            .and()
            .generatesSources(someTypeOutput)
    }

    @Test
    fun `for each type within a given package an asserter should be generated`() {
        // Preparation
        @Language("JAVA") val configurationClass = JavaFileObjects.forSourceLines(
            "some.pck.JPojoAssertConfiguration", """
                package some.pck;
                
                import io.github.marmer.testutils.annotationprocessing.jpojoassert.GenerateAsserter;
                
                @GenerateAsserter({"some.other.pck"})
                public interface JPojoAssertConfiguration{}
                """.trimIndent()
        )
        @Language("JAVA") val firstType = JavaFileObjects.forSourceLines(
            "some.other.pck.FirstType", """
                package some.other.pck;
                
                public interface FirstType {}
                """.trimIndent()
        )
        @Language("JAVA") val secondType = JavaFileObjects.forSourceLines(
            "some.other.pck.SecondType", """
                package some.other.pck;
                
                public interface SecondType {}
                """.trimIndent()
        )
        val now = LocalDateTime.of(1985, 1, 2, 3, 4, 5, 123000000)
        @Language("JAVA") val firstTypeOutput = JavaFileObjects.forSourceString(
            "some.other.pck.FirstTypeAsserter", getEmptyAsserterStubForInterface(now, "FirstType").trimIndent()
        )

        @Language("JAVA") val secondTypeOutput = JavaFileObjects.forSourceString(
            "some.other.pck.SecondTypeAsserter", getEmptyAsserterStubForInterface(now, "SecondType")
        )


        // Execution
        Truth.assert_()
            .about(JavaSourcesSubjectFactory.javaSources())
            .that(listOf(configurationClass, firstType, secondType))
            .processedWith(AssertionGeneratorProcessor { now })
            // Assertion
            .compilesWithoutWarnings()
            .and()
            .generatesSources(firstTypeOutput, secondTypeOutput)
    }


    // TODO: marmer 26.11.2020 generation for "container" class when the configuration points to full qualified nested type
    // TODO: marmer 26.11.2020 care about the different modifiert (private package private public, static, non static)
    // TODO: marmer 26.11.2020 care about the different Types (interfaces, classes)
    // TODO: marmer 26.11.2020 a little more qualified headings?
    // TODO: marmer 26.11.2020 Generics in nested types
    // TODO: marmer 26.11.2020 Generics in "container" types

    @Test
    fun `generation should work for nested types`() {
        // Preparation
        @Language("JAVA") val configurationClass = JavaFileObjects.forSourceLines(
            "some.pck.JPojoAssertConfiguration", """
                package some.pck;
                
                import io.github.marmer.testutils.annotationprocessing.jpojoassert.GenerateAsserter;
                
                @GenerateAsserter({"some.other.pck"})
                public interface JPojoAssertConfiguration{}
                """.trimIndent()
        )
        @Language("JAVA") val containerType = JavaFileObjects.forSourceLines(
            "some.other.pck.ContainerType", """
                package some.other.pck;
                
                public interface ContainerType {
                     interface DirectInnerType{
                         interface TransitiveInnerType{}
                     }
                }
                """.trimIndent()
        )
        val now = LocalDateTime.of(1985, 1, 2, 3, 4, 5, 123000000)
        @Language("JAVA") val output = JavaFileObjects.forSourceString(
            "some.other.pck.ContainerTypeAsserter", """
                        package some.other.pck;
                        
                        import io.github.marmer.testutils.annotationprocessing.jpojoassert.AssertionCallback;
                        import io.github.marmer.testutils.annotationprocessing.jpojoassert.PojoAssertionBuilder;
                        import java.util.Collections;
                        import javax.annotation.processing.Generated;
                        import org.hamcrest.Matcher;
                        import org.hamcrest.MatcherAssert;
                        
                        @Generated(
                                value = "io.github.marmer.testutils.annotationprocessing.jpojoassert.AssertionGeneratorProcessor",
                                date = "$now")
                        public class ContainerTypeAsserter {
                            private final PojoAssertionBuilder<ContainerType> pojoAssertionBuilder;
                        
                            private ContainerTypeAsserter(final ContainerType base) {
                                this(new PojoAssertionBuilder<ContainerType>(base, Collections.emptyList(), "ContainerType"));
                            }
                        
                            private ContainerTypeAsserter(final PojoAssertionBuilder<ContainerType> pojoAssertionBuilder) {
                                this.pojoAssertionBuilder = pojoAssertionBuilder;
                            }
                        
                            public static ContainerTypeAsserter prepareFor(final ContainerType base) {
                                return new ContainerTypeAsserter(base);
                            }
                        
                            public ContainerTypeAsserter with(final AssertionCallback<ContainerType> assertionCallback) {
                                return new ContainerTypeAsserter(pojoAssertionBuilder.add(assertionCallback));
                            }
                        
                            public ContainerTypeAsserter matches(final Matcher<? super ContainerType> matcher) {
                                return new ContainerTypeAsserter(pojoAssertionBuilder.add(base -> MatcherAssert.assertThat(base, matcher)));
                            }
                            
                            public void assertToFirstFail() {
                                pojoAssertionBuilder.assertToFirstFail();
                            }
                        
                            public void assertAll() {
                                pojoAssertionBuilder.assertAll();
                            }

                            @Generated(
                                    value = "io.github.marmer.testutils.annotationprocessing.jpojoassert.AssertionGeneratorProcessor",
                                    date = "$now")
                            public static class DirectInnerTypeAsserter {
                                private final PojoAssertionBuilder<ContainerType.DirectInnerType> pojoAssertionBuilder;

                                private DirectInnerTypeAsserter(final ContainerType.DirectInnerType base) {
                                    this(new PojoAssertionBuilder<ContainerType.DirectInnerType>(base, Collections.emptyList(), "DirectInnerType"));
                                }

                                private DirectInnerTypeAsserter(final PojoAssertionBuilder<ContainerType.DirectInnerType> pojoAssertionBuilder) {
                                    this.pojoAssertionBuilder = pojoAssertionBuilder;
                                }

                                public static DirectInnerTypeAsserter prepareFor(final ContainerType.DirectInnerType base) {
                                    return new DirectInnerTypeAsserter(base);
                                }

                                public DirectInnerTypeAsserter with(final AssertionCallback<ContainerType.DirectInnerType> assertionCallback) {
                                    return new DirectInnerTypeAsserter(pojoAssertionBuilder.add(assertionCallback));
                                }

                                public DirectInnerTypeAsserter matches(final Matcher<? super ContainerType.DirectInnerType> matcher) {
                                    return new DirectInnerTypeAsserter(pojoAssertionBuilder.add(base -> MatcherAssert.assertThat(base, matcher)));
                                }

                                public void assertToFirstFail() {
                                    pojoAssertionBuilder.assertToFirstFail();
                                }

                                public void assertAll() {
                                    pojoAssertionBuilder.assertAll();
                                }

                                @Generated(
                                        value = "io.github.marmer.testutils.annotationprocessing.jpojoassert.AssertionGeneratorProcessor",
                                        date = "$now")
                                public static class TransitiveInnerTypeAsserter {
                                    private final PojoAssertionBuilder<ContainerType.DirectInnerType.TransitiveInnerType> pojoAssertionBuilder;

                                    private TransitiveInnerTypeAsserter(final ContainerType.DirectInnerType.TransitiveInnerType base) {
                                        this(new PojoAssertionBuilder<ContainerType.DirectInnerType.TransitiveInnerType>(base, Collections.emptyList(), "TransitiveInnerType"));
                                    }

                                    private TransitiveInnerTypeAsserter(final PojoAssertionBuilder<ContainerType.DirectInnerType.TransitiveInnerType> pojoAssertionBuilder) {
                                        this.pojoAssertionBuilder = pojoAssertionBuilder;
                                    }

                                    public static TransitiveInnerTypeAsserter prepareFor(final ContainerType.DirectInnerType.TransitiveInnerType base) {
                                        return new TransitiveInnerTypeAsserter(base);
                                    }

                                    public TransitiveInnerTypeAsserter with(final AssertionCallback<ContainerType.DirectInnerType.TransitiveInnerType> assertionCallback) {
                                        return new TransitiveInnerTypeAsserter(pojoAssertionBuilder.add(assertionCallback));
                                    }

                                    public TransitiveInnerTypeAsserter matches(final Matcher<? super ContainerType.DirectInnerType.TransitiveInnerType> matcher) {
                                        return new TransitiveInnerTypeAsserter(pojoAssertionBuilder.add(base -> MatcherAssert.assertThat(base, matcher)));
                                    }

                                    public void assertToFirstFail() {
                                        pojoAssertionBuilder.assertToFirstFail();
                                    }

                                    public void assertAll() {
                                        pojoAssertionBuilder.assertAll();
                                    }
                                }
                            }
                        }
                        """.trimIndent().trimIndent()
        )

        // Execution
        Truth.assert_()
            .about(JavaSourcesSubjectFactory.javaSources())
            .that(listOf(configurationClass, containerType))
            .processedWith(AssertionGeneratorProcessor { now })
            // Assertion
            .compilesWithoutWarnings()
            .and()
            .generatesSources(output)
    }

    private fun getEmptyAsserterStubForInterface(now: LocalDateTime, typeName: String): String {
        return """
                    package some.other.pck;
                    
                    import io.github.marmer.testutils.annotationprocessing.jpojoassert.AssertionCallback;
                    import io.github.marmer.testutils.annotationprocessing.jpojoassert.PojoAssertionBuilder;
                    import java.util.Collections;
                    import javax.annotation.processing.Generated;
                    import org.hamcrest.Matcher;
                    import org.hamcrest.MatcherAssert;
                    
                    @Generated(
                            value = "io.github.marmer.testutils.annotationprocessing.jpojoassert.AssertionGeneratorProcessor",
                            date = "$now")
                    public class ${typeName}Asserter {
                        private final PojoAssertionBuilder<${typeName}> pojoAssertionBuilder;
                    
                        private ${typeName}Asserter(final ${typeName} base) {
                            this(new PojoAssertionBuilder<${typeName}>(base, Collections.emptyList(), "${typeName}"));
                        }
                    
                        private ${typeName}Asserter(final PojoAssertionBuilder<${typeName}> pojoAssertionBuilder) {
                            this.pojoAssertionBuilder = pojoAssertionBuilder;
                        }
                    
                        public static ${typeName}Asserter prepareFor(final ${typeName} base) {
                            return new ${typeName}Asserter(base);
                        }
                    
                        public ${typeName}Asserter with(final AssertionCallback<${typeName}> assertionCallback) {
                            return new ${typeName}Asserter(pojoAssertionBuilder.add(assertionCallback));
                        }
                    
                        public ${typeName}Asserter matches(final Matcher<? super ${typeName}> matcher) {
                            return new ${typeName}Asserter(pojoAssertionBuilder.add(base -> MatcherAssert.assertThat(base, matcher)));
                        }
                        
                        public void assertToFirstFail() {
                            pojoAssertionBuilder.assertToFirstFail();
                        }
                    
                        public void assertAll() {
                            pojoAssertionBuilder.assertAll();
                        }
                    }
                    """.trimIndent()
    }
}
