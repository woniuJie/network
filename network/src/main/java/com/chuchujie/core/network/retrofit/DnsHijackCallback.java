package com.chuchujie.core.network.retrofit;

/**
 * Created by wangjing on 2018/7/17.
 */
public interface DnsHijackCallback {

    /**
     * dns hijacking
     *
     * @param hostname
     * @param message
     */
    void onDnsHijacking(String hostname, String message);

}
