package com.zhangheng.enhance;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.agent.builder.ResettableClassFileTransformer;
import net.bytebuddy.asm.AsmVisitorWrapper;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.instrument.Instrumentation;

public class PreMain {

    public static void premain(String arg, Instrumentation instrumentation) {

        System.out.println("start env is " + arg);

        ModularEnhanceClassFileTransformer modularEnhangeClassFileTransformer = new ModularEnhanceClassFileTransformer("");
        ResettableClassFileTransformer resettableClassFileTransformer = new AgentBuilder.Default()
                // 排除 java 包
                .type(ElementMatchers.not(ElementMatchers.nameStartsWith("java"))
                        // 排除 sun 包
                        .and(ElementMatchers.not(ElementMatchers.nameStartsWith("sun")))
                        // 排除 net.bytebuddy 包
                        .and(ElementMatchers.not(ElementMatchers.nameStartsWith("net.bytebuddy"))))
                .transform(modularEnhangeClassFileTransformer)
                .installOn(instrumentation);


    }

}
