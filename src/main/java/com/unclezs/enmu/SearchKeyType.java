package com.unclezs.enmu;

/**
 * 搜索关键词类型
 *
 * @author uncle
 * @date 2020/4/18 13:27
 */
public enum SearchKeyType {
    /**
     * 枚举
     */
    AUTHOR("作者"), TITLE("书名"), SPEAK("播音");

    private String value;

    SearchKeyType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }
}
