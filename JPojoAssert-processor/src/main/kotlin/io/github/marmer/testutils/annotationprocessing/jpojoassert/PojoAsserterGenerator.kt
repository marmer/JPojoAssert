package io.github.marmer.testutils.annotationprocessing.jpojoassert

import com.squareup.javapoet.*
import com.squareup.javapoet.MethodSpec.methodBuilder
import org.hamcrest.Matcher
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import java.time.LocalDateTime
import java.util.*
import java.util.function.Function
import javax.annotation.processing.Generated
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.*
import javax.lang.model.type.DeclaredType
import javax.lang.model.type.PrimitiveType
import javax.lang.model.type.TypeKind
import javax.lang.model.type.TypeMirror



class PojoAsserterGenerator(
    private val processingEnv: ProcessingEnvironment,
    private val baseType: TypeElement,
    private val generationTimeStamp: () -> LocalDateTime,
    private val generationMarker: String,
    private val typesWithAsserters: Collection<TypeElement>
) {
    fun generate() = JavaFile.builder(
        baseType.packageElement.toString(),
        getPreparedTypeSpecBuilder()
            .build()
    ).build()
        .writeTo(processingEnv.filer)

    private fun getPreparedTypeSpecBuilder() = TypeSpec.classBuilder(simpleAsserterName)
        .addOriginatingElement(baseType)
        .addModifiers(Modifier.PUBLIC)
        .addAnnotation(getGeneratedAnnotation())
        .addTypeVariables(baseType.typeParameters.map { TypeVariableName.get(it) })
        .addSuperinterface(getPojoAsserterInterface())
        .addField(getPojoAssertionBuilderField())
        .addMethods(getInitializers())
        .addMethods(getBaseAssertionMethods())
        .addMethods(getPropertyAssertionMethods())
        .addMethods(getFinisherMethods())
        .addTypes(getInnerAsserters())

    private fun getPojoAsserterInterface() =
        ParameterizedTypeName.get(ClassName.get(PojoAsserter::class.java), TypeName.get(baseType.asType()))

    private fun getInnerAsserters(): List<TypeSpec> =
        baseType.enclosedElements
            .filterIsInstance(TypeElement::class.java)
            .filterNot { it.modifiers.contains(Modifier.PRIVATE) }
            .map {
                PojoAsserterGenerator(
                    processingEnv,
                    it,
                    generationTimeStamp,
                    generationMarker,
                    typesWithAsserters
                ).getPreparedTypeSpecBuilder()
                    .addModifiers(Modifier.STATIC)
                    .build()
            }

    private fun getPropertyAssertionMethods() =
        baseType.properties
            .flatMap { property ->
                listOfNotNull(
                    getPlainAssertionMethodFor(property),
                    getEqualAssertionMethodFor(property),
                    getMatcherAssertionMethodFor(property),
                    getReferenceAsserterMethodFor(property)
                )
            }

    private fun getReferenceAsserterMethodFor(property: Property) =
        if (asserterWillExistFor(property)) {
            val propertyType = property.boxedType as DeclaredType
            methodBuilder("has${property.name.capitalize()}")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(
                    ParameterizedTypeName.get(
                        ClassName.get(Function::class.java),
                        if (propertyType.typeArguments.isNotEmpty())
                            ParameterizedTypeName.get(
                                property.asserterName,
                                *propertyType
                                    .typeArguments
                                    .map { TypeName.get(it) }
                                    .toTypedArray())
                        else
                            property.asserterName,
                        ParameterizedTypeName.get(
                            ClassName.get(PojoAsserter::class.java),
                            TypeName.get(propertyType)
                        )
                    ),
                    "asserterFunction",
                    Modifier.FINAL
                )
                .addStatement(
                    "return new \$T($builderFieldName.addAsserter(\$S, base -> asserterFunction.apply(\$T.prepareFor(base.${property.accessor}))))",
                    getGeneratedTypeName(),
                    property.name,
                    property.asserterName
                )
                .returns(getGeneratedTypeName())
                .build()
        } else
            null

    private fun asserterWillExistFor(property: Property): Boolean {
        return typesWithAsserters.contains(property.boxedType.asElement())
    }

    private fun TypeMirror.asElement() = processingEnv.typeUtils.asElement(this)

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
                "return new \$T($builderFieldName.add(\$S, base -> assertionCallback.assertFor(base.${property.accessor})))",
                getGeneratedTypeName(),
                property.name
            )
            .returns(getGeneratedTypeName())
            .build()
    private fun getEqualAssertionMethodFor(property: Property) =
        methodBuilder("has${property.name.capitalize()}")
            .addModifiers(Modifier.PUBLIC)
            .addParameter(
                TypeName.get(property.boxedType),
                "value",
                Modifier.FINAL
            )
            .addStatement(
                "return has${property.name.capitalize()}(\$T.equalTo(value))",
                Matchers::class.java,
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
            .build(),
        methodBuilder("matches")
            .addModifiers(Modifier.PUBLIC)
            .addParameter(
                ParameterizedTypeName.get(
                    ClassName.get(Matcher::class.java),
                    WildcardTypeName.supertypeOf(baseType.typeName)
                ),
                "matcher",
                Modifier.FINAL
            )
            .addStatement(
                "return new \$T($builderFieldName.add(base -> \$T.assertThat(base, matcher)))",
                getGeneratedTypeName(),
                MatcherAssert::class.java
            )
            .returns(getGeneratedTypeName())
            .build(),
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
        if (baseType.typeParameters.isEmpty()) getSimpleAsserterClassName()
        else getSimpleAsserterClassNameWithParameters()

    private fun getSimpleAsserterClassNameWithParameters() = ParameterizedTypeName.get(
        getSimpleAsserterClassName(),
        *(baseType.typeParameters.map { TypeVariableName.get(it) }.toTypedArray())
    )

    private fun getSimpleAsserterClassName() =
        ClassName.get("", simpleAsserterName)

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
        get() = transitiveInheritedElements
            .filter { it.isProperty }
            .distinctBy { it.simpleName }
            .map { it as ExecutableElement }
            .map {
                Property(
                    name = it.simpleName.withoutPropertyPrefix(),
                    type = it.returnType,
                    accessor = it.toString()
                )
            }

    private val TypeElement.transitiveInheritedElements: List<Element>
        get() = if (superclass.kind != TypeKind.NONE && kind != ElementKind.ENUM)
            enclosedElements +
                superclass.asTypeElement().transitiveInheritedElements +
                interfaces.flatMap { it.asTypeElement().transitiveInheritedElements }
        else
            enclosedElements +
                interfaces.flatMap { it.asTypeElement().transitiveInheritedElements }

    private fun TypeMirror.asTypeElement() =
        (processingEnv.typeUtils.asElement(this) as TypeElement)

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

    private fun ExecutableElement.hasNoParameters() =
        this.parameters.isEmpty()

    private fun ExecutableElement.hasReturnType() =
        returnType.kind != TypeKind.VOID

    private val Property.asserterName: ClassName
        get() {
            val type = type.asTypeElement()

            val nestingTypes: List<TypeElement> = type.toNestingTypes()

            return if (nestingTypes.size > 1)
                ClassName.get(
                    processingEnv.elementUtils.getPackageOf(boxedType.asTypeElement()).toString(),
                    "${nestingTypes[0].simpleName}Asserter",
                    *nestingTypes
                        .subList(1, nestingTypes.size)
                        .map { "${it.simpleName}Asserter" }
                        .toTypedArray()
                )
            else
                ClassName.get(
                    processingEnv.elementUtils.getPackageOf(boxedType.asTypeElement()).toString(),
                    "${type.simpleName}Asserter"
                )
        }
}

private fun TypeElement.toNestingTypes(): List<TypeElement> =
    if (this.enclosingElement is TypeElement)
        (enclosingElement as TypeElement).toNestingTypes() + this
    else
        listOf(this)

private data class Property(val name: String, val type: TypeMirror, val accessor: String)

internal val Element.isPrivate: Boolean
    get() = modifiers.contains(Modifier.PRIVATE)
