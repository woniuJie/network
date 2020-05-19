package com.chuchujie.core.network.okhttp.cookie.store;

import java.util.List;

import okhttp3.Cookie;
import okhttp3.HttpUrl;

/**
 * Created by wangjing on 16/12/15.
 */
public interface CookieStore {

    void add(HttpUrl uri, List<Cookie> cookie);

    List<Cookie> get(HttpUrl uri);

    List<Cookie> getCookies();

    boolean remove(HttpUrl uri, Cookie cookie);

    boolean removeAll();

}
