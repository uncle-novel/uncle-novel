package com.unclezs.utils;

import java.math.BigInteger;
import java.util.Comparator;

/**
 * 乱序重排（仅支持数字.html结尾的网页链接）
 * 一般网页都是数字结尾，起点除外
 */
public class SortUrl implements Comparator<String> {

	@Override
	public int compare(String o1, String o2) {
		//预处理，把url变成数字
		if(o1.endsWith("/")){
			o1=o1.substring(0,o1.length()-1);
			o2=o2.substring(0,o2.length()-1);
		}
		BigInteger num1;
		BigInteger num2;
		try {
			o1=o1.replace(".html","").substring(o1.lastIndexOf("/")+1,o1.length()-5);
			o2=o2.replace(".html","").substring(o2.lastIndexOf("/")+1,o2.length()-5);
			//防止数字过长超出Integer的长度，所以用bigInteger
			num1=new BigInteger(o1);
			num2=new BigInteger(o2);
		}catch (Exception e){
			return o1.compareTo(o2);
		}
		num1=num1.subtract(num2);
		return Integer.parseInt(num1.toString());
	}
}
