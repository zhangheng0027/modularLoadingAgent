package com.zhangheng.enhance;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.agent.builder.ResettableClassFileTransformer;
import net.bytebuddy.asm.AsmVisitorWrapper;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.instrument.Instrumentation;

public class PreMain {



    public static void premain(String arg, Instrumentation instrumentation) {
        System.out.println("premain start arg " + arg);

        ArgEntity argEntity = ArgEntity.of(arg);

        System.out.println("premain envs is " + String.join(", ", argEntity.getEnvs()));

        ModularEnhanceClassFileTransformer modularEnhangeClassFileTransformer = new ModularEnhanceClassFileTransformer(argEntity.getEnvs());
        ResettableClassFileTransformer resettableClassFileTransformer = new AgentBuilder.Default()
                .type(argEntity.getAgentBuilder())
                .transform(modularEnhangeClassFileTransformer)
                .installOn(instrumentation);


    }



}
