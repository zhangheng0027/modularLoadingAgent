package com.zhangheng.enhance;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ModularLoadModel {

    public ModularLoadModel(ModularLoad modularLoad) {

        if (null == modularLoad)
            return;
        Collections.addAll(environment, modularLoad.env());

    }

    // 依赖的环境
    public Set<String> environment = new HashSet<>();


    /**
     * 判断是否有交集
     * @param set 注解所依赖的环境
     * @return true 无交集
     */
    public boolean isIntersection(Set<String> set) {
        for (String s : set) {
            if (environment.contains(s)) {
                return false;
            }
        }
        return true;
    }

}
