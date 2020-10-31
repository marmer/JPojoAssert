package io.github.marmer.testutils.annotationprocessing.jpojoassert

import com.google.auto.service.AutoService
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.TypeSpec
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

            JavaFile.builder(
                "some.other.pck",
                TypeSpec.classBuilder("SomeGeneratedClass")
                    .addModifiers(Modifier.PUBLIC)
                    .build()
            ).build()
                .writeTo(processingEnv.filer)

            return set.stream()
                .anyMatch { typeElement: TypeElement? -> typeElement!!.qualifiedName.toString() == GeneratePojoAsserter::class.java.name }
        }
        return false
    }
}
