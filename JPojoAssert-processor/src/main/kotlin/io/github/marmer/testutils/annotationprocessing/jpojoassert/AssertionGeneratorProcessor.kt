package io.github.marmer.testutils.annotationprocessing.jpojoassert

import com.google.auto.service.AutoService
import java.time.LocalDateTime
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.AnnotationMirror
import javax.lang.model.element.AnnotationValue
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic
import kotlin.reflect.KClass

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
                .forEach { generate(it) }
            true
        } else set.contains<Generated>() &&
                roundEnvironment.existsAnySelfGeneratedSource()
    }

    private fun generate(configurationType: Element) {
        getAllTypeElementsFor(configurationType)
            .forEach {
                if (it.isAnnotatedWith(Generated::class.java)) {
                    PojoAsserterGenerator(processingEnv, it, timeProvider, javaClass.name).generate()
                } else {
                    printSkipNoteBecauseOfSelfGenerationFor(it)
                }
            }
    }

    private fun getAllTypeElementsFor(configurationType: Element): List<TypeElement> {
        return configurationType
            .getAnnotation(GenerateAsserter::class.java)
            .value
            .distinct()
            .flatMap { getAllTypeElementsFor(it, configurationType) }
            .distinct()
    }

    private fun getAllTypeElementsFor(
        currentQualifiedTypeOrPackageName: String,
        configurationType: Element
    ): List<TypeElement> {
        val typeElementsForName = processingEnv.elementUtils
            .getAllPackageElements(currentQualifiedTypeOrPackageName)
            .flatMap { it.enclosedElements }
            .map { it as TypeElement }
            .plus(processingEnv.elementUtils.getTypeElement(currentQualifiedTypeOrPackageName)).filterNotNull()

        if (typeElementsForName.isEmpty())
            printSkipWarningBecauseOfNotExistingTypeConfigured(configurationType, currentQualifiedTypeOrPackageName)

        return typeElementsForName
    }

    private fun printSkipWarningBecauseOfNotExistingTypeConfigured(
        configurationClass: Element,
        qualifiedTypeOrPackageName: String
    ) {
        configurationClass.getAnnotationMirrors()
            .filter { it.isTypeOf(GenerateAsserter::class) }
            .forEach {
                processingEnv.messager.printMessage(
                    Diagnostic.Kind.MANDATORY_WARNING,
                    "Neither a type nor a type exists for '$qualifiedTypeOrPackageName'",
                    configurationClass,
                    it,
                    it.getAnnotationValueForField("value")
                )
            }
    }

    private fun AnnotationMirror.getAnnotationValueForField(fieldName: String): AnnotationValue? {
        val elementValueKey = elementValues.keys.filter {
            it.simpleName.contentEquals(
                fieldName
            )
        }.first()
        return elementValues.get(elementValueKey)
    }

    private fun printSkipNoteBecauseOfSelfGenerationFor(baseType: TypeElement) {
        baseType.getAnnotationMirrors()
            .filter { it.isTypeOf(Generated::class) }
            .forEach {
                processingEnv.messager.printMessage(
                    Diagnostic.Kind.NOTE,
                    "Generation skipped for: '${baseType.qualifiedName}' because is is already generated by this processor",
                    baseType,
                    it,
                    it.getAnnotationValueForField("value")
                )
            }
    }

    private fun RoundEnvironment.existsAnySelfGeneratedSource() =
        getElementsAnnotatedWith(Generated::class.java)
            .any { it.getAnnotation(Generated::class.java).value.contains(this@AssertionGeneratorProcessor.javaClass.name) }


    override fun getSupportedSourceVersion() = SourceVersion.latestSupported()
}

private fun AnnotationMirror.isTypeOf(type: KClass<out Annotation>) =
    annotationType.asElement().toString() == type.qualifiedName


private fun Element.isAnnotatedWith(annotationType: Class<out Annotation>) =
    getAnnotation(annotationType) == null


private inline fun <reified T> Set<TypeElement>.contains() =
    this.find { T::class.qualifiedName == it.qualifiedName.toString() } != null

