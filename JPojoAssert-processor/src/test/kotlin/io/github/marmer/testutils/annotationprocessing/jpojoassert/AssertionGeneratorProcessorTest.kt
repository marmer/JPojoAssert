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
                import io.github.marmer.testutils.annotationprocessing.jpojoassert.PojoAsserter;
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
                public class ExampleTypeAsserter implements PojoAsserter<ExampleType>{
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

    @Test
    fun `when pojos have properties of other types an asserter exists for, a convenience method with an asserter callback should be provided`() {
        // Preparation
        @Language("JAVA") val configurationClass = JavaFileObjects.forSourceLines(
            "some.pck.JPojoAssertConfiguration", """
                package some.pck;
                
                import io.github.marmer.testutils.annotationprocessing.jpojoassert.GenerateAsserter;
                
                @GenerateAsserter({"some.other.pck.ReferencingType","some.other.pck.ReferencedType"})
                abstract class JPojoAssertConfiguration{}
                """.trimIndent()
        )

        @Language("JAVA") val referencingType = JavaFileObjects.forSourceLines(
            "some.other.pck.ReferencingType", """
                package some.other.pck;
                
                public interface ReferencingType{
                    ReferencedType<String> getSomeProp();
                    InnerType getNestedTypeProp();
                    interface InnerType{}
                }""".trimIndent()
        )

        @Language("JAVA") val referencedType = JavaFileObjects.forSourceLines(
            "some.other.pck.ReferencedType", """
                package some.other.pck;
                
                public interface ReferencedType<T>{
                }""".trimIndent()
        )

        val now = LocalDateTime.of(1985, 1, 2, 3, 4, 5, 123000000)
        @Language("JAVA") val expectedOutput = JavaFileObjects.forSourceString(
            "some.other.pck.ReferencingTypeAsserter", """
                package some.other.pck;

                import io.github.marmer.testutils.annotationprocessing.jpojoassert.AssertionCallback;
                import io.github.marmer.testutils.annotationprocessing.jpojoassert.PojoAsserter;
                import io.github.marmer.testutils.annotationprocessing.jpojoassert.PojoAssertionBuilder;
                import java.lang.String;
                import java.util.Collections;
                import java.util.function.Function;
                import javax.annotation.processing.Generated;
                import org.hamcrest.Matcher;
                import org.hamcrest.MatcherAssert;
                import org.hamcrest.Matchers;
                
                @Generated(
                        value = "io.github.marmer.testutils.annotationprocessing.jpojoassert.AssertionGeneratorProcessor",
                        date = "$now")
                public class ReferencingTypeAsserter implements PojoAsserter<ReferencingType>{
                    private final PojoAssertionBuilder<ReferencingType> pojoAssertionBuilder;
                
                    private ReferencingTypeAsserter(final ReferencingType base) {
                        this(new PojoAssertionBuilder<ReferencingType>(base, Collections.emptyList(), "ReferencingType"));
                    }
                
                    private ReferencingTypeAsserter(final PojoAssertionBuilder<ReferencingType> pojoAssertionBuilder) {
                        this.pojoAssertionBuilder = pojoAssertionBuilder;
                    }
                
                    public static ReferencingTypeAsserter prepareFor(final ReferencingType base) {
                        return new ReferencingTypeAsserter(base);
                    }
                
                    public ReferencingTypeAsserter with(final AssertionCallback<ReferencingType> assertionCallback) {
                        return new ReferencingTypeAsserter(pojoAssertionBuilder.add(assertionCallback));
                    }
                    
                    public ReferencingTypeAsserter matches(final Matcher<? super ReferencingType> matcher) {
                        return new ReferencingTypeAsserter(pojoAssertionBuilder.add(base -> MatcherAssert.assertThat(base, matcher)));
                    }
                    
                    public ReferencingTypeAsserter withSomeProp(final AssertionCallback<ReferencedType<String>> assertionCallback) {
                        return new ReferencingTypeAsserter(pojoAssertionBuilder.add("someProp", base -> assertionCallback.assertFor(base.getSomeProp())));
                    }
                    
                    public ReferencingTypeAsserter hasSomeProp(final ReferencedType<String> value) {
                        return hasSomeProp(Matchers.equalTo(value));
                    }
                    
                    public ReferencingTypeAsserter hasSomeProp(final Matcher<? super ReferencedType<String>> matcher) {
                        return new ReferencingTypeAsserter(pojoAssertionBuilder.add(base -> MatcherAssert.assertThat(base, Matchers.hasProperty("someProp", matcher))));
                    }
                    
                    public ReferencingTypeAsserter hasSomeProp(final Function<ReferencedTypeAsserter<String>, PojoAsserter<ReferencedType<String>>> asserterFunction) {
                        return new ReferencingTypeAsserter(pojoAssertionBuilder.addAsserter("someProp", base -> asserterFunction.apply(ReferencedTypeAsserter.prepareFor(base.getSomeProp()))));
                    }
                    
                    public ReferencingTypeAsserter withNestedTypeProp(final AssertionCallback<ReferencingType.InnerType> assertionCallback) {
                        return new ReferencingTypeAsserter(pojoAssertionBuilder.add("nestedTypeProp", base -> assertionCallback.assertFor(base.getNestedTypeProp())));
                    }
                    
                    public ReferencingTypeAsserter hasNestedTypeProp(final ReferencingType.InnerType value) {
                        return hasNestedTypeProp(Matchers.equalTo(value));
                    }
                    
                    public ReferencingTypeAsserter hasNestedTypeProp(final Matcher<? super ReferencingType.InnerType> matcher) {
                        return new ReferencingTypeAsserter(pojoAssertionBuilder.add(base -> MatcherAssert.assertThat(base, Matchers.hasProperty("nestedTypeProp", matcher))));
                    }

                    public ReferencingTypeAsserter hasNestedTypeProp(
                            final Function<InnerTypeAsserter, PojoAsserter<ReferencingType.InnerType>> asserterFunction) {
                        return new ReferencingTypeAsserter(pojoAssertionBuilder.addAsserter("nestedTypeProp", base -> asserterFunction.apply(InnerTypeAsserter.prepareFor(base.getNestedTypeProp()))));
                    }

                    public void assertToFirstFail() {
                        pojoAssertionBuilder.assertToFirstFail();
                    }

                    public void assertAll() {
                        pojoAssertionBuilder.assertAll();
                    }

                    @Generated(
                            value = "io.github.marmer.testutils.annotationprocessing.jpojoassert.AssertionGeneratorProcessor",
                            date = "${now}"
                    )
                    public static class InnerTypeAsserter implements PojoAsserter<ReferencingType.InnerType> {
                        private final PojoAssertionBuilder<ReferencingType.InnerType> pojoAssertionBuilder;

                        private InnerTypeAsserter(final ReferencingType.InnerType base) {
                            this(new PojoAssertionBuilder<ReferencingType.InnerType> (base, Collections.emptyList(), "InnerType"));
                        }

                        private InnerTypeAsserter(
                                final PojoAssertionBuilder<ReferencingType.InnerType> pojoAssertionBuilder) {
                            this.pojoAssertionBuilder = pojoAssertionBuilder;
                        }

                        public static InnerTypeAsserter prepareFor(final ReferencingType.InnerType base) {
                            return new InnerTypeAsserter(base);
                        }

                        public InnerTypeAsserter with(
                                final AssertionCallback<ReferencingType.InnerType> assertionCallback) {
                            return new InnerTypeAsserter(pojoAssertionBuilder.add(assertionCallback));
                        }

                        public InnerTypeAsserter matches(final Matcher<? super ReferencingType.InnerType> matcher) {
                            return new InnerTypeAsserter(pojoAssertionBuilder.add(base -> MatcherAssert.assertThat(base, matcher)));
                        }

                        public void assertToFirstFail() {
                            pojoAssertionBuilder.assertToFirstFail();
                        }

                        public void assertAll() {
                            pojoAssertionBuilder.assertAll();
                        }
                    }
                }
""".trimIndent()
        )

        // Execution
        Truth.assert_()
            .about(JavaSourcesSubjectFactory.javaSources())
            .that(listOf(configurationClass, referencingType, referencedType))
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
                        return new ${baseTypeName}Asserter${generics}(pojoAssertionBuilder.add("${propertyName}", base -> assertionCallback.assertFor(base.${accessorPrefix}${propertyName.capitalize()}())));
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
                import io.github.marmer.testutils.annotationprocessing.jpojoassert.PojoAsserter;
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
                public class ExampleTypeAsserter<A extends CharSequence, B extends Consumer<A>, C extends Consumer<String> & Runnable, D> implements PojoAsserter<ExampleType<A, B, C, D>>{
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
                import io.github.marmer.testutils.annotationprocessing.jpojoassert.PojoAsserter;
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
                public class ExampleTypeAsserter implements PojoAsserter<ExampleType> {
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
                import io.github.marmer.testutils.annotationprocessing.jpojoassert.PojoAsserter;
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
                public class ChildTypeAsserter implements PojoAsserter<ChildType>{
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


    @Test
    fun `Configured full qualified names for inner classes should produce the parent matchers as well to avoid naming conflicts`() { // Preparation
        @Language("JAVA") val configurationClass = JavaFileObjects.forSourceLines(
            "some.pck.JPojoAssertConfiguration", """
                package some.pck;
                
                import io.github.marmer.testutils.annotationprocessing.jpojoassert.GenerateAsserter;
                
                @GenerateAsserter({"some.other.pck.ContainerType.DirectInnerType"})
                public interface JPojoAssertConfiguration{}
                """.trimIndent()
        )
        @Language("JAVA") val containerType = JavaFileObjects.forSourceLines(
            "some.other.pck.ContainerType", """
                package some.other.pck;
                
                public interface ContainerType{
                     interface DirectInnerType{
                     }
                }
                """.trimIndent()
        )
        val now = LocalDateTime.of(1985, 1, 2, 3, 4, 5, 123000000)
        @Language("JAVA") val output = JavaFileObjects.forSourceString(
            "some.other.pck.ContainerTypeAsserter", """
                        package some.other.pck;
                        
                        import io.github.marmer.testutils.annotationprocessing.jpojoassert.AssertionCallback;
                        import io.github.marmer.testutils.annotationprocessing.jpojoassert.PojoAsserter;
                        import io.github.marmer.testutils.annotationprocessing.jpojoassert.PojoAssertionBuilder;
                        import java.util.Collections;
                        import javax.annotation.processing.Generated;
                        import org.hamcrest.Matcher;
                        import org.hamcrest.MatcherAssert;
                        
                        @Generated(
                                value = "io.github.marmer.testutils.annotationprocessing.jpojoassert.AssertionGeneratorProcessor",
                                date = "$now")
                        public class ContainerTypeAsserter implements PojoAsserter<ContainerType>{
                            private final PojoAssertionBuilder<ContainerType> pojoAssertionBuilder;
                        
                            private ContainerTypeAsserter(final ContainerType base) {
                                this(new PojoAssertionBuilder<ContainerType>(base, Collections.emptyList(), "ContainerType"));
                            }
                        
                            private ContainerTypeAsserter(final PojoAssertionBuilder<ContainerType> pojoAssertionBuilder) {
                                this.pojoAssertionBuilder = pojoAssertionBuilder;
                            }
                        
                            public static  ContainerTypeAsserter prepareFor(final ContainerType base) {
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
                            public static class DirectInnerTypeAsserter implements PojoAsserter<ContainerType.DirectInnerType>{
                                private final PojoAssertionBuilder<ContainerType.DirectInnerType> pojoAssertionBuilder;

                                private DirectInnerTypeAsserter(final ContainerType.DirectInnerType base) {
                                    this(new PojoAssertionBuilder<ContainerType.DirectInnerType>(base, Collections.emptyList(), "DirectInnerType"));
                                }

                                private DirectInnerTypeAsserter(final PojoAssertionBuilder<ContainerType.DirectInnerType> pojoAssertionBuilder) {
                                    this.pojoAssertionBuilder = pojoAssertionBuilder;
                                }

                                public static  DirectInnerTypeAsserter prepareFor(final ContainerType.DirectInnerType base) {
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

    @Test
    fun `generation should work for nested generic types`() {
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
                
                public interface ContainerType<P> {
                     interface DirectInnerType<C>{
                     }
                }
                """.trimIndent()
        )
        val now = LocalDateTime.of(1985, 1, 2, 3, 4, 5, 123000000)
        @Language("JAVA") val output = JavaFileObjects.forSourceString(
            "some.other.pck.ContainerTypeAsserter", """
                        package some.other.pck;
                        
                        import io.github.marmer.testutils.annotationprocessing.jpojoassert.AssertionCallback;
                        import io.github.marmer.testutils.annotationprocessing.jpojoassert.PojoAsserter;
                        import io.github.marmer.testutils.annotationprocessing.jpojoassert.PojoAssertionBuilder;
                        import java.util.Collections;
                        import javax.annotation.processing.Generated;
                        import org.hamcrest.Matcher;
                        import org.hamcrest.MatcherAssert;
                        
                        @Generated(
                                value = "io.github.marmer.testutils.annotationprocessing.jpojoassert.AssertionGeneratorProcessor",
                                date = "$now")
                        public class ContainerTypeAsserter<P> implements PojoAsserter<ContainerType<P>> {
                            private final PojoAssertionBuilder<ContainerType<P>> pojoAssertionBuilder;
                        
                            private ContainerTypeAsserter(final ContainerType<P> base) {
                                this(new PojoAssertionBuilder<ContainerType<P>>(base, Collections.emptyList(), "ContainerType"));
                            }
                        
                            private ContainerTypeAsserter(final PojoAssertionBuilder<ContainerType<P>> pojoAssertionBuilder) {
                                this.pojoAssertionBuilder = pojoAssertionBuilder;
                            }
                        
                            public static <P> ContainerTypeAsserter<P> prepareFor(final ContainerType<P> base) {
                                return new ContainerTypeAsserter<P>(base);
                            }
                        
                            public ContainerTypeAsserter<P> with(final AssertionCallback<ContainerType<P>> assertionCallback) {
                                return new ContainerTypeAsserter<P>(pojoAssertionBuilder.add(assertionCallback));
                            }
                        
                            public ContainerTypeAsserter<P> matches(final Matcher<? super ContainerType<P>> matcher) {
                                return new ContainerTypeAsserter<P>(pojoAssertionBuilder.add(base -> MatcherAssert.assertThat(base, matcher)));
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
                            public static class DirectInnerTypeAsserter<C> implements PojoAsserter<ContainerType.DirectInnerType<C>>{
                                private final PojoAssertionBuilder<ContainerType.DirectInnerType<C>> pojoAssertionBuilder;

                                private DirectInnerTypeAsserter(final ContainerType.DirectInnerType<C> base) {
                                    this(new PojoAssertionBuilder<ContainerType.DirectInnerType<C>>(base, Collections.emptyList(), "DirectInnerType"));
                                }

                                private DirectInnerTypeAsserter(final PojoAssertionBuilder<ContainerType.DirectInnerType<C>> pojoAssertionBuilder) {
                                    this.pojoAssertionBuilder = pojoAssertionBuilder;
                                }

                                public static <C> DirectInnerTypeAsserter<C> prepareFor(final ContainerType.DirectInnerType<C> base) {
                                    return new DirectInnerTypeAsserter<C>(base);
                                }

                                public DirectInnerTypeAsserter<C> with(final AssertionCallback<ContainerType.DirectInnerType<C>> assertionCallback) {
                                    return new DirectInnerTypeAsserter<C>(pojoAssertionBuilder.add(assertionCallback));
                                }

                                public DirectInnerTypeAsserter<C> matches(final Matcher<? super ContainerType.DirectInnerType<C>> matcher) {
                                    return new DirectInnerTypeAsserter<C>(pojoAssertionBuilder.add(base -> MatcherAssert.assertThat(base, matcher)));
                                }

                                public void assertToFirstFail() {
                                    pojoAssertionBuilder.assertToFirstFail();
                                }

                                public void assertAll() {
                                    pojoAssertionBuilder.assertAll();
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
                
                public class ContainerType {
                     interface DirectInnerInterface{
                         interface TransitiveInnerType{}
                     }
                     
                     public enum InnerEnum{}
                     
                     public static class InnerPublicStaticClass{}
                     
                     static class InnerPackagePrivateStaticClass{}
                     
                     private static class InnerPrivateStaticClass{}
                     
                     public class InnerPublicClass{}
                }
                """.trimIndent()
        )
        val now = LocalDateTime.of(1985, 1, 2, 3, 4, 5, 123000000)
        @Language("JAVA") val output = JavaFileObjects.forSourceString(
            "some.other.pck.ContainerTypeAsserter", """
                        package some.other.pck;
                        
                        import io.github.marmer.testutils.annotationprocessing.jpojoassert.AssertionCallback;
                        import io.github.marmer.testutils.annotationprocessing.jpojoassert.PojoAsserter;
                        import io.github.marmer.testutils.annotationprocessing.jpojoassert.PojoAssertionBuilder;
                        
                        import java.lang.Class;
                        import java.util.Collections;
                        import javax.annotation.processing.Generated;
                        
                        import org.hamcrest.Matcher;
                        import org.hamcrest.MatcherAssert;
                        import org.hamcrest.Matchers;
                        
                        @Generated(
                                value = "io.github.marmer.testutils.annotationprocessing.jpojoassert.AssertionGeneratorProcessor",
                                date = "$now")
                        public class ContainerTypeAsserter implements PojoAsserter<ContainerType>{
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
                        
                            public ContainerTypeAsserter withClass(final AssertionCallback<Class<?>> assertionCallback) {
                                return new ContainerTypeAsserter(pojoAssertionBuilder.add("class", base -> assertionCallback.assertFor(base.getClass())));
                            }
                        
                            public ContainerTypeAsserter hasClass(final Class<?> value) {
                                return hasClass(Matchers.equalTo(value));
                            }
                        
                            public ContainerTypeAsserter hasClass(final Matcher<? super Class<?>> matcher) {
                                return new ContainerTypeAsserter(pojoAssertionBuilder.add(base -> MatcherAssert.assertThat(base, Matchers.hasProperty("class", matcher))));
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
                            public static class DirectInnerInterfaceAsserter implements PojoAsserter<ContainerType.DirectInnerInterface> {
                                private final PojoAssertionBuilder<ContainerType.DirectInnerInterface> pojoAssertionBuilder;
                        
                                private DirectInnerInterfaceAsserter(final ContainerType.DirectInnerInterface base) {
                                    this(new PojoAssertionBuilder<ContainerType.DirectInnerInterface>(base, Collections.emptyList(), "DirectInnerInterface"));
                                }
                        
                                private DirectInnerInterfaceAsserter(final PojoAssertionBuilder<ContainerType.DirectInnerInterface> pojoAssertionBuilder) {
                                    this.pojoAssertionBuilder = pojoAssertionBuilder;
                                }
                        
                                public static DirectInnerInterfaceAsserter prepareFor(final ContainerType.DirectInnerInterface base) {
                                    return new DirectInnerInterfaceAsserter(base);
                                }
                        
                                public DirectInnerInterfaceAsserter with(final AssertionCallback<ContainerType.DirectInnerInterface> assertionCallback) {
                                    return new DirectInnerInterfaceAsserter(pojoAssertionBuilder.add(assertionCallback));
                                }
                        
                                public DirectInnerInterfaceAsserter matches(final Matcher<? super ContainerType.DirectInnerInterface> matcher) {
                                    return new DirectInnerInterfaceAsserter(pojoAssertionBuilder.add(base -> MatcherAssert.assertThat(base, matcher)));
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
                                public static class TransitiveInnerTypeAsserter implements PojoAsserter<ContainerType.DirectInnerInterface.TransitiveInnerType> {
                                    private final PojoAssertionBuilder<ContainerType.DirectInnerInterface.TransitiveInnerType> pojoAssertionBuilder;
                        
                                    private TransitiveInnerTypeAsserter(final ContainerType.DirectInnerInterface.TransitiveInnerType base) {
                                        this(new PojoAssertionBuilder<ContainerType.DirectInnerInterface.TransitiveInnerType>(base, Collections.emptyList(), "TransitiveInnerType"));
                                    }
                        
                                    private TransitiveInnerTypeAsserter(final PojoAssertionBuilder<ContainerType.DirectInnerInterface.TransitiveInnerType> pojoAssertionBuilder) {
                                        this.pojoAssertionBuilder = pojoAssertionBuilder;
                                    }
                        
                                    public static TransitiveInnerTypeAsserter prepareFor(final ContainerType.DirectInnerInterface.TransitiveInnerType base) {
                                        return new TransitiveInnerTypeAsserter(base);
                                    }
                        
                                    public TransitiveInnerTypeAsserter with(final AssertionCallback<ContainerType.DirectInnerInterface.TransitiveInnerType> assertionCallback) {
                                        return new TransitiveInnerTypeAsserter(pojoAssertionBuilder.add(assertionCallback));
                                    }
                        
                                    public TransitiveInnerTypeAsserter matches(final Matcher<? super ContainerType.DirectInnerInterface.TransitiveInnerType> matcher) {
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

                            @Generated(
                                    value = "io.github.marmer.testutils.annotationprocessing.jpojoassert.AssertionGeneratorProcessor",
                                    date = "$now")
                            public static class InnerEnumAsserter implements PojoAsserter<ContainerType.InnerEnum>{
                                private final PojoAssertionBuilder<ContainerType.InnerEnum> pojoAssertionBuilder;

                                private InnerEnumAsserter(final ContainerType.InnerEnum base) {
                                    this(new PojoAssertionBuilder<ContainerType.InnerEnum>(base, Collections.emptyList(), "InnerEnum"));
                                }

                                private InnerEnumAsserter(final PojoAssertionBuilder<ContainerType.InnerEnum> pojoAssertionBuilder) {
                                    this.pojoAssertionBuilder = pojoAssertionBuilder;
                                }

                                public static InnerEnumAsserter prepareFor(final ContainerType.InnerEnum base) {
                                    return new InnerEnumAsserter(base);
                                }

                                public InnerEnumAsserter with(final AssertionCallback<ContainerType.InnerEnum> assertionCallback) {
                                    return new InnerEnumAsserter(pojoAssertionBuilder.add(assertionCallback));
                                }

                                public InnerEnumAsserter matches(final Matcher<? super ContainerType.InnerEnum> matcher) {
                                    return new InnerEnumAsserter(pojoAssertionBuilder.add(base -> MatcherAssert.assertThat(base, matcher)));
                                }

                                public void assertToFirstFail() {
                                    pojoAssertionBuilder.assertToFirstFail();
                                }

                                public void assertAll() {
                                    pojoAssertionBuilder.assertAll();
                                }
                            }

                            @Generated(
                                    value = "io.github.marmer.testutils.annotationprocessing.jpojoassert.AssertionGeneratorProcessor",
                                    date = "$now")
                            public static class InnerPublicStaticClassAsserter implements PojoAsserter<ContainerType.InnerPublicStaticClass>{
                                private final PojoAssertionBuilder<ContainerType.InnerPublicStaticClass> pojoAssertionBuilder;

                                private InnerPublicStaticClassAsserter(final ContainerType.InnerPublicStaticClass base) {
                                    this(new PojoAssertionBuilder<ContainerType.InnerPublicStaticClass>(base, Collections.emptyList(), "InnerPublicStaticClass"));
                                }

                                private InnerPublicStaticClassAsserter(final PojoAssertionBuilder<ContainerType.InnerPublicStaticClass> pojoAssertionBuilder) {
                                    this.pojoAssertionBuilder = pojoAssertionBuilder;
                                }

                                public static InnerPublicStaticClassAsserter prepareFor(final ContainerType.InnerPublicStaticClass base) {
                                    return new InnerPublicStaticClassAsserter(base);
                                }

                                public InnerPublicStaticClassAsserter with(final AssertionCallback<ContainerType.InnerPublicStaticClass> assertionCallback) {
                                    return new InnerPublicStaticClassAsserter(pojoAssertionBuilder.add(assertionCallback));
                                }

                                public InnerPublicStaticClassAsserter matches(final Matcher<? super ContainerType.InnerPublicStaticClass> matcher) {
                                    return new InnerPublicStaticClassAsserter(pojoAssertionBuilder.add(base -> MatcherAssert.assertThat(base, matcher)));
                                }

                                public InnerPublicStaticClassAsserter withClass(
                                        final AssertionCallback<Class<?>> assertionCallback) {
                                    return new InnerPublicStaticClassAsserter(pojoAssertionBuilder.add("class", base -> assertionCallback.assertFor(base.getClass())));
                                }

                                public InnerPublicStaticClassAsserter hasClass(final Class<?> value) {
                                    return hasClass(Matchers.equalTo(value));
                                }

                                public InnerPublicStaticClassAsserter hasClass(final Matcher<? super Class<?>> matcher) {
                                    return new InnerPublicStaticClassAsserter(pojoAssertionBuilder.add(base -> MatcherAssert.assertThat(base, Matchers.hasProperty("class", matcher))));
                                }

                                public void assertToFirstFail() {
                                    pojoAssertionBuilder.assertToFirstFail();
                                }

                                public void assertAll() {
                                    pojoAssertionBuilder.assertAll();
                                }
                            }

                            @Generated(
                                    value = "io.github.marmer.testutils.annotationprocessing.jpojoassert.AssertionGeneratorProcessor",
                                    date = "$now")
                            public static class InnerPackagePrivateStaticClassAsserter implements PojoAsserter<ContainerType.InnerPackagePrivateStaticClass>{
                                private final PojoAssertionBuilder<ContainerType.InnerPackagePrivateStaticClass> pojoAssertionBuilder;

                                private InnerPackagePrivateStaticClassAsserter(final ContainerType.InnerPackagePrivateStaticClass base) {
                                    this(new PojoAssertionBuilder<ContainerType.InnerPackagePrivateStaticClass>(base, Collections.emptyList(), "InnerPackagePrivateStaticClass"));
                                }

                                private InnerPackagePrivateStaticClassAsserter(final PojoAssertionBuilder<ContainerType.InnerPackagePrivateStaticClass> pojoAssertionBuilder) {
                                    this.pojoAssertionBuilder = pojoAssertionBuilder;
                                }

                                public static InnerPackagePrivateStaticClassAsserter prepareFor(final ContainerType.InnerPackagePrivateStaticClass base) {
                                    return new InnerPackagePrivateStaticClassAsserter(base);
                                }

                                public InnerPackagePrivateStaticClassAsserter with(final AssertionCallback<ContainerType.InnerPackagePrivateStaticClass> assertionCallback) {
                                    return new InnerPackagePrivateStaticClassAsserter(pojoAssertionBuilder.add(assertionCallback));
                                }

                                public InnerPackagePrivateStaticClassAsserter matches(final Matcher<? super ContainerType.InnerPackagePrivateStaticClass> matcher) {
                                    return new InnerPackagePrivateStaticClassAsserter(pojoAssertionBuilder.add(base -> MatcherAssert.assertThat(base, matcher)));
                                }

                                public InnerPackagePrivateStaticClassAsserter withClass(
                                        final AssertionCallback<Class<?>> assertionCallback) {
                                    return new InnerPackagePrivateStaticClassAsserter(pojoAssertionBuilder.add("class", base -> assertionCallback.assertFor(base.getClass())));
                                }

                                public InnerPackagePrivateStaticClassAsserter hasClass(final Class<?> value) {
                                    return hasClass(Matchers.equalTo(value));
                                }

                                public InnerPackagePrivateStaticClassAsserter hasClass(
                                        final Matcher<? super Class<?>> matcher) {
                                    return new InnerPackagePrivateStaticClassAsserter(pojoAssertionBuilder.add(base -> MatcherAssert.assertThat(base, Matchers.hasProperty("class", matcher))));
                                }

                                public void assertToFirstFail() {
                                    pojoAssertionBuilder.assertToFirstFail();
                                }

                                public void assertAll() {
                                    pojoAssertionBuilder.assertAll();
                                }
                            }

                            @Generated(
                                    value = "io.github.marmer.testutils.annotationprocessing.jpojoassert.AssertionGeneratorProcessor",
                                    date = "$now")
                            public static class InnerPublicClassAsserter implements PojoAsserter<ContainerType.InnerPublicClass>{
                                private final PojoAssertionBuilder<ContainerType.InnerPublicClass> pojoAssertionBuilder;

                                private InnerPublicClassAsserter(final ContainerType.InnerPublicClass base) {
                                    this(new PojoAssertionBuilder<ContainerType.InnerPublicClass>(base, Collections.emptyList(), "InnerPublicClass"));
                                }

                                private InnerPublicClassAsserter(final PojoAssertionBuilder<ContainerType.InnerPublicClass> pojoAssertionBuilder) {
                                    this.pojoAssertionBuilder = pojoAssertionBuilder;
                                }

                                public static InnerPublicClassAsserter prepareFor(final ContainerType.InnerPublicClass base) {
                                    return new InnerPublicClassAsserter(base);
                                }

                                public InnerPublicClassAsserter with(final AssertionCallback<ContainerType.InnerPublicClass> assertionCallback) {
                                    return new InnerPublicClassAsserter(pojoAssertionBuilder.add(assertionCallback));
                                }

                                public InnerPublicClassAsserter matches(final Matcher<? super ContainerType.InnerPublicClass> matcher) {
                                    return new InnerPublicClassAsserter(pojoAssertionBuilder.add(base -> MatcherAssert.assertThat(base, matcher)));
                                }

                                public InnerPublicClassAsserter withClass(final AssertionCallback<Class<?>> assertionCallback) {
                                    return new InnerPublicClassAsserter(pojoAssertionBuilder.add("class", base -> assertionCallback.assertFor(base.getClass())));
                                }

                                public InnerPublicClassAsserter hasClass(final Class<?> value) {
                                    return hasClass(Matchers.equalTo(value));
                                }

                                public InnerPublicClassAsserter hasClass(final Matcher<? super Class<?>> matcher) {
                                    return new InnerPublicClassAsserter(pojoAssertionBuilder.add(base -> MatcherAssert.assertThat(base, Matchers.hasProperty("class", matcher))));
                                }

                                public void assertToFirstFail() {
                                    pojoAssertionBuilder.assertToFirstFail();
                                }

                                public void assertAll() {
                                    pojoAssertionBuilder.assertAll();
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
                    import io.github.marmer.testutils.annotationprocessing.jpojoassert.PojoAsserter;
                    import io.github.marmer.testutils.annotationprocessing.jpojoassert.PojoAssertionBuilder;
                    import java.util.Collections;
                    import javax.annotation.processing.Generated;
                    import org.hamcrest.Matcher;
                    import org.hamcrest.MatcherAssert;
                    
                    @Generated(
                            value = "io.github.marmer.testutils.annotationprocessing.jpojoassert.AssertionGeneratorProcessor",
                            date = "$now")
                    public class ${typeName}Asserter implements PojoAsserter<${typeName}>{
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
