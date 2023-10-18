package com.zhangheng.enhance;

import net.bytebuddy.description.NamedElement;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ArgEntity {

    private Set<String> envs = new HashSet<>();

    private List<String> classMatches = new ArrayList<>();

    public Set<String> getEnvs() {
        return Set.copyOf(envs);
    }

    private ArgEntity() {}

    public static final ArgEntity of(String arg) {
        ArgEntity argEntity = new ArgEntity();
        if (null == arg || arg.isEmpty())
            return argEntity;

        String[] split = arg.split(";");
        if (split.length == 1) {
            return argEntity.handleEvns(split[0]);
        }

        for (String s : split) {
            String[] split1 = s.split("=");
            if (split1.length != 2)
                continue;

            switch (split1[0].toLowerCase()) {
                case "env", "envs" -> argEntity.handleEvns(split1[1]);
                case "classmatches" -> argEntity.handleClassMatches(split1[1]);
                default -> {
                }
            }

        }

        return argEntity;
    }

    private void handleClassMatches(String packages) {
        String[] split = packages.split(",");
        this.classMatches.addAll(List.of(split));
    }

    private ArgEntity handleEvns(String envs) {
        String[] env = envs.split(",");
        this.envs.addAll(Set.of(env));
        return this;
    }

    public ElementMatcher.Junction<NamedElement> getAgentBuilder() {
        ElementMatcher.Junction<NamedElement> matcher = ElementMatchers.not(ElementMatchers.nameStartsWith("java"))
                // 排除 sun 包
                .and(ElementMatchers.not(ElementMatchers.nameStartsWith("sun")))
                // 排除 net.bytebuddy 包
                .and(ElementMatchers.not(ElementMatchers.nameStartsWith("net.bytebuddy")))
                .and(ElementMatchers.not(ElementMatchers.nameStartsWith("jdk")))
                .and(ElementMatchers.not(ElementMatchers.nameStartsWith("com.zhangheng.enhance")));

        for (String aPackage : classMatches) {
            matcher = matcher.and(ElementMatchers.nameMatches(aPackage));
        }

        return matcher;

    }

}
