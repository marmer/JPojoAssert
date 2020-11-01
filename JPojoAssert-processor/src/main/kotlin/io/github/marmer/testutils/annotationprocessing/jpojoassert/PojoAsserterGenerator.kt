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
        TypeSpec.classBuilder("${baseType.simpleName}Asserter")
            .addModifiers(Modifier.PUBLIC)
            .addAnnotation(generatedAnnotation())
            .addField(pojoAssertionBuilderField())
            .build()
    ).build()
        .writeTo(processingEnv.filer)

    private fun pojoAssertionBuilderField() = FieldSpec.builder(
        getBuilderFieldType(),
        "pojoAssertionBuilder",
        Modifier.PRIVATE,
        Modifier.FINAL
    ).build()

    private fun getBuilderFieldType() = ParameterizedTypeName.get(
        ClassName.get(PojoAssertionBuilder::class.java),
        TypeName.get(baseType.asType())
    )

    private fun generatedAnnotation() = AnnotationSpec.builder(Generated::class.java)
        .addMember("value", "\$S", javaClass.name)
        .addMember("date", "\$S", generationTimeStamp())
        .build()

    private val TypeElement.packageElement: PackageElement
        get() = processingEnv.elementUtils.getPackageOf(this)
}
