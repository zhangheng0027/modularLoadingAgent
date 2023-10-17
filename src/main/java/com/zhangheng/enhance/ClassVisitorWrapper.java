package com.zhangheng.enhance;

import net.bytebuddy.asm.AsmVisitorWrapper;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.annotation.AnnotationList;
import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.field.FieldList;
import net.bytebuddy.description.method.MethodList;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.jar.asm.AnnotationVisitor;
import net.bytebuddy.jar.asm.ClassVisitor;
import net.bytebuddy.jar.asm.Opcodes;
import net.bytebuddy.pool.TypePool;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ClassVisitorWrapper implements AsmVisitorWrapper {

    final Set<String> modularLoadSet;

    public ClassVisitorWrapper(String... modularLoadSet) {
        this.modularLoadSet = Set.of(modularLoadSet);
    }

    public ClassVisitorWrapper(Set<String> modularLoadSet) {
        this.modularLoadSet = Set.copyOf(modularLoadSet);
    }

    @Override
    public int mergeWriter(int flags) {
        return 0;
    }

    @Override
    public int mergeReader(int flags) {
        return 0;
    }

    @Override
    @SuppressWarnings("DuplicatedCode")
    public ClassVisitor wrap(TypeDescription instrumentedType, ClassVisitor classVisitor,
                             Implementation.Context implementationContext, TypePool typePool,
                             FieldList<FieldDescription.InDefinedShape> fields, MethodList<?> methods,
                             int writerFlags, int readerFlags) {

        AnnotationList inheritedAnnotations = instrumentedType.getInheritedAnnotations();

        final Map<String, ModularLoadModel> map = Map.copyOf(handleTypeDescription(inheritedAnnotations));

        return new ClassVisitor(Opcodes.ASM8, classVisitor) {

            public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
                if (map.containsKey(descriptor)) {
                    ModularLoadModel modularLoadModel = map.get(descriptor);
                    if (modularLoadModel.isIntersection(modularLoadSet)) {
                        return null;
                    }
                }
                return super.visitAnnotation(descriptor, visible);
            }

        };

    }



    public static Map<String, ModularLoadModel> handleTypeDescription(AnnotationList inheritedAnnotations) {
        final Map<String, ModularLoadModel> cache = new HashMap<>();

        for (AnnotationDescription inheritedAnnotation : inheritedAnnotations) {
            if ("Lcom/zhangheng/enhance/ModularLoad;".equals(inheritedAnnotation.getAnnotationType().getDescriptor())) {

                ModularLoad ml = inheritedAnnotation.prepare(ModularLoad.class).load();

                cache.put("Lcom/zhangheng/enhance/ModularLoad;", new ModularLoadModel(null));

                addMap(cache, ml);
            } else if ("Lcom/zhangheng/enhance/ModularLoad$List;".equals(inheritedAnnotation.getAnnotationType().getDescriptor())) {
                ModularLoad.List ml = inheritedAnnotation.prepare(ModularLoad.List.class).load();

                cache.put("Lcom/zhangheng/enhance/ModularLoad$List;", new ModularLoadModel(null));

                for (ModularLoad modularLoad : ml.value()) {
                    addMap(cache, modularLoad);
                }
            }
        }
        return Map.copyOf(cache);
    }

    private static void addMap(Map<String, ModularLoadModel> map, ModularLoad modularLoad) {
        for (Class<?> clazz : modularLoad.annotation()) {
            String aClass = clazz.descriptorString();
            if (map.containsKey(aClass)) {
                Collections.addAll(map.get(aClass).environment, modularLoad.env());
            } else {
                map.put(aClass, new ModularLoadModel(modularLoad));
            }
        }
    }


}
