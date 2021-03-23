package com.unclezs.novel.app.jfx.launcher.model;

import com.unclezs.novel.app.jfx.launcher.enums.Os;
import lombok.Data;

/**
 * @author blog.unclezs.com
 * @since 2021/03/23 13:46
 */
@Data
public class Library {
    private String path;
    private String size;
    private Os os;
}
