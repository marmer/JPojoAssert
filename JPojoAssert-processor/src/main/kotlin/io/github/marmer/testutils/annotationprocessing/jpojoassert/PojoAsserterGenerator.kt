package io.github.marmer.testutils.annotationprocessing.jpojoassert

import com.squareup.javapoet.*
import java.time.LocalDateTime
import java.util.*
import javax.annotation.processing.Generated
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Modifier
import javax.lang.model.element.PackageElement
import javax.lang.model.element.TypeElement

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
            .addMethods(getFinisherMethods())
            .build()
    ).build()
        .writeTo(processingEnv.filer)

    private fun getBaseAssertionMethods() = listOf(
        MethodSpec.methodBuilder("add")
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

    private fun getHardAssertMethod() = MethodSpec.methodBuilder("assertHardly")
        .addModifiers(Modifier.PUBLIC)
        .addStatement("$builderFieldName.assertHardly()")
        .build()

    private fun getSoftAssertMethod() = MethodSpec.methodBuilder("assertSoftly")
        .addModifiers(Modifier.PUBLIC)
        .addStatement("$builderFieldName.assertSoftly()")
        .build()

    private fun getInitializers() = listOf(
        getBaseTypeConstructor(),
        getBuilderConstructor(),
        getApiInitializer()
    )

    private fun getApiInitializer() = MethodSpec.methodBuilder("assertThat")
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
}
