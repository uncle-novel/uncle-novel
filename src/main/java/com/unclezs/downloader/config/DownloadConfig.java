package com.unclezs.downloader.config;

import com.unclezs.enmu.TextNovelType;
import com.unclezs.model.Setting;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 下载配置
 *
 * @author uncle
 * @date 2020/4/30 9:31
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DownloadConfig {
    private Integer threadNum = 1;
    private Integer delay;
    private TextNovelType type;
    private String path;
    private boolean merge;


    public DownloadConfig(Setting setting) {
        this.path = setting.getSavePath().get();
        this.threadNum = setting.getThreadNum().get();
        this.delay = setting.getDelay().get();
        this.type = TextNovelType.valueOf(setting.getTextNovelSaveType().get());
        this.merge = setting.getMergeFile().get();
    }
}
