package com.unclezs.constrant;

import java.util.HashMap;
import java.util.Map;

/**
 * 中文字体
 *
 * @author uncle
 * @date 2020/6/8 16:35
 */
public final class ChineseFont {
    private static Map<String, String> map = new HashMap<>();

    static {
        map.put("宋体", "SimSun");
        map.put("新宋体", "NSimSun");
        map.put("黑体", "SimHei");
        map.put("仿宋", "FangSong");
        map.put("楷体", "KaiTi");
        map.put("隶书", "LiSu");
        map.put("幼圆", "YouYuan");
        map.put("华文细黑", "STXihei");
        map.put("华文楷体", "STKaiti");
        map.put("华文宋体", "STSong");
        map.put("华文中宋", "STZhongsong");
        map.put("华文仿宋", "STFangsong");
        map.put("方正舒体", "FZShuTi");
        map.put("方正姚体", "FZYaoti");
        map.put("华文彩云", "STCaiyun");
        map.put("华文琥珀", "STHupo");
        map.put("华文隶书", "STLiti");
        map.put("华文行楷", "STXingkai");
        map.put("华文新魏", "STXinwei");
    }

    /**
     * 获取对应的英文
     *
     * @param name /
     * @return /
     */
    public static String getFont(String name) {
        return map.get(name) == null ? name : map.get(name);
    }
}