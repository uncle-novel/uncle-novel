package com.unclezs.model.rule;

/**
 * @author uncle
 * @date 2020/4/8 15:34
 */
public interface Rule {
    /**
     * 获取站点
     *
     * @return /
     */
    String getSite();

    /**
     * 获取权重
     *
     * @return /
     */
    Integer getWeight();
}
