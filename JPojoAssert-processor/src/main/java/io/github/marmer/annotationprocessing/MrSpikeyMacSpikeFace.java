package io.github.marmer.annotationprocessing;

import com.google.auto.service.AutoService;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.Set;

@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes({
        "io.github.marmer.annotationprocessing.DoSomeProcessing"        })
@AutoService(Processor.class)
public class MrSpikeyMacSpikeFace extends AbstractProcessor {
    @Override
    public synchronized void init(final ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        processingEnv.getMessager().printMessage(Diagnostic.Kind.MANDATORY_WARNING, "Wird initialisiert");
    }

    @Override
    public boolean process(final Set<? extends TypeElement> set, final RoundEnvironment roundEnvironment) {
        if(!roundEnvironment.processingOver()) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "WIRD AUSGEFÜHRT. Gefunden: " + roundEnvironment.getElementsAnnotatedWith(DoSomeProcessing.class).toString());
        return set.stream().anyMatch(typeElement -> typeElement.getQualifiedName().toString().equals(DoSomeProcessing.class.getName()));
        }
        return false;
    }
}
