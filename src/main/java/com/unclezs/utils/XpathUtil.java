package com.unclezs.utils;

import static com.unclezs.constrant.RuleConstant.RULE_SEPARATOR;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.unclezs.model.rule.Rule;
import org.jsoup.nodes.Element;
import us.codecraft.xsoup.Xsoup;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * https://github.com/code4craft/xsoup
 *
 * @author uncle
 * @date 2020/4/8 15:29
 */
public class XpathUtil {
    /**
     * xpath选择器,将规则自动映射选择然后存成map
     *
     * @param element     Jsoup解析处理的元素
     * @param rule        规则
     * @param ignoreRules 忽略的字段
     * @return /
     */
    public static Map<String, String> xpath(Element element, Rule rule, List<String> ignoreRules) {
        Map<String, String> map = new HashMap<>(16);
        Field[] fields = rule.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            String fileName = field.getName();
            try {
                //String类型的并且忽略的规则列表中不包含这个
                if (field.getType() == String.class && !ignoreRules.contains(fileName)) {
                    String xpathAndAd = (String) field.get(rule);
                    //过滤出xpath选择器规则
                    if (xpathAndAd != null) {
                        String v = xpath(element, xpathAndAd);
                        map.put(fileName, v);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return map;
    }

    /**
     * 不带忽略字段的选择工具
     *
     * @param element /
     * @param rule    /
     * @return /
     */
    public static Map<String, String> xpath(Element element, Rule rule) {
        return xpath(element, rule, new ArrayList<>(0));
    }

    /**
     * xpath选择器,将规则自动映射选择然后存成指定实体（需要与规则一一对应字段）
     *
     * @param element     元素
     * @param rule        规则
     * @param ignoreRules 忽略字段
     * @param clazz       要转化成的实体
     * @param <T>         反应
     * @return T
     */
    public static <T> T xpath(Element element, Rule rule, List<String> ignoreRules, Class<T> clazz) {
        Map<String, String> map = xpath(element, rule, ignoreRules);
        return BeanUtil.mapToBean(map, clazz, true);
    }

    /**
     * 选择然后去除字符串
     *
     * @param element    元素
     * @param xpathAndAd 规则   xpath||ad1||ad2
     * @return /
     */
    public static String xpath(Element element, String xpathAndAd) {
        if (StrUtil.isEmpty(xpathAndAd)) {
            return "";
        }
        String[] xpath = xpathAndAd.split(RULE_SEPARATOR);
        String v = Xsoup.select(element, xpath[0]).get();
        if (v == null) {
            return "";
        }
        return TextUtil.remove(v, Arrays.copyOfRange(xpath, 1, xpath.length));
    }
}
