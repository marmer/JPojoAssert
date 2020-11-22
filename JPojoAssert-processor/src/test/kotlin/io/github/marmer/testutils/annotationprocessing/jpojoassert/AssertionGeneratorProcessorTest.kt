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
                    
                    public ExampleTypeAsserter withObjectProperty(final AssertionCallback<String> assertionCallback) {
                        return new ExampleTypeAsserter(pojoAssertionBuilder.add("objectProperty", base -> assertionCallback.accept(base.getObjectProperty())));
                    }
                    
                    public ExampleTypeAsserter hasObjectProperty(final String value) {
                        return hasObjectProperty(Matchers.equalTo(value));
                    }
                    
                    public ExampleTypeAsserter hasObjectProperty(final Matcher<? super String> matcher) {
                        return new ExampleTypeAsserter(pojoAssertionBuilder.add(base -> MatcherAssert.assertThat(base, Matchers.hasProperty("objectProperty", matcher))));
                    }
                    
                    public ExampleTypeAsserter withPrimitiveProperty(final AssertionCallback<Integer> assertionCallback) {
                        return new ExampleTypeAsserter(pojoAssertionBuilder.add("primitiveProperty", base -> assertionCallback.accept(base.getPrimitiveProperty())));
                    }
                    
                    public ExampleTypeAsserter hasPrimitiveProperty(final Integer value) {
                        return hasPrimitiveProperty(Matchers.equalTo(value));
                    }
                    
                    public ExampleTypeAsserter hasPrimitiveProperty(final Matcher<? super Integer> matcher) {
                        return new ExampleTypeAsserter(pojoAssertionBuilder.add(base -> MatcherAssert.assertThat(base, Matchers.hasProperty("primitiveProperty", matcher))));
                    }
                    
                    public ExampleTypeAsserter withPrimitiveBooleanProperty(final AssertionCallback<Boolean> assertionCallback) {
                        return new ExampleTypeAsserter(pojoAssertionBuilder.add("primitiveBooleanProperty", base -> assertionCallback.accept(base.isPrimitiveBooleanProperty())));
                    }
                    
                    public ExampleTypeAsserter hasPrimitiveBooleanProperty(final Boolean value) {
                        return hasPrimitiveBooleanProperty(Matchers.equalTo(value));
                    }
                    
                    public ExampleTypeAsserter hasPrimitiveBooleanProperty(final Matcher<? super Boolean> matcher) {
                        return new ExampleTypeAsserter(pojoAssertionBuilder.add(base -> MatcherAssert.assertThat(base, Matchers.hasProperty("primitiveBooleanProperty", matcher))));
                    }
                    
                    public ExampleTypeAsserter withGetRightBooleanMixProperty(final AssertionCallback<Boolean> assertionCallback) {
                        return new ExampleTypeAsserter(pojoAssertionBuilder.add("getRightBooleanMixProperty", base -> assertionCallback.accept(base.isGetRightBooleanMixProperty())));
                    }
                    
                    public ExampleTypeAsserter hasGetRightBooleanMixProperty(final Boolean value) {
                        return hasGetRightBooleanMixProperty(Matchers.equalTo(value));
                    }
                    
                    public ExampleTypeAsserter hasGetRightBooleanMixProperty(final Matcher<? super Boolean> matcher) {
                        return new ExampleTypeAsserter(pojoAssertionBuilder.add(base -> MatcherAssert.assertThat(base, Matchers.hasProperty("getRightBooleanMixProperty", matcher))));
                    }
                    
                    public ExampleTypeAsserter withIsWrongPrimitiveBooleanProperty(final AssertionCallback<Boolean> assertionCallback) {
                        return new ExampleTypeAsserter(pojoAssertionBuilder.add("isWrongPrimitiveBooleanProperty", base -> assertionCallback.accept(base.getIsWrongPrimitiveBooleanProperty())));
                    }
                    
                    public ExampleTypeAsserter hasIsWrongPrimitiveBooleanProperty(final Boolean value) {
                        return hasIsWrongPrimitiveBooleanProperty(Matchers.equalTo(value));
                    }
                    
                    public ExampleTypeAsserter hasIsWrongPrimitiveBooleanProperty(final Matcher<? super Boolean> matcher) {
                        return new ExampleTypeAsserter(pojoAssertionBuilder.add(base -> MatcherAssert.assertThat(base, Matchers.hasProperty("isWrongPrimitiveBooleanProperty", matcher))));
                    }
                    
                    public ExampleTypeAsserter withGetWrongBooleanWrapperProperty(final AssertionCallback<Boolean> assertionCallback) {
                        return new ExampleTypeAsserter(pojoAssertionBuilder.add("getWrongBooleanWrapperProperty", base -> assertionCallback.accept(base.isGetWrongBooleanWrapperProperty())));
                    }
                    
                    public ExampleTypeAsserter hasGetWrongBooleanWrapperProperty(final Boolean value) {
                        return hasGetWrongBooleanWrapperProperty(Matchers.equalTo(value));
                    }
                    
                    public ExampleTypeAsserter hasGetWrongBooleanWrapperProperty(final Matcher<? super Boolean> matcher) {
                        return new ExampleTypeAsserter(pojoAssertionBuilder.add(base -> MatcherAssert.assertThat(base, Matchers.hasProperty("getWrongBooleanWrapperProperty", matcher))));
                    }
                    
                    public ExampleTypeAsserter withIsRightBooleanWrapperProperty(final AssertionCallback<Boolean> assertionCallback) {
                        return new ExampleTypeAsserter(pojoAssertionBuilder.add("isRightBooleanWrapperProperty", base -> assertionCallback.accept(base.getIsRightBooleanWrapperProperty())));
                    }
                    
                    public ExampleTypeAsserter hasIsRightBooleanWrapperProperty(final Boolean value) {
                        return hasIsRightBooleanWrapperProperty(Matchers.equalTo(value));
                    }
                    
                    public ExampleTypeAsserter hasIsRightBooleanWrapperProperty(final Matcher<? super Boolean> matcher) {
                        return new ExampleTypeAsserter(pojoAssertionBuilder.add(base -> MatcherAssert.assertThat(base, Matchers.hasProperty("isRightBooleanWrapperProperty", matcher))));
                    }
                    
                    public ExampleTypeAsserter withPrimitiveArrayProperty(final AssertionCallback<int[]> assertionCallback) {
                        return new ExampleTypeAsserter(pojoAssertionBuilder.add("primitiveArrayProperty", base -> assertionCallback.accept(base.getPrimitiveArrayProperty())));
                    }
                    
                    public ExampleTypeAsserter hasPrimitiveArrayProperty(final int[] value) {
                        return hasPrimitiveArrayProperty(Matchers.equalTo(value));
                    }
                    
                    public ExampleTypeAsserter hasPrimitiveArrayProperty(final Matcher<? super int[]> matcher) {
                        return new ExampleTypeAsserter(pojoAssertionBuilder.add(base -> MatcherAssert.assertThat(base, Matchers.hasProperty("primitiveArrayProperty", matcher))));
                    }
                    
                    public ExampleTypeAsserter withObjectArrayProperty(final AssertionCallback<String[]> assertionCallback) {
                        return new ExampleTypeAsserter(pojoAssertionBuilder.add("objectArrayProperty", base -> assertionCallback.accept(base.getObjectArrayProperty())));
                    }
                    
                    public ExampleTypeAsserter hasObjectArrayProperty(final String[] value) {
                        return hasObjectArrayProperty(Matchers.equalTo(value));
                    }
                    
                    public ExampleTypeAsserter hasObjectArrayProperty(final Matcher<? super String[]> matcher) {
                        return new ExampleTypeAsserter(pojoAssertionBuilder.add(base -> MatcherAssert.assertThat(base, Matchers.hasProperty("objectArrayProperty", matcher))));
                    }
                    
                    public ExampleTypeAsserter withMultidimensionalArrayProperty(final AssertionCallback<String[][]> assertionCallback) {
                        return new ExampleTypeAsserter(pojoAssertionBuilder.add("multidimensionalArrayProperty", base -> assertionCallback.accept(base.getMultidimensionalArrayProperty())));
                    }
                    
                    public ExampleTypeAsserter hasMultidimensionalArrayProperty(final String[][] value) {
                        return hasMultidimensionalArrayProperty(Matchers.equalTo(value));
                    }
                    
                    public ExampleTypeAsserter hasMultidimensionalArrayProperty(final Matcher<? super String[][]> matcher) {
                        return new ExampleTypeAsserter(pojoAssertionBuilder.add(base -> MatcherAssert.assertThat(base, Matchers.hasProperty("multidimensionalArrayProperty", matcher))));
                    }
                    
                    public ExampleTypeAsserter withPackagePrivateProperty(final AssertionCallback<String> assertionCallback) {
                        return new ExampleTypeAsserter(pojoAssertionBuilder.add("packagePrivateProperty", base -> assertionCallback.accept(base.getPackagePrivateProperty())));
                    }
                    
                    public ExampleTypeAsserter hasPackagePrivateProperty(final String value) {
                        return hasPackagePrivateProperty(Matchers.equalTo(value));
                    }
                    
                    public ExampleTypeAsserter hasPackagePrivateProperty(final Matcher<? super String> matcher) {
                        return new ExampleTypeAsserter(pojoAssertionBuilder.add(base -> MatcherAssert.assertThat(base, Matchers.hasProperty("packagePrivateProperty", matcher))));
                    }
                    
                    public ExampleTypeAsserter withProtectedProperty(final AssertionCallback<String> assertionCallback) {
                        return new ExampleTypeAsserter(pojoAssertionBuilder.add("protectedProperty", base -> assertionCallback.accept(base.getProtectedProperty())));
                    }
                    
                    public ExampleTypeAsserter hasProtectedProperty(final String value) {
                        return hasProtectedProperty(Matchers.equalTo(value));
                    }
                    
                    public ExampleTypeAsserter hasProtectedProperty(final Matcher<? super String> matcher) {
                        return new ExampleTypeAsserter(pojoAssertionBuilder.add(base -> MatcherAssert.assertThat(base, Matchers.hasProperty("protectedProperty", matcher))));
                    }
                    
                    public ExampleTypeAsserter withAbstractProperty(final AssertionCallback<String> assertionCallback) {
                        return new ExampleTypeAsserter(pojoAssertionBuilder.add("abstractProperty", base -> assertionCallback.accept(base.getAbstractProperty())));
                    }
                    
                    public ExampleTypeAsserter hasAbstractProperty(final String value) {
                        return hasAbstractProperty(Matchers.equalTo(value));
                    }
                    
                    public ExampleTypeAsserter hasAbstractProperty(final Matcher<? super String> matcher) {
                        return new ExampleTypeAsserter(pojoAssertionBuilder.add(base -> MatcherAssert.assertThat(base, Matchers.hasProperty("abstractProperty", matcher))));
                    }
                    
                    public ExampleTypeAsserter withFinalProperty(final AssertionCallback<String> assertionCallback) {
                        return new ExampleTypeAsserter(pojoAssertionBuilder.add("finalProperty", base -> assertionCallback.accept(base.getFinalProperty())));
                    }
                    
                    public ExampleTypeAsserter hasFinalProperty(final String value) {
                        return hasFinalProperty(Matchers.equalTo(value));
                    }
                    
                    public ExampleTypeAsserter hasFinalProperty(final Matcher<? super String> matcher) {
                        return new ExampleTypeAsserter(pojoAssertionBuilder.add(base -> MatcherAssert.assertThat(base, Matchers.hasProperty("finalProperty", matcher))));
                    }
                    
                    public ExampleTypeAsserter withClass(final AssertionCallback<Class<?>> assertionCallback) {
                        return new ExampleTypeAsserter(pojoAssertionBuilder.add("class", base -> assertionCallback.accept(base.getClass())));
                    }
                    
                    public ExampleTypeAsserter hasClass(final Class<?> value) {
                        return hasClass(Matchers.equalTo(value));
                    }
                    
                    public ExampleTypeAsserter hasClass(final Matcher<? super Class<?>> matcher) {
                        return new ExampleTypeAsserter(pojoAssertionBuilder.add(base -> MatcherAssert.assertThat(base, Matchers.hasProperty("class", matcher))));
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
                
                    public ExampleTypeAsserter<A, B, C, D> withGenericProperty(final AssertionCallback<Map<String, List<Integer>>> assertionCallback) {
                        return new ExampleTypeAsserter<A, B, C, D>(pojoAssertionBuilder.add("genericProperty", base -> assertionCallback.accept(base.getGenericProperty())));
                    }
                    
                    public ExampleTypeAsserter<A, B, C, D> hasGenericProperty(final Map<String, List<Integer>> value) {
                        return hasGenericProperty(Matchers.equalTo(value));
                    }
                    
                    public ExampleTypeAsserter<A, B, C, D> hasGenericProperty(final Matcher<? super Map<String, List<Integer>>> matcher) {
                        return new ExampleTypeAsserter<A, B, C, D>(pojoAssertionBuilder.add(base -> MatcherAssert.assertThat(base, Matchers.hasProperty("genericProperty",matcher))));
                    }
                
                    public ExampleTypeAsserter<A, B, C, D> withGenericFromTypeDefinitionProperty(final AssertionCallback<C> assertionCallback) {
                        return new ExampleTypeAsserter<A, B, C, D>(pojoAssertionBuilder.add("genericFromTypeDefinitionProperty", base -> assertionCallback.accept(base.getGenericFromTypeDefinitionProperty())));
                    }
                    
                    public ExampleTypeAsserter<A, B, C, D> hasGenericFromTypeDefinitionProperty(final C value) {
                        return hasGenericFromTypeDefinitionProperty(Matchers.equalTo(value));
                    }
                    
                    public ExampleTypeAsserter<A, B, C, D> hasGenericFromTypeDefinitionProperty(final Matcher<? super C> matcher) {
                        return new ExampleTypeAsserter<A, B, C, D>(pojoAssertionBuilder.add(base -> MatcherAssert.assertThat(base, Matchers.hasProperty("genericFromTypeDefinitionProperty",matcher))));
                    }
                
                    public ExampleTypeAsserter<A, B, C, D> withGenericFromTypeDefinitionPropertyAsGeneric(final AssertionCallback<List<C>> assertionCallback) {
                        return new ExampleTypeAsserter<A, B, C, D>(pojoAssertionBuilder.add("genericFromTypeDefinitionPropertyAsGeneric", base -> assertionCallback.accept(base.getGenericFromTypeDefinitionPropertyAsGeneric())));
                    }
                    
                    public ExampleTypeAsserter<A, B, C, D> hasGenericFromTypeDefinitionPropertyAsGeneric(final List<C> value) {
                        return hasGenericFromTypeDefinitionPropertyAsGeneric(Matchers.equalTo(value));
                    }
                    
                    public ExampleTypeAsserter<A, B, C, D> hasGenericFromTypeDefinitionPropertyAsGeneric(final Matcher<? super List<C>> matcher) {
                        return new ExampleTypeAsserter<A, B, C, D>(pojoAssertionBuilder.add(base -> MatcherAssert.assertThat(base, Matchers.hasProperty("genericFromTypeDefinitionPropertyAsGeneric",matcher))));
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
                
                    public ExampleTypeAsserter withSomeValue(final AssertionCallback<String> assertionCallback) {
                        return new ExampleTypeAsserter(pojoAssertionBuilder.add("someValue", base -> assertionCallback.accept(base.getSomeValue())));
                    }
                
                    public ExampleTypeAsserter hasSomeValue(final String value) {
                        return hasSomeValue(Matchers.equalTo(value));
                    }
                
                    public ExampleTypeAsserter hasSomeValue(final Matcher<? super String> matcher) {
                        return new ExampleTypeAsserter(pojoAssertionBuilder.add(base -> MatcherAssert.assertThat(base, Matchers.hasProperty("someValue", matcher))));
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

                    public ChildTypeAsserter withChildOnlyProperty(final AssertionCallback<String> assertionCallback) {
                        return new ChildTypeAsserter(pojoAssertionBuilder.add("childOnlyProperty", base -> assertionCallback.accept(base.getChildOnlyProperty())));
                    }

                    public ChildTypeAsserter hasChildOnlyProperty(final String value) {
                        return hasChildOnlyProperty(Matchers.equalTo(value));
                    }

                    public ChildTypeAsserter hasChildOnlyProperty(final Matcher<? super String> matcher) {
                        return new ChildTypeAsserter(pojoAssertionBuilder.add(base -> MatcherAssert.assertThat(base, Matchers.hasProperty("childOnlyProperty", matcher))));
                    }

                    public ChildTypeAsserter withChildAndParentPropertyWithDifferentReturnTypes(final AssertionCallback<String> assertionCallback) {
                        return new ChildTypeAsserter(pojoAssertionBuilder.add("childAndParentPropertyWithDifferentReturnTypes", base -> assertionCallback.accept(base.getChildAndParentPropertyWithDifferentReturnTypes())));
                    }

                    public ChildTypeAsserter hasChildAndParentPropertyWithDifferentReturnTypes(final String value) {
                        return hasChildAndParentPropertyWithDifferentReturnTypes(Matchers.equalTo(value));
                    }

                    public ChildTypeAsserter hasChildAndParentPropertyWithDifferentReturnTypes(final Matcher<? super String> matcher) {
                        return new ChildTypeAsserter(pojoAssertionBuilder.add(base -> MatcherAssert.assertThat(base, Matchers.hasProperty("childAndParentPropertyWithDifferentReturnTypes", matcher))));
                    }
                    
                    public ChildTypeAsserter withDirectParentClassProperty(final AssertionCallback<String> assertionCallback) {
                        return new ChildTypeAsserter(pojoAssertionBuilder.add("directParentClassProperty", base -> assertionCallback.accept(base.getDirectParentClassProperty())));
                    }

                    public ChildTypeAsserter hasDirectParentClassProperty(final String value) {
                        return hasDirectParentClassProperty(Matchers.equalTo(value));
                    }

                    public ChildTypeAsserter hasDirectParentClassProperty(final Matcher<? super String> matcher) {
                        return new ChildTypeAsserter(pojoAssertionBuilder.add(base -> MatcherAssert.assertThat(base, Matchers.hasProperty("directParentClassProperty", matcher))));
                    }
        
                    public ChildTypeAsserter withClass(final AssertionCallback<Class<?>> assertionCallback) {
                        return new ChildTypeAsserter(pojoAssertionBuilder.add("class", base -> assertionCallback.accept(base.getClass())));
                    }
                
                    public ChildTypeAsserter hasClass(final Class<?> value) {
                        return hasClass(Matchers.equalTo(value));
                    }
                    
                    public ChildTypeAsserter hasClass(final Matcher<? super Class<?>> matcher) {
                        return new ChildTypeAsserter(pojoAssertionBuilder.add(base -> MatcherAssert.assertThat(base, Matchers.hasProperty("class", matcher))));
                    }
                    
                    public ChildTypeAsserter withDirectInterfaceParentProperty(final AssertionCallback<String> assertionCallback) {
                        return new ChildTypeAsserter(pojoAssertionBuilder.add("directInterfaceParentProperty", base -> assertionCallback.accept(base.getDirectInterfaceParentProperty())));
                    }
                
                    public ChildTypeAsserter hasDirectInterfaceParentProperty(final String value) {
                        return hasDirectInterfaceParentProperty(Matchers.equalTo(value));
                    }
                
                    public ChildTypeAsserter hasDirectInterfaceParentProperty(final Matcher<? super String> matcher) {
                        return new ChildTypeAsserter(pojoAssertionBuilder.add(base -> MatcherAssert.assertThat(base, Matchers.hasProperty("directInterfaceParentProperty", matcher))));
                    }
                    
                    public ChildTypeAsserter withIndirectInterfaceParentProperty(final AssertionCallback<String> assertionCallback) {
                        return new ChildTypeAsserter(pojoAssertionBuilder.add("indirectInterfaceParentProperty", base -> assertionCallback.accept(base.getIndirectInterfaceParentProperty())));
                    }
                
                    public ChildTypeAsserter hasIndirectInterfaceParentProperty(final String value) {
                        return hasIndirectInterfaceParentProperty(Matchers.equalTo(value));
                    }
                
                    public ChildTypeAsserter hasIndirectInterfaceParentProperty(final Matcher<? super String> matcher) {
                        return new ChildTypeAsserter(pojoAssertionBuilder.add(base -> MatcherAssert.assertThat(base, Matchers.hasProperty("indirectInterfaceParentProperty", matcher))));
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
        val now = LocalDateTime.of(1985, 1, 2, 3, 4, 5, 123000000)

        // Execution
        Truth.assert_()
            .about(JavaSourcesSubjectFactory.javaSources())
            .that(listOf(configurationClass, javaFileObject))
            .processedWith(AssertionGeneratorProcessor { now })
            // Assertion
            .compilesWithoutWarnings()
            .withNoteContaining("Generation skipped for: 'some.other.pck.ExampleType' because is is already generated by this processor")
            .`in`(javaFileObject)
            .onLine(6)
            .atColumn(13)
    }

    @Test
    fun `if the configured type to generate does neither exist as type nore as package a warning should be given`() {
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
    fun `generated files only from different generators without an appropriate warning should raise a warning`() {
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
            "some.other.pck.FirstTypeAsserter", getEmptyAsserterStubFor(now, "FirstType").trimIndent()
        )

        @Language("JAVA") val secondTypeOutput = JavaFileObjects.forSourceString(
            "some.other.pck.SecondTypeAsserter", getEmptyAsserterStubFor(now, "SecondType")
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
            "some.other.pck.SomeTypeAsserter", getEmptyAsserterStubFor(now, "SomeType").trimIndent()
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
            "some.other.pck.FirstTypeAsserter", getEmptyAsserterStubFor(now, "FirstType").trimIndent()
        )

        @Language("JAVA") val secondTypeOutput = JavaFileObjects.forSourceString(
            "some.other.pck.SecondTypeAsserter", getEmptyAsserterStubFor(now, "SecondType")
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

    private fun getEmptyAsserterStubFor(now: LocalDateTime, typeName: String): String {
        return """
                    package some.other.pck;
                    
                    import io.github.marmer.testutils.annotationprocessing.jpojoassert.AssertionCallback;
                    import io.github.marmer.testutils.annotationprocessing.jpojoassert.PojoAssertionBuilder;
                    import java.util.Collections;
                    import javax.annotation.processing.Generated;
                    
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
