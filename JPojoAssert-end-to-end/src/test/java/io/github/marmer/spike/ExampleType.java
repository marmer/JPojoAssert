package io.github.marmer.spike;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public abstract class ExampleType<A extends CharSequence, B extends Consumer<A>, C extends Consumer<String>, D> {
    public Map<String, List<Integer>> getGenericProperty() {
        return null;
    }

    public final C getGenericFromTypeDefinitionProperty() {
        return null;
    }

    public final List<C> getGenericFromTypeDefinitionPropertyAsGeneric() {
        return null;
    }
}

