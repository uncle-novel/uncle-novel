package com.uncles.novel.app.jfx.sass;

import lombok.Data;

@Data
public class SassExtension {
    private boolean inplace = true;
    private String cssPath = "css";
    private String sassPath = "scss";
}
