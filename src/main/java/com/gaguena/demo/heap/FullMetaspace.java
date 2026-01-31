package com.gaguena.demo.heap;

import java.util.ArrayList;
import java.util.List;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.MethodDelegation;

public class FullMetaspace {

    public static void fill() {

        int targetClasses = 6000; // API MEDIA, total de classes(realista)
        List<ClassLoader> loaders = new ArrayList<>();

        try {
            for (int i = 0; i < targetClasses; i++) {

                // Um ClassLoader por classe -> impede GC do Metaspace
                ClassLoader cl = new ClassLoader() {};
                loaders.add(cl);

                DynamicType.Builder<?> builder =
                        new ByteBuddy()
                                .subclass(Object.class)
                                .name("com.fake.GeneratedClass" + i);

                // === Campos (DTOs / Entities / Projections) ===
                for (int f = 0; f < 10; f++) {
                    builder = builder.defineField(
                            "field" + f,
                            String.class,
                            Visibility.PRIVATE
                    );
                }

                // === Métodos (Services / KafkaListeners / Proxies) ===
                for (int m = 0; m < 25; m++) {
                    builder = builder
                            .defineMethod(
                                    "method" + m,
                                    String.class,
                                    Visibility.PUBLIC
                            )
                            .withParameters(String.class)
                            .intercept(MethodDelegation.to(MethodBody.class));
                }

                // === Annotation (Spring pesa MUITO aqui) ===
                builder = builder.annotateType(
                        AnnotationDescription.Builder
                                .ofType(Deprecated.class)
                                .build()
                );

                builder.make()
                       .load(cl, ClassLoadingStrategy.Default.INJECTION);

                if (i % 500 == 0) {
                    System.out.println("Classes carregadas: " + i);
                }
            }

            System.out.println("FINALIZADO. Total de classes: " + targetClasses);

        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}