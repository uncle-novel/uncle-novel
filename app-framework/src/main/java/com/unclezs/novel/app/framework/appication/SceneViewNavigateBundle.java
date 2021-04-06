package com.unclezs.novel.app.framework.appication;

import com.unclezs.novel.app.framework.bundle.BaseBundle;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * 场景view切换 数据包
 *
 * @author blog.unclezs.com
 * @date 2021/03/06 17:03
 */
public class SceneViewNavigateBundle extends BaseBundle {

  /**
   * 来自哪个view 全限定类名
   */
  @Setter(AccessLevel.PACKAGE)
  @Getter
  private String from;
}
