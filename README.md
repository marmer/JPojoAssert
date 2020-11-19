![CI-Build](https://github.com/marmer/JPojoAssert/workflows/CI-Build/badge.svg)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.github.marmer.testutils/JPojoAssert/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.github.marmer.testutils/JPojoAssert)
 
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=io.github.marmer.testutils:JPojoAssert&metric=alert_status)](https://sonarcloud.io/dashboard?id=io.github.marmer.testutils:JPojoAssert)
[![Code Coverage](https://sonarcloud.io/api/project_badges/measure?project=io.github.marmer.testutils:JPojoAssert&metric=coverage)](https://sonarcloud.io/component_measures?id=io.github.marmer.testutils:JPojoAssert&metric=Coverage)
[![Technical Dept](https://sonarcloud.io/api/project_badges/measure?project=io.github.marmer.testutils:JPojoAssert&metric=sqale_index)](https://sonarcloud.io/project/issues?facetMode=effort&id=io.github.marmer.testutils:JPojoAssert&resolved=false&types=CODE_SMELL)

[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=io.github.marmer.testutils:JPojoAssert&metric=security_rating)](https://sonarcloud.io/component_measures?id=io.github.marmer.testutils:JPojoAssert&metric=Security)
[![Maintainability](https://sonarcloud.io/api/project_badges/measure?project=io.github.marmer.testutils:JPojoAssert&metric=sqale_rating)](https://sonarcloud.io/component_measures?id=io.github.marmer.testutils:JPojoAssert&metric=Maintainability)
[![Reliability](https://sonarcloud.io/api/project_badges/measure?project=io.github.marmer.testutils:JPojoAssert&metric=reliability_rating)](https://sonarcloud.io/component_measures?id=io.github.marmer.testutils:JPojoAssert&metric=Reliability)

JPojoAssert
===========

This library provides the generation of fluent, atomic assertion classes for pojos, beans, models, entities, or whatever you want to call your object (especially with properties). All this happens without the need to pollute the production code with annotations. And can be used as an adon for all java testing frameworks.

Bonus: Because this library is an annotation processor, you can use it without any plugins for your IDE or build tool as long as annotation processing is supported (so even with plain javac) 

Sample/Getting Started:
-----------------------

Att the following dependency to your pom
```.xml
        <dependency>
            <artifactId>JPojoAssert-processor</artifactId>
            <groupId>io.github.marmer.testutils</groupId>
            <version>${JPojoAssert.version}</version>
            <scope>test</scope>
        </dependency>
```

Create some Types:
```java
        // Some possible Pojos, Beans, Models, Entities, Objects, Types, ...
        public class SomePojo{
                private String firstName;
                public String getFirstName(){
                        return firstName;
                }
        }
        
        public interface SomePojo{
                String getFirstName();
        }
```

Configure the generation for the types:
```.java
@GenerateAsserter({
        "io.github.marmer", //Package Configuration
        "io.github.marmer.SomePojo" //Qualified type name configuration
})
class JPojoAssertConfiguration {}
```

Enjoy some readable compile safe assertions for your configured types:
```java
        // Sample Assertion related to "SomePojo"        
        SomePojoAsserter.prepareFor(pojo)
            .with( it -> assertThat(it, hasProperty("notExistingProperty")) )  // Custom assertion related to the pojo itself (Here you can do annything and assert in any way you want. E.g. use assertThat from Hamcrest, AssertJ or Truth)
            .withFirstName(it -> assertThat(it, equalTo("Some value"))) // Custom assertion related to the property (Here you can do annything and assert in any way you want. E.g. use assertThat from Hamcrest, AssertJ or Truth)
            .hasFirstName("Some value") // Equals Check for the value of the related property of the pojo            
            .withFirstName(equalTo("Some value")) // Hamcrest check for the value of the related property of the pojo 

//Still Work in Progress
            .hasFirstName() // Check whether the passed pojo has a property
//Still Work in Progress
            .matches(hasProperty("notExistingProperty")) //Ability to pass Hamcrest Matchers for the Pojo itself
//Still Work in Progress
            .isInstanceOfSomePojo() // Optional Check whether it is an instance related to the Base Class the Asserter was created of

            .assertAll() // Soft assertion for an atomic result (you could also use assertToFirstFail() to fail fast)
```


Changelog
---------
### 0.3.0
Feature: ability to assert for equality of property values e.g.: 
```.java
 asserter.withFirstName("Some value")
```
### 0.2.1
Fix: Interopt with Java
Fix: Changelog added
Fix: generic version in Readme sample dependency
### 0.2.0
Feature: Generation of convenience methods for properties with hamcrest matchers e.g.:
```.java 
asserter.withFirstName(equalTo("Some value")))
```
### 0.1.1
Fix: Property names in error messages
### 0.1.0
Feature: Simple generation of Asserter classes e.g.:
 ```.java 
 SomePojoAsserter.prepareFor(pojo).with( it -> assertThat(it, hasProperty("notExistingProperty")) )  // Custom assertion related to the pojo itself (Here you can do annything and assert in any way you want. E.g. use assertThat from Hamcrest, AssertJ or Truth))
```
