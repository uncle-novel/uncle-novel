package com.uncles.novel.app.jfx.ui;

import com.uncles.novel.app.jfx.model.MainModel;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * @author blog.unclezs.com
 * @since 2021/03/04 11:06
 */
public class MainViewModel {
    private StringProperty name = new SimpleStringProperty();
    private final MainModel model = new MainModel();

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

}
