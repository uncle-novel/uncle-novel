package com.unclezs.utils;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ObjectUtil;

import java.io.IOException;

/**
 * 序列化到文件工具
 *
 * @author uncle
 * @date 2020/4/30 9:00
 */
public class SerializableUtil {
    /**
     * 序列化
     *
     * @param o    对象
     * @param path 路径
     * @throws IOException 路径不存在
     */
    public static void serialize(Object o, String path) throws IOException {
        byte[] serialize = ObjectUtil.serialize(o);
        FileUtil.writeBytes(serialize, path);
    }

    /**
     * 反序列化
     *
     * @param path 路径
     * @param <T>  对象
     * @return /
     */
    public static <T> T deserialize(String path) {
        byte[] bytes = FileUtil.readBytes(path);
        return ObjectUtil.deserialize(bytes);
    }

}
