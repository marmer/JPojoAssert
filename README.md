![CI-Build](https://github.com/marmer/JPojoAssert/workflows/CI-Build/badge.svg)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.github.marmer.testutils/JPojoAssert/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.github.marmer.testutils/JPojoAssert)
 
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=io.github.marmer.testutils:JPojoAssert&metric=alert_status)](https://sonarcloud.io/dashboard?id=io.github.marmer.testutils:JPojoAssert)
[![Code Coverage](https://sonarcloud.io/api/project_badges/measure?project=io.github.marmer.testutils:JPojoAssert&metric=coverage)](https://sonarcloud.io/component_measures?id=io.github.marmer.testutils:JPojoAssert&metric=Coverage)
[![Technical Dept](https://sonarcloud.io/api/project_badges/measure?project=io.github.marmer.testutils:JPojoAssert&metric=sqale_index)](https://sonarcloud.io/project/issues?facetMode=effort&id=io.github.marmer.testutils:JPojoAssert&resolved=false&types=CODE_SMELL)

[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=io.github.marmer.testutils:JPojoAssert&metric=security_rating)](https://sonarcloud.io/component_measures?id=io.github.marmer.testutils:JPojoAssert&metric=Security)
[![Maintainability](https://sonarcloud.io/api/project_badges/measure?project=io.github.marmer.testutils:JPojoAssert&metric=sqale_rating)](https://sonarcloud.io/component_measures?id=io.github.marmer.testutils:JPojoAssert&metric=Maintainability)
[![Reliability](https://sonarcloud.io/api/project_badges/measure?project=io.github.marmer.testutils:JPojoAssert&metric=reliability_rating)](https://sonarcloud.io/component_measures?id=io.github.marmer.testutils:JPojoAssert&metric=Reliability)

JPojoAssert - (UNDER CONSTRUCTION)
===========

Vision: A library which generates compile safe assertions for Pojos, Beans, Models, Entities, Objects, Types, ... (whatever you want to call it) with properties in a fluent and typesafe and (optional) an atomic way and independently of a build tool, IDE or testingframework. (Should at least work with JUnit4, JUnit5 and TestNG).

How does it work? It's a Java annotation Processor.

Example:
```java
        // Some possible Pojos, Beans, Models, Entities, Objects, Types, ...
        public class SomePojo{
                private String firstProperty;
                public String getFirstProperty(){
                        return firstProperty;
                }
        }
        
        public interface SomePojo{
                String getFirstProperty();
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


