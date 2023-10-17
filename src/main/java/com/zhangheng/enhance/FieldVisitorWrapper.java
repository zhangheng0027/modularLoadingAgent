package com.zhangheng.enhance;

import net.bytebuddy.asm.AsmVisitorWrapper;
import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.jar.asm.AnnotationVisitor;
import net.bytebuddy.jar.asm.FieldVisitor;
import net.bytebuddy.jar.asm.Opcodes;

import java.util.Map;
import java.util.Set;

public class FieldVisitorWrapper implements AsmVisitorWrapper.ForDeclaredFields.FieldVisitorWrapper {



    private final Set<String> env;

    public FieldVisitorWrapper(String... env) {
        this.env = Set.of(env);
    }

    public FieldVisitorWrapper(Set<String> env) {
        this.env = Set.copyOf(env);
    }




    /**
     * Wraps a field visitor.
     *
     * @param instrumentedType The instrumented type.
     * @param fieldDescription The field that is currently being defined.
     * @param fieldVisitor     The original field visitor that defines the given field.
     * @return The wrapped field visitor.
     */
    @Override
    @SuppressWarnings("DuplicatedCode")
    public FieldVisitor wrap(TypeDescription instrumentedType, FieldDescription.InDefinedShape fieldDescription, FieldVisitor fieldVisitor) {

        final Map<String, ModularLoadModel> cache = ClassVisitorWrapper.handleTypeDescription(fieldDescription.getDeclaredAnnotations());
        if (cache.isEmpty())
            return fieldVisitor;


        return new FieldVisitor(Opcodes.ASM8, fieldVisitor) {
            public AnnotationVisitor visitAnnotation(final String descriptor, final boolean visible) {
                if (cache.containsKey(descriptor) && cache.get(descriptor).isIntersection(env)) {
                    return null;
                }
                return super.visitAnnotation(descriptor, visible);
            }
        };
    }

}
