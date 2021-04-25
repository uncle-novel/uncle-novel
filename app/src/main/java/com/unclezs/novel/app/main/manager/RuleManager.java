package com.unclezs.novel.app.main.manager;

import cn.hutool.core.bean.BeanUtil;
import com.unclezs.novel.analyzer.core.helper.RuleHelper;
import com.unclezs.novel.analyzer.core.model.AnalyzerRule;
import com.unclezs.novel.analyzer.util.GsonUtils;
import java.util.List;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.experimental.UtilityClass;

/**
 * 规则管理器
 *
 * @author blog.unclezs.com
 * @date 2021/4/24 1:07
 */
@UtilityClass
public class RuleManager {

  public static final String RULES_FILE_NAME = "rules.json";
  private static final ObservableList<AnalyzerRule> RULES;

  static {
    RuleHelper.loadRules(ResourceManager.readConfFile(RULES_FILE_NAME));
    RULES = FXCollections.observableList(RuleHelper.rules());
    // 绑定监听
    RuleHelper.setOnRuleChangeListener(RULES::setAll);
  }

  public static ObservableList<AnalyzerRule> rules() {
    return RULES;
  }

  /**
   * 添加规则
   *
   * @param rule 规则
   */
  public static void addRule(AnalyzerRule rule) {
    AnalyzerRule old = RuleHelper.getRule(rule.getSite());
    if (old != null) {
      BeanUtil.copyProperties(rule, old);
      return;
    }
    RuleHelper.addRule(rule);
  }

  /**
   * 规则是否存在
   *
   * @param rule 规则
   * @return true 存在
   */
  public static boolean exist(AnalyzerRule rule) {
    return RuleHelper.getRule(rule.getSite()) != null;
  }

  /**
   * 更新全部规则
   *
   * @param rules 所有规则
   */
  public static void update(List<AnalyzerRule> rules) {
    RuleHelper.setRules(rules);
  }

  /**
   * 保存规则到文件
   */
  public static void save() {
    ResourceManager.saveConfFile(RULES_FILE_NAME, GsonUtils.toJson(RULES));
  }

  /**
   * 文本小说规则
   *
   * @return 规则
   */
  public static List<AnalyzerRule> textRules() {
    return RULES.stream().filter(rule -> rule.isEnabled() && rule.isEffective() && !rule.isAudio()).collect(Collectors.toList());
  }

  /**
   * 文本小说搜索规则
   *
   * @return 规则
   */
  public static List<AnalyzerRule> textSearchRules() {
    return textRules().stream().filter(rule -> rule.getSearch() != null && rule.getSearch().isEffective()).collect(Collectors.toList());
  }

  /**
   * 有声小说规则
   *
   * @return 规则
   */
  public static List<AnalyzerRule> audioRules() {
    return RULES.stream().filter(rule -> rule.isEnabled() && rule.isEffective() && rule.isAudio()).collect(Collectors.toList());
  }
}
