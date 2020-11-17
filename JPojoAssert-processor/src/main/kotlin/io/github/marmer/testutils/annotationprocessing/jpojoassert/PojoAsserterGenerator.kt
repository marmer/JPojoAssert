package io.github.marmer.testutils.annotationprocessing.jpojoassert

import com.squareup.javapoet.*
import com.squareup.javapoet.MethodSpec.methodBuilder
import org.hamcrest.Matcher
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import java.time.LocalDateTime
import java.util.*
import javax.annotation.processing.Generated
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.*
import javax.lang.model.type.PrimitiveType
import javax.lang.model.type.TypeKind
import javax.lang.model.type.TypeMirror


class PojoAsserterGenerator(
    private val processingEnv: ProcessingEnvironment,
    private val baseType: TypeElement,
    private val generationTimeStamp: () -> LocalDateTime,
    private val generationMarker: String,
) {
    fun generate() = JavaFile.builder(
        baseType.packageElement.toString(),
        TypeSpec.classBuilder(simpleAsserterName)
            .addModifiers(Modifier.PUBLIC)
            .addAnnotation(getGeneratedAnnotation())
            .addTypeVariables(baseType.typeParameters.map { TypeVariableName.get(it) })
            .addField(getPojoAssertionBuilderField())
            .addMethods(getInitializers())
            .addMethods(getBaseAssertionMethods())
            .addMethods(getPropertyAssertionMethods())
            .addMethods(getFinisherMethods())
            .build()
    ).build()
        .writeTo(processingEnv.filer)

    private fun getPropertyAssertionMethods() =
        baseType.properties
            .flatMap { property ->
                listOf(
                    getPlainAssertionMethodFor(property),
                    getMatcherAssertionMethodFor(property)
                )
            }

    private fun getPlainAssertionMethodFor(property: Property) =
        methodBuilder("with${property.name.capitalize()}")
            .addModifiers(Modifier.PUBLIC)
            .addParameter(
                ParameterizedTypeName.get(
                    ClassName.get(AssertionCallback::class.java),
                    TypeName.get(property.boxedType)
                ),
                "assertionCallback",
                Modifier.FINAL
            )
            .addStatement(
                "return new \$T($builderFieldName.add(\$S, base -> assertionCallback.accept(base.${property.accessor})))",
                getGeneratedTypeName(),
                property.name
            )
            .returns(getGeneratedTypeName())
            .build()

    private fun getMatcherAssertionMethodFor(property: Property) =
        methodBuilder("has${property.name.capitalize()}")
            .addModifiers(Modifier.PUBLIC)
            .addParameter(
                ParameterizedTypeName.get(
                    ClassName.get(Matcher::class.java),
                    WildcardTypeName.supertypeOf(TypeName.get(property.boxedType))
                ),
                "matcher",
                Modifier.FINAL
            )
            .addStatement(
                "return new \$T($builderFieldName.add(base -> \$T.assertThat(base, \$T.hasProperty(\$S, matcher))))",
                getGeneratedTypeName(),
                MatcherAssert::class.java,
                Matchers::class.java,
                property.name
            )
            .returns(getGeneratedTypeName())
            .build()

    private fun getBaseAssertionMethods() = listOf(
        methodBuilder("with")
            .addModifiers(Modifier.PUBLIC)
            .addParameter(
                ParameterizedTypeName.get(ClassName.get(AssertionCallback::class.java), baseType.typeName),
                "assertionCallback",
                Modifier.FINAL
            )
            .addStatement(
                "return new \$T($builderFieldName.add(assertionCallback))",
                getGeneratedTypeName()
            )
            .returns(getGeneratedTypeName())
            .build()
    )

    private fun getFinisherMethods() = listOf(
        getHardAssertMethod(),
        getSoftAssertMethod()
    )


    private fun getHardAssertMethod() = methodBuilder("assertToFirstFail")
        .addModifiers(Modifier.PUBLIC)
        .addStatement("$builderFieldName.assertToFirstFail()")
        .build()

    private fun getSoftAssertMethod() = methodBuilder("assertAll")
        .addModifiers(Modifier.PUBLIC)
        .addStatement("$builderFieldName.assertAll()")
        .build()

    private fun getInitializers() = listOf(
        getBaseTypeConstructor(),
        getBuilderConstructor(),
        getApiInitializer()
    )

    private fun getApiInitializer() = methodBuilder("prepareFor")
        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
        .addTypeVariables(baseType.typeParameters.map { TypeVariableName.get(it) })
        .addParameter(baseType.typeName, "base", Modifier.FINAL)
        .addStatement("return new \$T(base)", getGeneratedTypeName())
        .returns(getGeneratedTypeName())
        .build()

    private fun getGeneratedTypeName() =
        if (baseType.typeParameters.isEmpty()) generatedTypeNameWithoutParameters()
        else getGeneratedTypeNameWithParameters()

    private fun getGeneratedTypeNameWithParameters() = ParameterizedTypeName.get(
        generatedTypeNameWithoutParameters(),
        *(baseType.typeParameters.map { TypeVariableName.get(it) }.toTypedArray())
    )

    private fun generatedTypeNameWithoutParameters() =
        ClassName.get(baseType.packageElement.toString(), simpleAsserterName)

    private fun getBaseTypeConstructor() = MethodSpec.constructorBuilder()
        .addModifiers(Modifier.PRIVATE)
        .addParameter(baseType.typeName, "base", Modifier.FINAL)
        .addStatement(
            "this(new \$T (base, \$T.emptyList(), \$S))",
            getBuilderFieldType(),
            Collections::class.java,
            baseType.simpleName
        )
        .build()

    private fun getBuilderConstructor() = MethodSpec.constructorBuilder()
        .addModifiers(Modifier.PRIVATE)
        .addParameter(getBuilderFieldType(), builderFieldName, Modifier.FINAL)
        .addStatement("this.$builderFieldName = $builderFieldName")
        .build()

    private val simpleAsserterName = "${baseType.simpleName}Asserter"

    private val builderFieldName = "pojoAssertionBuilder"

    private fun getPojoAssertionBuilderField() = FieldSpec.builder(
        getBuilderFieldType(),
        builderFieldName,
        Modifier.PRIVATE,
        Modifier.FINAL
    ).build()


    private fun getBuilderFieldType() = ParameterizedTypeName.get(
        ClassName.get(PojoAssertionBuilder::class.java),
        baseType.typeName
    )

    private fun getGeneratedAnnotation() = AnnotationSpec.builder(Generated::class.java)
        .addMember("value", "\$S", generationMarker)
        .addMember("date", "\$S", generationTimeStamp())
        .build()

    private val TypeElement.packageElement: PackageElement
        get() = processingEnv.elementUtils.getPackageOf(this)

    private val TypeElement.typeName: TypeName
        get() = TypeName.get(asType())

    private val TypeElement.properties: List<Property>
        get() = enclosedElements
            .filter { it.isProperty }
            .map { it as ExecutableElement }
            .map {
                Property(
                    name = it.simpleName.withoutPropertyPrefix(),
                    type = it.returnType,
                    accessor = it.toString()
                )
            }

    private val Property.boxedType: TypeMirror
        get() =
            if (type is PrimitiveType) processingEnv.typeUtils.boxedClass(type).asType()
            else type

    private fun Name.withoutPropertyPrefix() = toString()
        .replaceFirst(Regex("^((get)|(is))"), "")
        .decapitalize()

    private val Element.isProperty
        get() =
            this is ExecutableElement &&
                    !isPrivate &&
                    hasPropertyPrefix() &&
                    hasReturnType() &&
                    hasNoParameters()

    private fun Element.hasPropertyPrefix() =
        simpleName.startsWith("get") || simpleName.startsWith("is")

    private val Element.isPrivate: Boolean
        get() = modifiers.contains(Modifier.PRIVATE)

    private fun ExecutableElement.hasNoParameters() =
        this.parameters.isEmpty()

    private fun ExecutableElement.hasReturnType() =
        returnType.kind != TypeKind.VOID
}

data class Property(val name: String, val type: TypeMirror, val accessor: String)
