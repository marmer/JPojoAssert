package io.github.marmer.testutils.annotationprocessing.jpojoassert;

import com.google.auto.service.AutoService;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.function.Supplier;

@SupportedAnnotationTypes(
        {"io.github.marmer.testutils.annotationprocessing.jpojoassert.GenerateAsserter",
                "javax.annotation.processing.Generated"}
)

@AutoService(Processor.class)
public class AssertionGeneratorProcessor extends AbstractProcessor {
    private final Supplier<LocalDateTime> timeProvider;
    private AssertionGeneratorProcessorWorker worker;

    public AssertionGeneratorProcessor() {
        this(LocalDateTime::now);
    }

    public AssertionGeneratorProcessor(final Supplier<LocalDateTime> timeProvider) {
        this.timeProvider = timeProvider;
    }

    @Override
    public synchronized void init(final ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.worker = new AssertionGeneratorProcessorWorker(timeProvider::get, processingEnv, getClass().getName());
    }

    @Override
    public boolean process(final Set<? extends TypeElement> set, final RoundEnvironment roundEnvironment) {
        return worker.process(set, roundEnvironment);
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return this.worker.getSupportedSourceVersion();
    }
}
