package io.github.marmer.testutils.annotationprocessing.jpojoassert

import com.google.auto.service.AutoService
import java.time.LocalDateTime
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement

@SupportedAnnotationTypes("io.github.marmer.testutils.annotationprocessing.jpojoassert.GenerateAsserter")
@AutoService(Processor::class)
class AssertionGeneratorProcessor(private val timeProvider: () -> LocalDateTime = LocalDateTime::now) :
    AbstractProcessor() {

    @Synchronized
    override fun init(processingEnvironment: ProcessingEnvironment) = super.init(processingEnvironment)
    override fun process(set: Set<TypeElement>, roundEnvironment: RoundEnvironment): Boolean {
        if (!roundEnvironment.processingOver() && set.isNotEmpty()) {
            roundEnvironment.getElementsAnnotatedWith(GenerateAsserter::class.java)
                .forEach {
                    val baseType =
                        processingEnv.elementUtils.getTypeElement(it.getAnnotation(GenerateAsserter::class.java).value)

                    PojoAsserterGenerator(processingEnv, baseType, timeProvider, javaClass.name).generate()
                }
            return true
        }
        return false
    }


    override fun getSupportedSourceVersion() = SourceVersion.latestSupported()
}

