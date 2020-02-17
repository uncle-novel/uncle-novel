package com.unclezs.utils;

import java.io.*;

/**
 * 对象存入工具类
 * Created by Uncle
 * 2019.07.31.
 */
public class OBJUtil {
    private static String baseDir = "cache/";

    static {
        new File(baseDir).mkdirs();
    }

    /**
     * 保存配置文件
     *
     * @param wobj 要保存的对象
     * @param name 保存成什么名字
     */
    public static void saveOBJ(Object wobj, String name) {
        try {
            name = baseDir + name;
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(name));
            out.writeObject(wobj);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取配置文件对象
     *
     * @param name 对象的名字
     * @return 读取到的对象
     */
    public static <E> E loadOBJ(String name) {
        name = baseDir + name;
        File file = new File(name);
        if (!checkFileExist(name)) {
            return null;
        }
        try {
            ObjectInputStream oin = new ObjectInputStream(new FileInputStream(file));
            E e = (E) oin.readObject();
            oin.close();
            return e;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 判断文件是否存在
     *
     * @param path 文件路径
     * @return 存在返回true
     */
    public static boolean checkFileExist(String path) {
        if (new File(path).exists())
            return true;
        return false;
    }


    public static void deleteOBJ(String name) {
        new File(baseDir + name).delete();
    }
}