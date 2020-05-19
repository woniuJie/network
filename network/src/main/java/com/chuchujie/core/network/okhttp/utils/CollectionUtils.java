package com.chuchujie.core.network.okhttp.utils;

import java.util.List;

/**
 * Created by yedr on 2016/5/26.
 */
public class CollectionUtils {

    /**
     * 判断给定的(List<T>)是否为空
     *
     * @param lists
     * @return
     * @author yedr
     */
    public static <T> boolean isListEmpty(List<T> lists) {
        return null == lists || lists.isEmpty();
    }
}
