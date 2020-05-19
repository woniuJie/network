package com.chuchujie.core.network.okhttp.callback;

/**
 * Created by wangjing on 16/12/15.
 */
public interface IGenericsSerializator {
    <T> T transform(String response, Class<T> classOfT);
}
