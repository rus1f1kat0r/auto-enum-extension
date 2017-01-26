package ru.rus1f1kat0r.autoenum.processor;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

import ru.rus1f1kat0r.autoenum.annotations.AutoEnum;

@AutoService(Processor.class)
public class AutoEnumExtensionProcessor extends AbstractProcessor {

    private Messager messager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        messager = processingEnvironment.getMessager();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton(AutoEnum.class.getCanonicalName());
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        for (Element annotatedElement : roundEnvironment.getElementsAnnotatedWith(AutoEnum.class)) {
            if (annotatedElement.getKind() == ElementKind.INTERFACE) {
                TypeElement type = (TypeElement) annotatedElement;
                AutoEnum annotation = type.getAnnotation(AutoEnum.class);
                String[] values = annotation.value();
                String[] names = annotation.declaredNames();
                if (names.length == 0) {
                    names = new String[values.length];
                    for (int i = 0; i < values.length; i ++) {
                        String value = values[i];
                        names[i] = value.toUpperCase();
                    }
                } else if (names.length != values.length) {
                    messager.printMessage(
                            Diagnostic.Kind.ERROR,
                            "size of declared names should be same as size of values", annotatedElement);
                    return false;
                }
                PackageElement packageOf = processingEnv.getElementUtils().getPackageOf(annotatedElement);
                if (packageOf.isUnnamed()) {
                    messager.printMessage(Diagnostic.Kind.ERROR,
                            "package of element is undefined", annotatedElement);
                    return false;
                }
                String pckgName = packageOf.getQualifiedName().toString();
                String name = annotation.name();
                if (name.length() == 0) {
                    name = "AutoEnum" + annotatedElement.getSimpleName().toString();
                }
                TypeVariableName R = TypeVariableName.get("R");
                TypeVariableName P = TypeVariableName.get("P");
                ParameterizedTypeName visitorType = ParameterizedTypeName.get(ClassName.get(pckgName, name + "Visitor"), R, P);
                TypeSpec.Builder visitorBuilder = TypeSpec.interfaceBuilder(name + "Visitor")
                        .addModifiers(Modifier.PUBLIC)
                        .addTypeVariable(R)
                        .addTypeVariable(P);
                ClassName enumType = ClassName.get(pckgName, name);
                TypeSpec.Builder builder = TypeSpec.enumBuilder(name)
                        .addModifiers(Modifier.PUBLIC)
                        .addSuperinterface(TypeName.get(annotatedElement.asType()))
                        .addField(TypeName.get(String.class), "label", Modifier.PRIVATE, Modifier.FINAL)
                        .addMethod(MethodSpec.constructorBuilder()
                                .addParameter(TypeName.get(String.class), "label")
                                .addStatement("this.label = label")
                                .build())
                        .addMethod(MethodSpec.methodBuilder("accept")
                                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                                .addTypeVariable(R)
                                .addTypeVariable(P)
                                .returns(R)
                                .addParameter(ParameterSpec.builder(visitorType, "visitor").build())
                                .addParameter(P, "param")
                                .build())
                        .addMethod(MethodSpec.methodBuilder("valueOfLabel")
                                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                                .returns(enumType)
                                .addParameter(TypeName.get(String.class), "label")
                                .addCode(CodeBlock.builder()
                                        .beginControlFlow("for ($T each : values())", enumType)
                                            .beginControlFlow("if (each.label.equalsIgnoreCase(label))")
                                                .addStatement("return each")
                                            .endControlFlow()
                                        .endControlFlow()
                                        .addStatement("return null")
                                    .build())
                                .build());

                for (int i = 0; i < values.length; i ++) {
                    visitorBuilder.addMethod(MethodSpec.methodBuilder(names[i].toLowerCase())
                            .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                            .returns(R)
                            .addParameter(P, "param")
                            .build());
                    builder.addEnumConstant(names[i], TypeSpec.anonymousClassBuilder("$S", values[i])
                            .addMethod(MethodSpec.methodBuilder("accept")
                                    .addModifiers(Modifier.PUBLIC)
                                    .addTypeVariable(R)
                                    .addTypeVariable(P)
                                    .returns(R)
                                    .addParameter(
                                            ParameterSpec.builder(visitorType, "visitor").build())
                                    .addParameter(P, "param")
                                    .addStatement("return visitor." + names[i].toLowerCase() + "(param)")
                                    .build())
                            .build());
                }
                TypeSpec enumSpec = builder
                        .addType(visitorBuilder.build())
                        .build();
                try {
                    JavaFile.builder(pckgName, enumSpec)
                            .build()
                            .writeTo(processingEnv.getFiler());
                } catch (IOException e) {
                    messager.printMessage(Diagnostic.Kind.ERROR,
                            "Exception while writing src file" + e.getLocalizedMessage(), annotatedElement);
                    e.printStackTrace();
                }
            } else {
                messager.printMessage(Diagnostic.Kind.ERROR,
                        "AutoEnum should be used for interfaces only", annotatedElement);
                return false;
            }

        }
        return false;
    }
}
