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
            if (set.contains<GenerateAsserter>()) {
                roundEnvironment.getElementsAnnotatedWith(GenerateAsserter::class.java)
                    .forEach {
                        val baseType =
                            processingEnv.elementUtils.getTypeElement(it.getAnnotation(GenerateAsserter::class.java).value)

                        PojoAsserterGenerator(processingEnv, baseType, timeProvider, javaClass.name).generate()
                    }
                return true
            }
            return set.contains<Generated>() &&
                    roundEnvironment.existsAnySelfGeneratedSource()
        }
        return false
    }

    private fun RoundEnvironment.existsAnySelfGeneratedSource() =
        getElementsAnnotatedWith(Generated::class.java)
            .any { it.getAnnotation(Generated::class.java).value.contains(this@AssertionGeneratorProcessor.javaClass.name) }


    override fun getSupportedSourceVersion() = SourceVersion.latestSupported()
}

private inline fun <reified T> Set<TypeElement>.contains() =
    this.find { T::class.qualifiedName == it.qualifiedName.toString() } != null

