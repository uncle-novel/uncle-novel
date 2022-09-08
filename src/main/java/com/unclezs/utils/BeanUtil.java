package com.unclezs.utils;

import com.alibaba.fastjson.JSON;
import com.unclezs.model.AnalysisConfig;

/**
 * @author uncle
 * @date 2020/4/29 22:37
 */
public class BeanUtil {
    /**
     * 简易版copy 等待更好实现  解决拷贝fx bean后还是绑定的情况
     *
     * @return /
     */
    public static <T> T copy(T o, Class<T> toClass) {
        String s = JsonUtil.toJson(o);
        return JSON.parseObject(s, toClass);
    }

    public static void main(String[] args) {
        AnalysisConfig copy = BeanUtil.copy(new AnalysisConfig(), AnalysisConfig.class);
        System.out.println(copy);
    }
}
