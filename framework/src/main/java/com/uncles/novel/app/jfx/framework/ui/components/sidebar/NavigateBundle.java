package com.uncles.novel.app.jfx.framework.ui.components.sidebar;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

/**
 * 侧边导航 跳转数据
 *
 * @author blog.unclezs.com
 * @since 2021/03/05 14:41
 */
@Data
@NoArgsConstructor
public class NavigateBundle {
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
    private Map<String, Object> data;

    public NavigateBundle put(String key, String value) {
        if (data == null) {
            data = new HashMap<>(16);
        }
        data.put(key, value);
        return this;
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        return (T) data.get(key);
    }
}
