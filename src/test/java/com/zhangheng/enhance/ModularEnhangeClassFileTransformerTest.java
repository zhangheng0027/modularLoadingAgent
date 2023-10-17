package com.zhangheng.enhance;

import jdk.jfr.Name;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.asm.AsmVisitorWrapper;
import net.bytebuddy.matcher.ElementMatchers;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.NotBlank;
import org.junit.jupiter.api.Test;

import java.beans.JavaBean;
import java.io.File;
import java.io.IOException;

class ModularEnhanceClassFileTransformerTest {


    @Test
    public void test01() throws IOException {


        AsmVisitorWrapper.Compound compound = new AsmVisitorWrapper.Compound(
                new ClassVisitorWrapper("test"),  // 类访问器
                new AsmVisitorWrapper.ForDeclaredFields().field(ElementMatchers.any(), new FieldVisitorWrapper("test")), // 字段访问器
                new AsmVisitorWrapper.ForDeclaredMethods().method(ElementMatchers.any(), new MethodVisitorWrapper("test")) // 方法访问器
        );


        ByteBuddy byteBuddy = new ByteBuddy();
        byteBuddy.decorate(Base.class).visit(compound)
//                .field()
                .make().saveIn(new File("D:\\tmp\\test"));

//        load.getBytes() save
    }


    @ModularLoad(env = {"dev"}, annotation = {JavaBean.class})
    @ModularLoad(env = {"test"}, annotation = {JavaBean.class})
    @JavaBean
    public static class Base {


        @ModularLoad(env = {"dev"}, annotation = {NotBlank.class})
        @NotBlank
        private String name;

        @ModularLoad(env = {"test1"}, annotation = {Name.class})
        @Name("AA")
        public void AA() {

        }

    }


}