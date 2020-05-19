package com.chuchujie.core.network.okhttp.cookie;

import com.chuchujie.core.network.okhttp.cookie.store.CookieStore;
import com.chuchujie.core.network.okhttp.utils.ExceptionUtils;

import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

/**
 * Created by wangjing on 16/12/15.
 */
public class CookieJarImpl implements CookieJar {

    private CookieStore cookieStore;

    public CookieJarImpl(CookieStore cookieStore) {
        if (cookieStore == null) ExceptionUtils.illegalArgument("cookieStore can not be null.");
        this.cookieStore = cookieStore;
    }

    @Override
    public synchronized void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
        cookieStore.add(url, cookies);
    }

    @Override
    public synchronized List<Cookie> loadForRequest(HttpUrl url) {
        return cookieStore.get(url);
    }

    public CookieStore getCookieStore() {
        return cookieStore;
    }

}
