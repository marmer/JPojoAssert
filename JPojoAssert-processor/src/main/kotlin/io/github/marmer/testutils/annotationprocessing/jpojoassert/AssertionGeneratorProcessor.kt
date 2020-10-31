package io.github.marmer.testutils.annotationprocessing.jpojoassert

import com.google.auto.service.AutoService
import com.squareup.javapoet.AnnotationSpec
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.TypeSpec
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
            if (set.any { it != null && it.qualifiedName.toString() == GeneratePojoAsserter::class.java.name })
                generate()
            return true
        }
        return false
    }

    private fun generate() {
        JavaFile.builder(
            "some.other.pck",
            TypeSpec.classBuilder("SomeGeneratedClass")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(
                    AnnotationSpec.builder(Generated::class.java)
                        .addMember("value", "\$S", javaClass.name)
                        .addMember("date", "\$S", LocalDate.now())
                        .build()
                )
                .build()
        ).build()
            .writeTo(processingEnv.filer)
    }
}
