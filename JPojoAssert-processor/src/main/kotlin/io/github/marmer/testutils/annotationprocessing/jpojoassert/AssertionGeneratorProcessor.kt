package io.github.marmer.testutils.annotationprocessing.jpojoassert

import com.google.auto.common.AnnotationMirrors
import com.google.auto.service.AutoService
import java.time.LocalDateTime
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

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
        if (roundEnvironment.processingOver()) {
            return false
        }

        return if (set.contains<GenerateAsserter>()) {
            roundEnvironment.getElementsAnnotatedWith(GenerateAsserter::class.java)
                .forEach {
                    val annotation = it.getAnnotation(GenerateAsserter::class.java)
                    val configuredTypeName = annotation.value
                    val baseType =
                        processingEnv.elementUtils.getTypeElement(configuredTypeName)

                    if (baseType.getAnnotation(Generated::class.java) == null) {
                        PojoAsserterGenerator(processingEnv, baseType, timeProvider, javaClass.name).generate()
                    } else {

                        val annotationMirrors =
                            AnnotationMirrors.getAnnotatedAnnotations(it, GenerateAsserter::class.java)

                        // FIXME: marmer 11.11.2020 CLeanup and make it less error prone
                        // TODO: marmer 11.11.2020 Test what happens if the Annotation was enclosed by others

                        val generatedAnnotationMirror =
                            baseType.annotationMirrors.filter { it.annotationType.toString() == Generated::class.qualifiedName }
                                .first()
                        processingEnv.messager.printMessage(
                            Diagnostic.Kind.NOTE,
                            "Generation skipped for: '$configuredTypeName' because is is already generated by this processor",
                            baseType,
                            generatedAnnotationMirror,
                            AnnotationMirrors.getAnnotationValue(generatedAnnotationMirror, "value")
                        )
                    }
                }
            true
        } else set.contains<Generated>() &&
                roundEnvironment.existsAnySelfGeneratedSource()
    }

    private fun RoundEnvironment.existsAnySelfGeneratedSource() =
        getElementsAnnotatedWith(Generated::class.java)
            .any { it.getAnnotation(Generated::class.java).value.contains(this@AssertionGeneratorProcessor.javaClass.name) }


    override fun getSupportedSourceVersion() = SourceVersion.latestSupported()
}

private inline fun <reified T> Set<TypeElement>.contains() =
    this.find { T::class.qualifiedName == it.qualifiedName.toString() } != null

