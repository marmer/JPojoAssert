package io.github.marmer.testutils.annotationprocessing.jpojoassert

import java.time.LocalDateTime
import javax.annotation.processing.Generated
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.AnnotationMirror
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic
import kotlin.reflect.KClass

class AssertionGeneratorProcessorWorker(
    private val timeProvider: () -> LocalDateTime,
    private val processingEnv: ProcessingEnvironment,
    private val generatorName: String
) {
    fun process(set: Set<TypeElement>, roundEnvironment: RoundEnvironment): Boolean {
        if (roundEnvironment.processingOver()) {
            return false
        }

        return if (set.contains<GenerateAsserter>()) {
            roundEnvironment.getElementsAnnotatedWith(GenerateAsserter::class.java)
                .forEach { generate(it, roundEnvironment) }
            true
        } else set.contains<Generated>() &&
                roundEnvironment.existsAnySelfGeneratedSource()
    }

    private fun generate(configurationType: Element, roundEnvironment: RoundEnvironment) {
        getAllTypeElementsFor(configurationType)
            .forEach {
                if (it.isSelfGenerated()) {
                    printSkipNoteBecauseOfSelfGenerationFor(it)
                } else {
                    PojoAsserterGenerator(
                        processingEnv,
                        it,
                        timeProvider,
                        generatorName,
                        getTypesWithAsserters(roundEnvironment)
                    ).generate()
                }
            }
    }

    private fun getTypesWithAsserters(roundEnvironment: RoundEnvironment): Collection<TypeElement> =
        roundEnvironment.getElementsAnnotatedWith(GenerateAsserter::class.java)
            .flatMap { getAllTypeElementsFor(it) }
            .flatMap { expandToNestingTypes(it) }
            .flatMap { expandToNestedTypes(it) }
            .distinct()

    private fun expandToNestingTypes(typeElement: TypeElement): List<TypeElement> =
        if (typeElement.enclosingElement is TypeElement)
            listOf(typeElement) + expandToNestingTypes(typeElement.enclosingElement as TypeElement)
        else
            listOf(typeElement)

    private fun expandToNestedTypes(typeElement: TypeElement): List<TypeElement> =
        listOf(typeElement) +
                typeElement.enclosedElements
                    .filterIsInstance(TypeElement::class.java)
                    .filterNot(TypeElement::isPrivate)
                    .flatMap { expandToNestedTypes(it) }


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
            .plus(
                getHighestNestingType(
                    processingEnv
                        .elementUtils
                        .getTypeElement(currentQualifiedTypeOrPackageName)
                )
            ).filterNotNull()

        if (typeElementsForName.isEmpty())
            printSkipWarningBecauseOfNotExistingTypeConfigured(configurationType, currentQualifiedTypeOrPackageName)

        return typeElementsForName
    }

    private fun printSkipWarningBecauseOfNotExistingTypeConfigured(
        configurationClass: Element,
        qualifiedTypeOrPackageName: String
    ) {
        configurationClass.annotationMirrors
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

    private fun AnnotationMirror.getAnnotationValueForField(fieldName: String) =
        elementValues.get(elementValues.keys.first { it.simpleName.contentEquals(fieldName) })

    private fun printSkipNoteBecauseOfSelfGenerationFor(baseType: TypeElement) {
        baseType.annotationMirrors
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
            .any { it.isSelfGenerated() }

    fun getSupportedSourceVersion() = SourceVersion.latestSupported()

    private fun Element.isSelfGenerated(): Boolean =
        getAnnotation(Generated::class.java)
            .let {
                it != null && it.value.any { value -> value == generatorName }
            }

    private fun getHighestNestingType(typeElement: TypeElement?): TypeElement? =
        if (typeElement != null && typeElement.enclosingElement is TypeElement)
            getHighestNestingType(typeElement.enclosingElement as TypeElement)
        else
            typeElement

    private fun AnnotationMirror.isTypeOf(type: KClass<out Annotation>) =
        annotationType.asElement().toString() == type.qualifiedName

    private inline fun <reified T> Set<TypeElement>.contains() =
        this.find { T::class.qualifiedName == it.qualifiedName.toString() } != null
}


