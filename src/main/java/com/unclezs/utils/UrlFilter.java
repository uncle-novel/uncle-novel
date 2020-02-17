package com.unclezs.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UrlFilter {
    /**
     *
     * @param urls 传入需要过滤的小说章节目录链接
     * @return 过滤后的url链接集合
     */
    public static List<String> getChapterFilterRes(List<String> urls){
        String baseUrl = getBaseUrl(urls);
        List<String> res=new ArrayList<>();
        res.addAll(urls);
        BloomFileter filter = new BloomFileter(BloomFileter.MisjudgmentRate.HIGH, 10000, null);//布隆过滤器去重复url
        for(String url:urls){
            if(filter.addIfNotExist(url)){//url已经存在过则直接移除
                res.remove(url);
            }else if(!url.contains(baseUrl)){//如果不包含基准url就排除掉
                res.remove(url);
            }else if((url.length()-1)<=baseUrl.length()){//如果长度小于基准url，那么肯定不是需要的小说正文链接，排除(减一去除url末尾的/)
                res.remove(url);
            }
        }
        return res;
    }
    /**
     *
     * @param urls 要获取基准url的url集合
     * @return 基准url
     */
    public static String getBaseUrl(List<String> urls){
        String base="";
        Map<String,Integer> f=new HashMap<>();
        for (String url:urls){
            //url=url.substring(0,url.length()-2);//去除末尾时/的url的影响
            url=url.substring(0,url.lastIndexOf("/"));//获取除去末尾的url地址
            if(!f.containsKey(url)){
                f.put(url,1);
            }else {
                f.put(url,f.get(url)+1);
            }
        }
        //遍历获取出现次数最多的基准url
        int max=0;
        for(String key:f.keySet()){
            if(f.get(key)>max){
                base=key;
                max=f.get(key);
            }
        }
        return base+"/";
    }

}
