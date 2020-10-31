package io.github.marmer.testutils.annotationprocessing.jpojoassert

import com.google.auto.service.AutoService
import com.squareup.javapoet.AnnotationSpec
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.TypeSpec.classBuilder
import java.time.LocalDate
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement

@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes("io.github.marmer.testutils.annotationprocessing.jpojoassert.GeneratePojoAsserter")
@AutoService(Processor::class)
class AssertionGeneratorProcessor : AbstractProcessor() {
    @Synchronized
    override fun init(processingEnvironment: ProcessingEnvironment) = super.init(processingEnvironment)

    override fun process(set: Set<TypeElement?>, roundEnvironment: RoundEnvironment): Boolean {
        if (!roundEnvironment.processingOver()) {
            if (set.any { it != null && it.qualifiedName.toString() == GenerateAsserter::class.java.name })
                roundEnvironment.getElementsAnnotatedWith(GenerateAsserter::class.java)
                    .forEach { generate(it.getAnnotation(GenerateAsserter::class.java)) }
            return true
        }
        return false
    }

    private fun generate(configuration: GenerateAsserter) {
        // TODO: marmer 31.10.2020  go on here. The test is prepared
        JavaFile.builder(
            "some.other.pck",
            classBuilder("SomeGeneratedClass")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(generatedAnnotation())
                .build()
        ).build()
            .writeTo(processingEnv.filer)
    }

    private fun generatedAnnotation(): AnnotationSpec? {
        return AnnotationSpec.builder(Generated::class.java)
            .addMember("value", "\$S", javaClass.name)
            .addMember("date", "\$S", LocalDate.now())
            .build()
    }
}
