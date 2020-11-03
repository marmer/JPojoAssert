package some.other.pck;

import lombok.Value;

@Value
public class SimplePojoInterface<A, B extends CharSequence> {
    String firstName;
}
