package io.github.marmer.annotationprocessing

import com.google.auto.service.AutoService
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes("io.github.marmer.annotationprocessing.DoSomeProcessing")
@AutoService(Processor::class)
class MrSpikeyMacSpikeFace : AbstractProcessor() {
    @Synchronized
    override fun init(processingEnvironment: ProcessingEnvironment) {
        super.init(processingEnvironment)
        processingEnv.messager.printMessage(Diagnostic.Kind.MANDATORY_WARNING, "Wird initialisiert")
    }

    override fun process(set: Set<TypeElement?>, roundEnvironment: RoundEnvironment): Boolean {
        if (!roundEnvironment.processingOver()) {
            processingEnv.messager.printMessage(
                Diagnostic.Kind.MANDATORY_WARNING,
                "WIRD AUSGEFÜHRT. Gefunden: " + roundEnvironment.getElementsAnnotatedWith(
                    DoSomeProcessing::class.java
                ).toString()
            )
            return set.stream()
                .anyMatch { typeElement: TypeElement? -> typeElement!!.qualifiedName.toString() == DoSomeProcessing::class.java.name }
        }
        return false
    }
}
