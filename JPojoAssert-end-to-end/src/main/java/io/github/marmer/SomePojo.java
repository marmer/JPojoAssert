package io.github.marmer;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public abstract class SomePojo<T extends CharSequence> {
    private final String firstName;
    private final List<T> titles;
}
