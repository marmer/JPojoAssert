package io.github.marmer.testutils.annotationprocessing.jpojoassert

import com.squareup.javapoet.*
import java.time.LocalDateTime
import java.util.*
import javax.annotation.processing.Generated
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.*
import javax.lang.model.type.PrimitiveType
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
            .map { property ->
                MethodSpec.methodBuilder("with${property.name.capitalize()}")
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
                        "return new $simpleAsserterName($builderFieldName.add(base -> assertionCallback.accept(base.${property.accessor})))"
                    )
                    .returns(ClassName.get(baseType.packageElement.toString(), simpleAsserterName))
                    .build()
            }

    private fun getBaseAssertionMethods() = listOf(
        MethodSpec.methodBuilder("with")
            .addModifiers(Modifier.PUBLIC)
            .addParameter(
                ParameterizedTypeName.get(ClassName.get(AssertionCallback::class.java), baseType.typeName),
                "assertionCallback",
                Modifier.FINAL
            )
            .addStatement(
                "return new $simpleAsserterName($builderFieldName.add(assertionCallback))"
            )
            .returns(ClassName.get(baseType.packageElement.toString(), simpleAsserterName))
            .build()
    )

    private fun getFinisherMethods() = listOf(
        getHardAssertMethod(),
        getSoftAssertMethod()
    )


    private fun getHardAssertMethod() = MethodSpec.methodBuilder("assertToFirstFail")
        .addModifiers(Modifier.PUBLIC)
        .addStatement("$builderFieldName.assertToFirstFail()")
        .build()

    private fun getSoftAssertMethod() = MethodSpec.methodBuilder("assertAll")
        .addModifiers(Modifier.PUBLIC)
        .addStatement("$builderFieldName.assertAll()")
        .build()

    private fun getInitializers() = listOf(
        getBaseTypeConstructor(),
        getBuilderConstructor(),
        getApiInitializer()
    )

    private fun getApiInitializer() = MethodSpec.methodBuilder("prepareFor")
        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
        .addParameter(baseType.typeName, "base", Modifier.FINAL)
        .addStatement("return new \$L(base)", simpleAsserterName)
        .returns(ClassName.get(baseType.packageElement.toString(), simpleAsserterName))
        .build()

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
            .map { it as ExecutableElement }
            .map {
                Property(
                    it.simpleName.withoutPropertyPrefix(),
                    it.returnType,
                    it.toString()
                )
            }

    private val TypeMirror.simpleName: String
        get() = processingEnv.typeUtils.asElement(this).simpleName.toString()

    private val Property.boxedType: TypeMirror
        get() = if (type.kind.isPrimitive) processingEnv.typeUtils.boxedClass(type as PrimitiveType).asType() else type

    private fun Name.withoutPropertyPrefix() =
        toString()
            .replace("get", "")
            .mapIndexed { index, c -> if (index == 0) c.toLowerCase() else c }
            .joinToString("")
}

data class Property(val name: String, val type: TypeMirror, val accessor: String)
