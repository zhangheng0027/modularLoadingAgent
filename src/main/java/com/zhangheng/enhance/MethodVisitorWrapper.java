package com.zhangheng.enhance;

import net.bytebuddy.asm.AsmVisitorWrapper;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.jar.asm.AnnotationVisitor;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.jar.asm.Opcodes;
import net.bytebuddy.pool.TypePool;

import java.util.Map;
import java.util.Set;

public class MethodVisitorWrapper implements AsmVisitorWrapper.ForDeclaredMethods.MethodVisitorWrapper {

    private final Set<String> env;

    public MethodVisitorWrapper(String... env) {
        this.env = Set.of(env);
    }

    public MethodVisitorWrapper(Set<String> env) {
        this.env = Set.copyOf(env);
    }

    @SuppressWarnings("DuplicatedCode")
    @Override
    public MethodVisitor wrap(TypeDescription instrumentedType, MethodDescription instrumentedMethod, MethodVisitor methodVisitor,
                              Implementation.Context implementationContext, TypePool typePool, int writerFlags, int readerFlags) {

        final Map<String, ModularLoadModel> cache = ClassVisitorWrapper.handleTypeDescription(instrumentedMethod.getDeclaredAnnotations());
        if (cache.isEmpty())
            return methodVisitor;


        return new MethodVisitor(Opcodes.ASM8, methodVisitor) {
            public AnnotationVisitor visitAnnotation(final String descriptor, final boolean visible) {
                if (cache.containsKey(descriptor) && cache.get(descriptor).isIntersection(env)) {
                    return null;
                }
                return super.visitAnnotation(descriptor, visible);
            }
        };

    }
}
