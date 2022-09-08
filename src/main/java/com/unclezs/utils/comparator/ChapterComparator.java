package com.unclezs.utils.comparator;

import cn.hutool.core.util.NumberUtil;
import com.unclezs.model.Chapter;
import com.unclezs.utils.UrlUtil;

import java.math.BigInteger;
import java.util.Comparator;

/**
 * @author uncle
 * @date 2020/4/9 21:54
 */
public class ChapterComparator implements Comparator<Chapter> {
    @Override
    public int compare(Chapter o1, Chapter o2) {
        String one = UrlUtil.getUrlLastPathNotSuffix(o1.getUrl());
        String two = UrlUtil.getUrlLastPathNotSuffix(o2.getUrl());
        if (NumberUtil.isNumber(one) && NumberUtil.isNumber(two)) {
            BigInteger v1 = new BigInteger(one);
            BigInteger v2 = new BigInteger(two);
            return v1.compareTo(v2);
        }
        return 0;
    }
}
