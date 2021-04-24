package com.unclezs.novel.app.main.manager;

import com.unclezs.novel.analyzer.core.helper.RuleHelper;
import com.unclezs.novel.analyzer.core.model.AnalyzerRule;
import com.unclezs.novel.analyzer.util.GsonUtils;
import java.util.List;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.experimental.UtilityClass;

/**
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
    RuleHelper.setOnRuleChangeListener(RULES::setAll);
  }

  public static ObservableList<AnalyzerRule> rules() {
    return RULES;
  }

  public static void update(List<AnalyzerRule> rules) {
    RuleHelper.setRules(rules);
  }

  public static void save() {
    ResourceManager.saveConfFile(RULES_FILE_NAME, GsonUtils.toJson(RULES));
  }

  public static List<AnalyzerRule> textRules() {
    return RULES.stream().filter(rule -> rule.isEnabled() && rule.isEffective() && !rule.isAudio()).collect(Collectors.toList());
  }

  public static List<AnalyzerRule> audioRules() {
    return RULES.stream().filter(rule -> rule.isEnabled() && rule.isEffective() && rule.isAudio()).collect(Collectors.toList());
  }
}
