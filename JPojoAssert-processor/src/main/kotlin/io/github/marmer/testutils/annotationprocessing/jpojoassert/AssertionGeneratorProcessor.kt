package io.github.marmer.testutils.annotationprocessing.jpojoassert

import com.google.auto.service.AutoService
import com.squareup.javapoet.*
import com.squareup.javapoet.TypeSpec.classBuilder
import java.time.LocalDate
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.Modifier
import javax.lang.model.element.PackageElement
import javax.lang.model.element.TypeElement


@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes("io.github.marmer.testutils.annotationprocessing.jpojoassert.GenerateAsserter")
@AutoService(Processor::class)
class AssertionGeneratorProcessor : AbstractProcessor() {
    @Synchronized
    override fun init(processingEnvironment: ProcessingEnvironment) = super.init(processingEnvironment)
    override fun process(set: Set<TypeElement>, roundEnvironment: RoundEnvironment): Boolean {
        if (!roundEnvironment.processingOver() && set.containsTypeInfoFor(GenerateAsserter::class.java)) {
            roundEnvironment.getElementsAnnotatedWith(GenerateAsserter::class.java)
                .forEach { generate(it.getAnnotation(GenerateAsserter::class.java)) }
            return true
        }
        return false
    }

    private fun generate(configuration: GenerateAsserter) {
        val baseType = processingEnv.elementUtils.getTypeElement(configuration.value)

        generate(baseType)
    }

    private fun generate(baseType: TypeElement) = JavaFile.builder(
        baseType.packageElement.toString(),
        classBuilder("${baseType.simpleName}Asserter")
            .addModifiers(Modifier.PUBLIC)
            .addAnnotation(generatedAnnotation())
            .addField(pojoAssertionBuilderField(baseType))
            .build()
    ).build()
        .writeTo(processingEnv.filer)

    private fun pojoAssertionBuilderField(baseType: TypeElement): FieldSpec = FieldSpec.builder(
        getBuilderFieldTypeFor(baseType),
        "pojoAssertionBuilder",
        Modifier.PRIVATE,
        Modifier.FINAL
    ).build()

    private fun getBuilderFieldTypeFor(baseType: TypeElement): ParameterizedTypeName? {
        return ParameterizedTypeName.get(
            ClassName.get(PojoAssertionBuilder::class.java),
            TypeName.get(baseType.asType())
        )
    }

    private fun generatedAnnotation(): AnnotationSpec? {
        return AnnotationSpec.builder(Generated::class.java)
            .addMember("value", "\$S", javaClass.name)
            .addMember("date", "\$S", LocalDate.now())
            .build()
    }


    private val TypeElement.packageElement: PackageElement
        get() = processingEnv.elementUtils.getPackageOf(this)

}

private fun String.toPackageName(): String =
    removeRange(lastIndexOf("."), length)

private fun <T> Iterable<TypeElement>.containsTypeInfoFor(type: Class<T>): Boolean =
    any { it.qualifiedName.toString() == type.name }
