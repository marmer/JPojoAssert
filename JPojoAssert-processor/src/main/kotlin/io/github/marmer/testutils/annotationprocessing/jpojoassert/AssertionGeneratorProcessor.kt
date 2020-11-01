package io.github.marmer.testutils.annotationprocessing.jpojoassert

import com.google.auto.service.AutoService
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
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
                .forEach {
                    PojoAsserterGenerator(
                        processingEnv = processingEnv,
                        configuration = it.getAnnotation(GenerateAsserter::class.java)
                    ).generate()
                }
            return true
        }
        return false
    }


}

private fun <T> Iterable<TypeElement>.containsTypeInfoFor(type: Class<T>): Boolean =
    any { it.qualifiedName.toString() == type.name }
