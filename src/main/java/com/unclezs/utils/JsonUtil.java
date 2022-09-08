package com.unclezs.utils;

import cn.hutool.core.collection.ListUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;

/**
 * 对javaFX这些烦人的fx bean进行序列化🙄
 *
 * @author uncle
 * @date 2020/4/29 11:47
 */
public class JsonUtil {

    /**
     * 忽略properties里面的一些字段
     *
     * @param o /
     * @return /
     */
    public static String toJson(Object o) {
        SimplePropertyPreFilter simplePropertyPreFilter = new SimplePropertyPreFilter();
        simplePropertyPreFilter.getExcludes().addAll(ListUtil.toList("bound", "valueSafe"));
        return JSON.toJSONString(o, simplePropertyPreFilter, SerializerFeature.PrettyFormat);
    }
}
