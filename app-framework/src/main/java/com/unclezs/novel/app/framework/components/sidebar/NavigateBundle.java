package com.unclezs.novel.app.framework.components.sidebar;

import com.unclezs.novel.app.framework.bundle.BaseBundle;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 侧边导航 跳转数据
 *
 * @author blog.unclezs.com
 * @since 2021/03/05 14:41
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class NavigateBundle extends BaseBundle {

  /**
   * 来自哪个view 全限定类名
   */
  @Setter(AccessLevel.PACKAGE)
  private String from;
  /**
   * 是否由菜单触发
   */
  private boolean isMenuTrigger;
  /**
   * 跳转的标记
   */
  private int flag;
}
