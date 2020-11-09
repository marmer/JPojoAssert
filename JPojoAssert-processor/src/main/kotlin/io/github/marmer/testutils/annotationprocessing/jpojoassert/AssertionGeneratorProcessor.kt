package io.github.marmer.testutils.annotationprocessing.jpojoassert

import com.google.auto.service.AutoService
import java.time.LocalDateTime
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement

@SupportedAnnotationTypes(
    "io.github.marmer.testutils.annotationprocessing.jpojoassert.GenerateAsserter",
    "javax.annotation.processing.Generated"
)
@AutoService(Processor::class)
class AssertionGeneratorProcessor(private val timeProvider: () -> LocalDateTime = LocalDateTime::now) :
    AbstractProcessor() {

    @Synchronized
    override fun init(processingEnvironment: ProcessingEnvironment) = super.init(processingEnvironment)
    override fun process(set: Set<TypeElement>, roundEnvironment: RoundEnvironment): Boolean {
        if (!roundEnvironment.processingOver()) {
            if (set.containsGenerationAsserter<GenerateAsserter>()) {
                roundEnvironment.getElementsAnnotatedWith(GenerateAsserter::class.java)
                    .forEach {
                        val baseType =
                            processingEnv.elementUtils.getTypeElement(it.getAnnotation(GenerateAsserter::class.java).value)

                        PojoAsserterGenerator(processingEnv, baseType, timeProvider, javaClass.name).generate()
                    }

            }
            // TODO: marmer 09.11.2020 Currently "generated" is also claimed if someone else has generated it. Take only care of your own!
            return true
        }
        return false
    }


    override fun getSupportedSourceVersion() = SourceVersion.latestSupported()
}

private inline fun <reified T> Set<TypeElement>.containsGenerationAsserter() =
    this.find { T::class.qualifiedName == it.qualifiedName.toString() } != null

