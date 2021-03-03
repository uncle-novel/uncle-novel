package com.uncles.novel.app.jfx;

import com.uncles.novel.app.jfx.framework.app.TestApp;
import com.uncles.novel.app.jfx.framework.util.LanguageUtils;
import com.uncles.novel.app.jfx.framework.util.ResourceUtils;
import javafx.fxml.FXMLLoader;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URL;

/**
 * @author blog.unclezs.com
 * @since 2021/02/26 14:28
 */
public class LoaderTest {
    @Test
    public void test() throws IOException {
        URL location = ResourceUtils.load("/layout/simple.fxml");
        FXMLLoader loader = new FXMLLoader();
        FXMLLoader fxmlLoader = new FXMLLoader(location, LanguageUtils.getBundle("basic"));
        fxmlLoader.load();
        TestApp testApp = fxmlLoader.getController();
        System.out.println(testApp.resources.getString("Uncle小说"));
    }
}
