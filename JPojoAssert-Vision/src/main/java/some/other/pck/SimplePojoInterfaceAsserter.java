package some.other.pck;

import io.github.marmer.testutils.annotationprocessing.jpojoassert.PojoAssertionBuilder;

import javax.annotation.processing.Generated;
import java.util.Collections;
import java.util.function.Consumer;

@Generated(
        value = "io.github.marmer.testutils.annotationprocessing.jpojoassert.AssertionGeneratorProcessor",
        date = "now")
public class SimplePojoInterfaceAsserter {
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

    public SimplePojoInterfaceAsserter add(final Consumer<SimplePojoInterface> assertionCallback) {
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
}
