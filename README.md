![CI](https://github.com/marmer/JPojoAssert/workflows/CI/badge.svg)

JPojoAssert
===========

Vision: A library which generates compile safe assertions for Pojos, Beans, Models, Entities, Objects, Types, ... (whatever you want to call it) with properties in a fluent and typesafe and (optional) an atomic way and independently of a build tool, ID or testingframework. (Should at least work with JUnit4, JUnit5 and TestNG).

How does it work? It's a Java annotation Processor.

Example:
```java
        SomePojoAsserter.assertThat(pojo)
            .matches(hasProperty("notExistingProperty"))
            .isInstanceOfSomePojo()
            .withFirstProperty()
            .withFirstProperty("Some value")
            .withFirstProperty(equalTo("Some value"))
            .withFirstProperty(() -> assertThat(it, equalTo("Some value")))
            .withSecondProperty()
            .withSecondProperty(42)
            .withSecondProperty(equalTo(42))
            .withSecondProperty(() -> assertThat(it, equalTo("42")))
            .assertSoftly()
```

UNDER CONSTRUCTION
