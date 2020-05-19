package com.chuchujie.core.network.okhttp.utils;

/**
 * Created by wangjing on 16/12/15.
 */
public class ExceptionUtils {

    public static void illegalArgument(String msg, Object... params) {
        throw new IllegalArgumentException(String.format(msg, params));
    }

}
