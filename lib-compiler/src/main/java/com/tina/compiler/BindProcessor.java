package com.tina.compiler;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.tina.annotation.BindView;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

/**
 * @author yxc
 * @date 2018/9/27
 */
public class BindProcessor extends AbstractProcessor {

    Filer filer;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        filer = processingEnv.getFiler();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
//        generateBinding2(annotations, roundEnv);
        generateBinding(roundEnv);
//        test1();
//        test2();
        return false;
    }

    //直接通过BindView入手
    private void generateBinding2(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv){
        for (Element element : roundEnv.getElementsAnnotatedWith(BindView.class)) {
            Element classElement = element.getEnclosingElement();

            Element pacakeElement = classElement.getEnclosingElement();//todo 这一层是否已经到达了 package了

            String packageStr = pacakeElement.toString();
            String classStr = classElement.getSimpleName().toString();

            ClassName className = ClassName.get(packageStr, classStr + "$Binding");

            MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder()
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(ClassName.get(packageStr, classStr), "activity");

            boolean hasBinding = false;
            for (Element enclosedElement : element.getEnclosedElements()) {
                if (enclosedElement.getKind() == ElementKind.FIELD) {
                    BindView bindView = enclosedElement.getAnnotation(BindView.class);
                    if (bindView != null) {
                        hasBinding = true;
                        constructorBuilder.addStatement("activity.$N = activity.findViewById($L)",
                                enclosedElement.getSimpleName(), bindView.value());
                    }
                }
            }
            System.out.println("----------> " + className);

            TypeSpec buildClass = TypeSpec.classBuilder(className)
                    .addModifiers(Modifier.PUBLIC)
                    .addMethod(constructorBuilder.build())
                    .build();

            if (hasBinding) {
                try {
                    JavaFile.builder(packageStr, buildClass)
                            .build()
                            .writeTo(filer);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    //生成 Activity$Binding 类
    private void generateBinding(RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getRootElements()) {
//            List<? extends  Element> encloseElements = element.getEnclosedElements(); //Activity 下的子类 field， method， intenal class
            String packageStr = element.getEnclosingElement().toString();
            String classStr = element.getSimpleName().toString();

            ClassName className = ClassName.get(packageStr, classStr + "$Binding");

            MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder()
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(ClassName.get(packageStr, classStr), "activity");

            boolean hasBinding = false;

            for (Element enclosedElement : element.getEnclosedElements()) {
                if (enclosedElement.getKind() == ElementKind.FIELD) {
                    BindView bindView = enclosedElement.getAnnotation(BindView.class);
                    if (bindView != null) {
                        hasBinding = true;
                        constructorBuilder.addStatement("activity.$N = activity.findViewById($L)",
                                enclosedElement.getSimpleName(), bindView.value());
                    }
                }
            }
            System.out.println("----------> " + className);

            TypeSpec buildClass = TypeSpec.classBuilder(className)
                    .addModifiers(Modifier.PUBLIC)
                    .addMethod(constructorBuilder.build())
                    .build();

            if (hasBinding) {
                try {
                    JavaFile.builder(packageStr, buildClass)
                            .build()
                            .writeTo(filer);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    //todo 测试生成 Test类
    private void test1() {
        ClassName className = ClassName.get("com.tina.butterknife", "Test");
        TypeSpec buildClass = TypeSpec.classBuilder(className).build();

        try {
            JavaFile.builder("com.tina.butterknife", buildClass)
                    .build()
                    .writeTo(filer);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    //todo 硬编码测试
    private void test2() {
        ClassName className = ClassName.get("com.tina.butterknife", "MainActivity$Binding");
        TypeSpec buildClass = TypeSpec.classBuilder(className)
                .addModifiers(Modifier.PUBLIC)
                .addMethod(MethodSpec.constructorBuilder().
                        addModifiers(Modifier.PUBLIC).
                        addParameter(ClassName.get("com.tina.butterknife", "MainActivity"), "activity")
                        .addStatement("activity.textView = activity.findViewById(R.id.textId)")
                        .build())
                .build();

        try {
            JavaFile.builder("com.tina.butterknife", buildClass)
                    .build()
                    .writeTo(filer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton(BindView.class.getCanonicalName());
    }


    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }


}
