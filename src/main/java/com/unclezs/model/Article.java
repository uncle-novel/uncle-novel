package com.unclezs.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 用于转换成e book
 *
 * @author uncle
 * @date 2020/4/29 16:57
 */
@Data
@NoArgsConstructor
public class Article {
    private String cover;
    private String title;
    private String author;
    private List<String> chapters;

    public Article(String cover) {
        this.cover = cover;
        this.author = "公众号:【书虫无书荒】";
    }
}
