package com.unclezs.novel.app.jfx.app.ui;

import com.unclezs.novel.app.jfx.app.model.MainModel;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * @author blog.unclezs.com
 * @since 2021/03/04 11:06
 */
public class MainViewModel {
    private final MainModel model = new MainModel();
    private StringProperty name = new SimpleStringProperty();

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public StringProperty nameProperty() {
        return name;
    }

}
