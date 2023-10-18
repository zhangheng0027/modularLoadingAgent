package com.zhangheng.enhance;


import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.AsmVisitorWrapper;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.JavaModule;

import java.security.ProtectionDomain;
import java.util.Set;

public class ModularEnhanceClassFileTransformer implements AgentBuilder.Transformer {

    private final Set<String> modularLoadSet;


    public ModularEnhanceClassFileTransformer(Set<String> ms) {
        modularLoadSet = Set.copyOf(ms);
    }

    public ModularEnhanceClassFileTransformer(String... ms) {
        modularLoadSet = Set.of(ms);
    }

    /**
     * Allows for a transformation of a {@link DynamicType.Builder}.
     *
     * @param builder          The dynamic builder to transform.
     * @param typeDescription  The description of the type currently being instrumented.
     * @param classLoader      The class loader of the instrumented class. Might be {@code null} to represent the bootstrap class loader.
     * @param module           The class's module or {@code null} if the current VM does not support modules.
     * @param protectionDomain The protection domain of the transformed type.
     * @return A transformed version of the supplied {@code builder}.
     */
    @Override
    public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder, TypeDescription typeDescription,
                                            ClassLoader classLoader, JavaModule module, ProtectionDomain protectionDomain) {

        AsmVisitorWrapper.Compound compound = new AsmVisitorWrapper.Compound(
                new ClassVisitorWrapper(modularLoadSet),  // 类访问器
                new AsmVisitorWrapper.ForDeclaredFields().field(ElementMatchers.any(), new FieldVisitorWrapper(modularLoadSet)), // 字段访问器
                new AsmVisitorWrapper.ForDeclaredMethods().method(ElementMatchers.any(), new MethodVisitorWrapper(modularLoadSet)) // 方法访问器
        );

        return builder.visit(compound);
    }
}
