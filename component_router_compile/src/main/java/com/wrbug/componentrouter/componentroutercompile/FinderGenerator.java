package com.wrbug.componentrouter.componentroutercompile;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.wrbug.componentrouter.ComponentRouterProxy;
//import com.wrbug.componentrouter.ComponentRouterFinder;

import java.util.Set;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

import static com.wrbug.componentrouter.componentroutercompile.Constant.*;

public class FinderGenerator implements Generator {
    private Filer mFiler;
    private Set<? extends Element> mElements;

    public FinderGenerator(Filer filer, Set<? extends Element> elements) {
        mFiler = filer;
        mElements = elements;
    }

    @Override
    public void generate() {
        TypeSpec.Builder builder = TypeSpec.classBuilder("ComponentRouterFinder")
                .addModifiers(Modifier.PUBLIC)
                .addJavadoc("Generated code from Treasure. Do not modify!");
        MethodSpec.Builder getMethodBuilder = MethodSpec.methodBuilder("get")
                .addModifiers(Modifier.STATIC, Modifier.PUBLIC)
                .addAnnotation(ClassName.get("android.support.annotation","Nullable"))
                .returns(ComponentRouterProxy.class)
                .addParameter(Object.class, "obj");

        for (Element element : mElements) {
            if (element instanceof TypeElement) {
                TypeElement typeElement = (TypeElement) element;
                final String name = typeElement.getQualifiedName().toString() + SUFFIX;
                final String packageName = name.substring(0, name.lastIndexOf("."));
                final String className = name.substring(packageName.length() + 1);
                final ClassName classType = ClassName.get(packageName, className);
                getMethodBuilder.beginControlFlow("if ($T.is(obj))", classType)
                        .addCode("return new $T(($L)obj);", classType,typeElement.getQualifiedName().toString())
                        .endControlFlow();
            }
        }
        getMethodBuilder.addStatement("return null");
        builder.addMethod(getMethodBuilder.build());
        JavaFile javaFile = JavaFile.builder("com.wrbug.componentrouter", builder.build()).build();
        try {
            javaFile.writeTo(mFiler);
        } catch (Exception e) {
        }
    }
}
