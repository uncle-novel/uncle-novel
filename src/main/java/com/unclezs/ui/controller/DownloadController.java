package com.unclezs.ui.controller;

import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXProgressBar;
import com.unclezs.adapter.DownloadAdapter;
import com.unclezs.mapper.DownHistoryMapper;
import com.unclezs.model.DownloadHistory;
import com.unclezs.ui.node.DownloadedNode;
import com.unclezs.ui.node.DownloadingNode;
import com.unclezs.ui.utils.DataManager;
import com.unclezs.utils.MybatisUtil;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import org.apache.ibatis.session.SqlSession;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

/*
 *下载管理
 *@author unclezs.com
 *@date 2019.07.06 00:16
 */
public class DownloadController implements Initializable {
    @FXML
    VBox downloadRoot;
    @FXML
    Pane headerPane;
    @FXML
    Label on, over;
    @FXML
    TableView tableView;//正在下载列表
    @FXML
    TableColumn hId, hTitle, hPb, hOp;//表格四列
    @FXML
    JFXListView overList;//下载完成列表

    static ObservableList<DownloadingNode> list = FXCollections.observableArrayList();//正在下载列表
    static ObservableList<DownloadedNode> finishList = FXCollections.observableArrayList();//下载完成列表

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        autoSize();//自适应
        bindData();//绑定表格数据
        tableView.setItems(list);
        overList.setItems(finishList);
        initEventHandler();//事件监听
        loadHistory();//加载历史下载记录
    }

    //初始化事件
    void initEventHandler() {
        on.setOnMouseClicked(e -> {
            //更换面板
            overList.setVisible(false);
            tableView.setVisible(true);
            //按钮变色
            on.setStyle("-fx-background-color: RGB(124,125,133); -fx-background-radius: 5px; -fx-border-radius: 5px; -fx-border-color: rgb(229,229,229); -fx-text-fill: #e5e5e7");
            over.setStyle("-fx-background-color: RGB(255,255,255); -fx-background-radius: 5px; -fx-border-radius: 5px; -fx-border-color: rgb(229,229,229);-fx-text-fill: #888888");
        });
        over.setOnMouseClicked(e -> {
            tableView.setVisible(false);
            overList.setVisible(true);
            over.setStyle("-fx-background-color: RGB(124,125,133); -fx-background-radius: 5px; -fx-border-radius: 5px; -fx-border-color: rgb(229,229,229); -fx-text-fill: #e5e5e7");
            on.setStyle("-fx-background-color: RGB(255,255,255); -fx-background-radius: 5px; -fx-border-radius: 5px; -fx-border-color: rgb(229,229,229);-fx-text-fill: #888888");
        });

    }

    //自适应
    void autoSize() {
        on.layoutXProperty().bind(downloadRoot.layoutXProperty().add(downloadRoot.widthProperty().divide(2).subtract(110)));
        over.layoutXProperty().bind(downloadRoot.layoutXProperty().add(downloadRoot.widthProperty().divide(2)));
        //tableView自适应
        tableView.prefWidthProperty().bind(downloadRoot.widthProperty());
        tableView.prefHeightProperty().bind(DataManager.content.heightProperty().subtract(70));
        ObservableList<TableColumn> columns = tableView.getColumns();
        columns.get(0).prefWidthProperty().bind(downloadRoot.widthProperty().multiply(0.1));
        columns.get(1).prefWidthProperty().bind(downloadRoot.widthProperty().multiply(0.3));
        columns.get(2).prefWidthProperty().bind(downloadRoot.widthProperty().multiply(0.45));
        columns.get(3).prefWidthProperty().bind(downloadRoot.widthProperty().multiply(0.15));
        //下载完成listView自适应
        overList.prefWidthProperty().bind(downloadRoot.widthProperty());
        overList.prefHeightProperty().bind(DataManager.content.heightProperty().subtract(70));
    }

    void bindData() {
        hId.setCellFactory((col) -> {
            TableCell<DownloadingNode, String> cell = new TableCell<DownloadingNode, String>() {
                @Override
                public void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    this.setText(null);
                    this.setGraphic(null);

                    if (!empty) {
                        int rowIndex = this.getIndex() + 1;
                        this.setText(String.valueOf(rowIndex));
                    }
                }
            };
            return cell;
        });
        hTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        hPb.setCellValueFactory(new PropertyValueFactory<>("pbPane"));
        hOp.setCellValueFactory(new PropertyValueFactory<>("remove"));
    }

    //添加下载任务
    public static void addTask(DownloadAdapter downloader) {
        MybatisUtil.getCurrentSqlSession().close();
        DownloadingNode downloadingNode = new DownloadingNode(null, new SimpleStringProperty(downloader.getTitle()));
        JFXProgressBar pb = downloadingNode.getPb();//进度条
        Label label = downloadingNode.getLabel();//文字进度条
        //监视任务
        Task t = new Task() {
            @Override
            protected Object call() throws Exception {
                while (true) {
                    int overNum = downloader.getOverNum();//完成数量
                    int maxNUm = downloader.getMaxNum();//最大数量
                    updateProgress(overNum, maxNUm);
                    updateMessage(overNum + "/" + maxNUm);
                    Thread.sleep(1000);
                    if (overNum == maxNUm) {
                        break;
                    }
                }
                return true;
            }
        };
        pb.progressProperty().bind(t.progressProperty());//绑定进度条
        label.textProperty().bind(t.messageProperty());//绑定文字显示进度
        new Thread(t).start();
        list.add(downloadingNode);
        //成功后移除然后加入下载完成里
        t.setOnSucceeded(e -> {
            //开启sqlSession
            SqlSession sqlSession = MybatisUtil.openSqlSession(true);
            DownHistoryMapper mapper = sqlSession.getMapper(DownHistoryMapper.class);
            DownloadHistory history = new DownloadHistory(downloader.getType(), downloader.getPath(), downloader.getTitle(), getCurrentDate(), downloader.getImgPath());
            //保存记录入库
            mapper.saveDownloadHistory(history);
            //获取刚入库的记录
            DownloadHistory lastOne = mapper.findLastOne();//找到最新插入的
            DownloadedNode node = new DownloadedNode(lastOne);
            //删除历史记录事件
            removeHistory(node);
            //移除下载列表
            list.remove(downloadingNode);
            finishList.add(node);
            sqlSession.close();
        });
        //删除下载任务
        downloadingNode.getRemove().setOnMouseClicked(e -> {
            list.remove(downloadingNode);
            downloader.stop();
            t.cancel();
        });
    }

    //获取当前时间字符串
    public static String getCurrentDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("    HH:mm \nyyyy-MM-dd");
        Date date = new Date();
        String s = simpleDateFormat.format(date);
        return s;
    }

    public void loadHistory() {
        new Thread(() -> {
            DownHistoryMapper mapper = MybatisUtil.getMapper(DownHistoryMapper.class);
            List<DownloadHistory> historys=null;
            try {
                historys = mapper.findAllDownloadHistory();
            }catch (Exception e){
                System.out.println("没得记录");
                return;
            }
            if(historys==null||historys.size()==0){//防空
                return;
            }
            for (DownloadHistory history : historys) {
                DownloadedNode node = new DownloadedNode(history);
                removeHistory(node);
                overList.getItems().add(node);
            }
            MybatisUtil.getCurrentSqlSession().close();
        }).start();
    }

    //移除一条记录
    private static void removeHistory(DownloadedNode node) {
        Platform.runLater(() -> {
            node.getRemove().setOnMouseClicked(event -> {
                finishList.remove(node);//移除列表
                DownHistoryMapper mapper = MybatisUtil.getMapper(DownHistoryMapper.class);
                mapper.deleteDownLoadHistroy(node.getHistory().getId());//删库
                MybatisUtil.getCurrentSqlSession().close();
            });
        });
    }
}
