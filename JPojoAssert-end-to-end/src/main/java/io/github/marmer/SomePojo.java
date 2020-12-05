package io.github.marmer;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public abstract class SomePojo<T extends CharSequence> {
    private final String firstName;
    private final List<T> titles;
    private final Address<String> address;

    @Getter
    @AllArgsConstructor
    public static class Address<X> {
        private final String city;
        private final String street;
        private final X x;
    }
}
