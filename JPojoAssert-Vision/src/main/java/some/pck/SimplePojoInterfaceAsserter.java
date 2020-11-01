package some.pck;

import io.github.marmer.testutils.annotationprocessing.jpojoassert.PojoAssertionBuilder;
import org.jetbrains.annotations.NotNull;

import javax.annotation.processing.Generated;
import java.util.function.Consumer;

import static java.util.Collections.emptyList;


@Generated(
        value = "io.github.marmer.testutils.annotationprocessing.jpojoassert.AssertionGeneratorProcessor",
        date = "today")
public class SimplePojoInterfaceAsserter {
    private final PojoAssertionBuilder<SimplePojoInterface> pojoAssertionBuilder;

    public SimplePojoInterfaceAsserter(SimplePojoInterface pojo) {
        this.pojoAssertionBuilder = new PojoAssertionBuilder<SimplePojoInterface>(pojo, emptyList(), "SimplePojo");
    }

    private SimplePojoInterfaceAsserter(PojoAssertionBuilder<SimplePojoInterface> pojoPojoAssertionBuilder) {
        this.pojoAssertionBuilder = pojoPojoAssertionBuilder;
    }

    public SimplePojoInterfaceAsserter add(@NotNull Consumer<SimplePojoInterface> assertionCallback) {
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
}
