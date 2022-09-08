package com.unclezs.gui.app;

/**
 * @author unclezs.com
 * @date 2019.07.06 10:06
 */

import static cn.hutool.http.ssl.SSLSocketFactoryBuilder.SSL;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.unclezs.gui.components.StageDecorator;
import com.unclezs.gui.controller.TestController;
import com.unclezs.gui.utils.ResourceUtil;
import com.unclezs.utils.RequestUtil;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputControl;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


public class TestStage extends Application {
    public MediaPlayer mediaPlayer;
    int i = 0;

    public static void main(String[] args) {
        launch(args);
    }

    private static void testStage(Stage primaryStage) throws IOException {
        Node load = ResourceUtil.loadFxml(TestController.class);
        StageDecorator root = new StageDecorator(primaryStage, load);
        root.setGraphic(new ImageView("images/logo/uncle_novel.png"));
        Scene scene = new Scene(root, 800, 800);
        scene.getStylesheets().add(TestStage.class.getResource("/css/index.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void start(Stage primaryStage)
        throws IOException, URISyntaxException, KeyManagementException, NoSuchAlgorithmException {
        BorderPane pane = new BorderPane();
        WebView webView = new WebView();
        URI uri = URI.create("https://vipreader.qidian.com/chapter/1735921/45491650");
        String cookie =
            "_yep_uuid=c395968a-17d7-e37c-88ba-94f245b64c45; e1=%7B%22pid%22%3A%22qd_P_vipread%22%2C%22eid%22%3A%22qd_G94%22%2C%22l2%22%3A4%2C%22l1%22%3A15%7D; e2=%7B%22pid%22%3A%22qd_P_vipread%22%2C%22eid%22%3A%22%22%2C%22l2%22%3A4%2C%22l1%22%3A15%7D; _csrfToken=1bVJens69OPMqvdJgsS0Bry2U1LDni7qaBuwUAlD; newstatisticUUID=1608791066_10517697; ywguid=1585503310; ywkey=ywqK8YfCTwpY; ywopenid=7387FAAE42281F44434B7CF4977CFD3E; qdrs=0%7C3%7C0%7C0%7C1; showSectionCommentGuide=1; qdgd=1; pageOps=1; e2=%7B%22pid%22%3A%22qd_P_auto_dingyue%22%2C%22eid%22%3A%22qd_M186%22%2C%22l1%22%3A2%7D; rcmClose=1; e1=%7B%22pid%22%3A%22qd_P_my_bookshelf%22%2C%22eid%22%3A%22qd_M185%22%2C%22l1%22%3A2%7D; bc=1012284323%2C3247938%2C2952453%2C1735921%2C1024617405; rcr=1735921%2C1024617405%2C1012284323%2C2952453%2C3247938%2C1024416983%2C1025224742; lrbc=1735921%7C45491650%7C1%2C1024617405%7C625270110%7C0%2C1025224742%7C622420708%7C0";
//        AgentLoader.loadAgentClass(MyJavaAgent.class.getName(), null);
        WebEngine engine = webView.getEngine();
        Map<String, List<String>> headers = new LinkedHashMap<>();
        headers.put("Set-Cookie", Arrays.stream(cookie.split(";")).collect(Collectors.toList()));
        java.net.CookieHandler.getDefault().put(new URI("https://vipreader.qidian.com"), headers);
        engine.getLoadWorker().stateProperty().addListener(
            (ov, oldState, newState) -> {
                if (newState == Worker.State.SUCCEEDED) {
                    ThreadUtil.execute(() -> {
                        Platform.runLater(() -> {
                            String html = engine.executeScript("document.documentElement.outerHTML").toString();
                            System.out.println(html);
                        });
                    });
                }
            });
        engine.setOnError(e -> {
            e.getException().printStackTrace();
        });
        Button loading = new Button("加载");
        pane.setTop(loading);
        loading.setOnMouseClicked(e -> {
            engine.load(uri.toString());
            ThreadUtil.execute(() -> {
                while (true) {
                    ThreadUtil.sleep(2000);
                    Platform.runLater(() -> {
                        System.out.println(engine.getLoadWorker().getState());
                    });
                }
            });
        });
        TrustManager trm = new X509TrustManager() {
            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            @Override
            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        };
        SSLContext sc = SSLContext.getInstance(SSL);
        sc.init(null, new TrustManager[] {trm}, null);
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        engine.setUserAgent(RequestUtil.USER_AGENT);
        pane.setCenter(webView);
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        //底层面板
        StackPane root = new StackPane();
        root.setStyle(
            "-fx-background-color: rgba(255, 255, 255, 1);" +
                "-fx-effect: dropshadow(gaussian, black, 50, 50, 0, 0);" +
                "-fx-background-insets: 50;"
        );
        root.getChildren().add(pane);
        Scene scene = new Scene(root, 800, 800);
        scene.setFill(Color.TRANSPARENT);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void test() {
        String res = HttpUtil.createPost("https://developer.baidu.com/vcast/getVcastInfo")
            .form(Dict.create()
                .set("title", "完美世界")
                .set("content", "")
                .set("sex", 4).set("speed", 5).set("volumn", 7).set("pit", 5).set("method", "TRADIONAL"))
            .cookie(
                "BIDUPSID=B8263343A610B7418BE7F0D3268C3918; Hm_lvt_e05e14d468d18c3e7677ef5401c0698c=1586248890; bdshare_firstime=1586249041552; jshunter-uuid=75e743f9-7328-4703-90e8-3bdb565cad4d; BAIDUID=1115E9CB69A8E1D0B7F1A031472E795E:FG=1; BDUSS_BFESS=9kVHFBflRxaFFiODVUQUV0SU5Ec001allKQ3haN1RPb2FOQnFjcHUtZ1lRTkplRVFBQUFBJCQAAAAAAAAAAAEAAADqufFKVbLmsuawobChAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABizql4Ys6peUU; BDORZ=B490B5EBF6F3CD402E515D22BCDA1598; H_WISE_SIDS=142143_146498_143879_145946_140631_145497_144991_144134_145237_146536_146307_131247_144682_141261_144251_140259_141941_127969_146549_140593_143492_131953_131423_144658_142208_145523_146003_145601_107316_146592_146136_139909_144966_145607_144765_144018_146053_145398_143858_145072_139914_110085; H_PS_PSSID=31656_1466_31671_21127_31591_30842_31270_31661_31464_30823_26350; delPer=0; PSINO=1; Hm_lvt_3abe3fb0969d25e335f1fe7559defcc6=1587691192,1589687465; BDUSS=VFczg2akJsd1FjeHl3bmVoamlhTExjVTQzU3JCcXppbjN-eVdkZ3VRVkthLWhlRUFBQUFBJCQAAAAAAAAAAAEAAADqufFKVbLmsuawobChAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAErewF5K3sBebl; __yjsv5_shitong=1.0_7_ff5620d09205b04712ecca3a47e7cbf063d1_300_1589699131535_125.85.184.101_ecb01dcd; Hm_lpvt_3abe3fb0969d25e335f1fe7559defcc6=1589699137")
            .header("Referer", "https://developer.baidu.com/vcast")
            .execute()
            .body();
        System.out.println(res);
        String audioLink = JSON.parseObject(res).getString("bosUrl");
        MediaPlayer mediaPlayer = new MediaPlayer(new Media(audioLink));
        mediaPlayer.setAutoPlay(true);
    }


    class Test extends TextInputControl {
        /**
         * Creates a new TextInputControl. The content is an immutable property and
         * must be specified (as non-null) at the time of construction.
         *
         * @param content a non-null implementation of Content.
         */
        protected Test(Content content) {
            super(content);
        }

    }


    public class JavaBridge {
        public void log(String text) {
            System.out.println(text);
        }
    }

}

