package com.jcodecraeer.xrecyclerview.utils;

public class StringUtils {
    public static String defaultStr(String in,String defaultStr){
        return com.blankj.utilcode.util.StringUtils.isTrimEmpty(in) ? defaultStr : in;
    }
}
