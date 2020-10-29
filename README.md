![CI](https://github.com/marmer/JPojoAssert/workflows/CI/badge.svg)

JPojoAssert
===========

Vision: A library which generates compile safe assertions for Pojos, Beans, Models, Entities, Objects, Types, ... (whatever you want to call it) with properties in a fluent and typesafe and (optional) an atomic way and independently of a build tool, ID or testingframework. (Should at least work with JUnit4, JUnit5 and TestNG).

How does it work? It's a Java annotation Processor.

Example:
```java
        // Pojos, Beans, Models, Entities, Objects, Types, ...
        public static class SomePojo{
                private String firstProperty;
                private String getFirstProperty(){
                        return firstProperty;
                }
        }
```

```java
        // Sample Assertion related to "SomePojo"        
        SomePojoAsserter.assertThat(pojo)
            .withFirstProperty() // Check whether the passed pojo has a property
            .withFirstProperty("Some value") // Equals Check for the value of the related property of the pojo
            .withFirstProperty(equalTo("Some value")) // Hamcrest check for the value of the related property of the pojo
            .withFirstProperty(it -> assertThat(it, equalTo("Some value"))) // Custom assertion related to the property (Here you can do annything and assert in any way you want. E.g. use assertThat from Hamcrest, AssertJ or Truth) 
            .matches(hasProperty("notExistingProperty")) //Ability to pass Hamcrest Matchers for the Pojo itself
            .matches( it -> assertThat(it, hasProperty("notExistingProperty")) )  // Custom assertion related to the pojo itself (Here you can do annything and assert in any way you want. E.g. use assertThat from Hamcrest, AssertJ or Truth) 
            .isInstanceOfSomePojo() // Optional Check whether it is an instance related to the Base Class the Asserter was created of
            .assertSoftly() // Soft assertion for an atomic result (you could also use assertHardly())
```

UNDER CONSTRUCTION
