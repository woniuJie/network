package com.chuchujie.core.network.retrofit;

import android.content.Context;
import android.net.ConnectivityManager;

import com.qiniu.android.dns.DnsManager;
import com.qiniu.android.dns.Domain;
import com.qiniu.android.dns.IResolver;
import com.qiniu.android.dns.NetworkInfo;
import com.qiniu.android.dns.http.DnspodFree;
import com.qiniu.android.dns.local.AndroidDnsServer;
import com.qiniu.android.dns.local.DnshijackingException;
import com.qiniu.android.dns.local.HijackingDetectWrapper;
import com.qiniu.android.dns.local.Resolver;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;

import okhttp3.Dns;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Created by wangjing on 2017/4/26.
 */
public class DefaultDnsImpl implements Dns {

    private static final String[] DNS_SERVERS = new String[]{"114.114.114.114", "119.29.29.29"};

    private final HttpLoggingInterceptor.Logger mLogger;

    private DnsManager mDnsManager;

    private Context mContext;

    private String[] mUdpDnsServers;

    private String[] mHttpDnsServers;

    private DnsHijackCallback mDnsHijackCallback;

    private boolean needUdpHijack = true;

    public DefaultDnsImpl(Context context) {
        this(context, null);
    }

    public DefaultDnsImpl(Context context, HttpLoggingInterceptor.Logger logger) {
        this(context, null, null, logger, null);
    }

    public DefaultDnsImpl(Context context,
                          String[] udpDnsServers,
                          String[] httpDnsServers,
                          HttpLoggingInterceptor.Logger logger,
                          DnsHijackCallback dnsHijackCallback) {
        this(context,udpDnsServers,httpDnsServers,logger,dnsHijackCallback,true);
    }

    public DefaultDnsImpl(Context context,
                          String[] udpDnsServers,
                          String[] httpDnsServers,
                          HttpLoggingInterceptor.Logger logger,
                          DnsHijackCallback dnsHijackCallback,boolean needUdpHijack) {
        this.mContext = context;
        this.mUdpDnsServers = udpDnsServers == null ? new String[]{} : udpDnsServers;
        this.mHttpDnsServers = httpDnsServers == null ? new String[]{} : httpDnsServers;
        this.mLogger = logger;
        this.mDnsHijackCallback = dnsHijackCallback;
        this.needUdpHijack = needUdpHijack;
    }

    @Override
    public List<InetAddress> lookup(String hostname) throws UnknownHostException {
        List<InetAddress> result = null;
        try {
            result = Arrays.asList(getDnsManager().queryInetAdress(new Domain(hostname)));
        } catch (IOException e) {
            if (mLogger != null) {
                mLogger.log("parse dns failed. exception:" + e != null ? e.getMessage() : "");
            }

            if (e instanceof DnshijackingException && mDnsHijackCallback != null) {
                mDnsHijackCallback.onDnsHijacking(hostname, e.getMessage());
            }

            throw new UnknownHostException(e != null ? e.getMessage() : "");
        }
        return result;
    }

    protected DnsManager getDnsManager() {
        if (this.mDnsManager == null) {
            IResolver[] resolvers = createResolvers();
            NetworkInfo.NetSatus netSatus;
            if (this.mContext != null) {
                ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(
                        Context.CONNECTIVITY_SERVICE);
                android.net.NetworkInfo ani = cm.getActiveNetworkInfo();
                if (ani != null
                        && ani.getType() == ConnectivityManager.TYPE_WIFI
                        && ani.isConnected()) {
                    netSatus = NetworkInfo.NetSatus.WIFI;
                } else {
                    netSatus = NetworkInfo.NetSatus.MOBILE;
                }
            } else {
                netSatus = NetworkInfo.NetSatus.WIFI;
            }

            this.mDnsManager = new DnsManager(new NetworkInfo(netSatus, 0), resolvers);
        }

        return this.mDnsManager;
    }

    private IResolver[] createResolvers() {
        IResolver[] resolvers = null;
        if (mUdpDnsServers.length == 0 && mHttpDnsServers.length == 0) {
            resolvers = new IResolver[3];
            // 系统dns解析
            resolvers[0] = AndroidDnsServer.defaultResolver();
            // udp dns解析
            if (needUdpHijack) {
                resolvers[1] = new HijackingDetectWrapper(
                        new Resolver(createInetAddress(DNS_SERVERS[0])));
            }else {
                resolvers[1] = new Resolver(createInetAddress(DNS_SERVERS[0]));
            }
            // http dns解析
            resolvers[2] = new DnspodFree(DNS_SERVERS[1]);
        } else {
            resolvers = new IResolver[mUdpDnsServers.length + mHttpDnsServers.length];
            for (int i = 0; i < mUdpDnsServers.length; i++) {
                if (needUdpHijack) {
                    resolvers[i] = new HijackingDetectWrapper(
                            new Resolver(createInetAddress(mUdpDnsServers[i])));
                }else{
                    resolvers[i] = new Resolver(createInetAddress(mUdpDnsServers[i]));
                }
            }
            for (int i = 0; i < mHttpDnsServers.length; i++) {
                resolvers[i + mHttpDnsServers.length - 1] = new DnspodFree(mHttpDnsServers[i]);
            }
        }
        return resolvers;
    }

    private InetAddress createInetAddress(String host) {
        InetAddress inetAddress = null;
        try {
            inetAddress = InetAddress.getByName(host);
        } catch (UnknownHostException e) {
            if (mLogger != null) {
                mLogger.log("创建InetAddress异常:" + e != null ? e.getMessage() : "");
            }
        }
        return inetAddress;
    }

}
