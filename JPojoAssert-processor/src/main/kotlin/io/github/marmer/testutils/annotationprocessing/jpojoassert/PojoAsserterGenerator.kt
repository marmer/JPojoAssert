package io.github.marmer.testutils.annotationprocessing.jpojoassert

import com.squareup.javapoet.*
import java.time.LocalDateTime
import javax.annotation.processing.Generated
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Modifier
import javax.lang.model.element.PackageElement
import javax.lang.model.element.TypeElement

class PojoAsserterGenerator(
    private val processingEnv: ProcessingEnvironment,
    private val baseType: TypeElement,
    private val generationTimeStamp: () -> LocalDateTime
) {
    fun generate() = JavaFile.builder(
        baseType.packageElement.toString(),
        TypeSpec.classBuilder(getSimpleAsserterName())
            .addModifiers(Modifier.PUBLIC)
            .addAnnotation(getGeneratedAnnotation())
            .addField(getPojoAssertionBuilderField())
            .addMethods(getInitializers())
            .build()
    ).build()
        .writeTo(processingEnv.filer)

    private fun getInitializers() = listOf(
        getBaseTypeconstructor()
    )

    private fun getBaseTypeconstructor(): MethodSpec {
        return MethodSpec.constructorBuilder()
            .addModifiers(Modifier.PRIVATE)
            .addParameter(baseType.typeName, "base", Modifier.FINAL)
            .addStatement(
                "this(new \$T (base, emptyList(), \$S))",
                getBuilderFieldType(),
                baseType.simpleName
            )
            .build()
    }

    private fun getSimpleAsserterName() = "${baseType.simpleName}Asserter"


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
        .addMember("value", "\$S", javaClass.name)
        .addMember("date", "\$S", generationTimeStamp())
        .build()

    private val TypeElement.packageElement: PackageElement
        get() = processingEnv.elementUtils.getPackageOf(this)

    private val TypeElement.typeName: TypeName
        get() = TypeName.get(asType())
}
