package com.unclezs.model;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.unclezs.mapper.SearchAudioRuleMapper;
import com.unclezs.mapper.SearchTextRuleMapper;
import com.unclezs.model.rule.SearchAudioRule;
import com.unclezs.model.rule.SearchTextRule;
import com.unclezs.utils.MybatisUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author uncle
 * @date 2020/4/22 21:09
 */
@Data
@NoArgsConstructor
public class ApplicationConfig implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 应用ID
     */
    private String appId = IdUtil.simpleUUID();
    /**
     * 系统设置
     */
    private Setting setting = new Setting();
    /**
     * 主题
     */
    private AppTheme appTheme = new AppTheme();

    /**
     * 阅读器配置
     */
    private ReaderConfig readerConfig = new ReaderConfig();
    /**
     * 文本搜索规则
     */
    @JSONField(serialize = false)
    private transient ObservableList<SearchTextRule> textRules;
    /**
     * 有声小说搜索规则
     */
    @JSONField(serialize = false)
    private transient ObservableList<SearchAudioRule> audioRules;

    public ObservableList<SearchTextRule> getTextRules() {
        if (textRules == null) {
            List<SearchTextRule> rules = MybatisUtil.execute(SearchTextRuleMapper.class, mapper -> mapper.selectList(
                Wrappers.<SearchTextRule>lambdaQuery().orderByDesc(SearchTextRule::getWeight)));
            this.textRules = FXCollections.observableArrayList(rules);
        }
        return textRules;
    }

    public ObservableList<SearchAudioRule> getAudioRules() {
        if (audioRules == null) {
            List<SearchAudioRule> rules = MybatisUtil.execute(SearchAudioRuleMapper.class, mapper -> mapper.selectList(
                Wrappers.<SearchAudioRule>lambdaQuery().orderByDesc(SearchAudioRule::getWeight)));
            this.audioRules = FXCollections.observableArrayList(rules);
        }
        return audioRules;
    }
}
