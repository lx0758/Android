package com.liux.android.http.dns;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public abstract class HttpDns extends TimeOutDns {

    private OkHttpClient okHttpClient;
    private Map<String, DnsResult> dnsInfoMap = new ConcurrentHashMap<>();

    public HttpDns(int time, TimeUnit timeUnit) {
        super(time, timeUnit);
        okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(time, timeUnit)
                .writeTimeout(time, timeUnit)
                .readTimeout(time, timeUnit)
                .retryOnConnectionFailure(true)
                .build();
    }

    @Override
    public List<InetAddress> lookup(String hostname) throws UnknownHostException {
        if (isProxyExist() || isIpAddress(hostname)) return super.lookup(hostname);
        DnsResult dnsResult = dnsInfoMap.get(hostname);
        if (dnsResult != null) {
            if (dnsResult.isValid()) {
                return dnsResult.getInetAddresses();
            } else {
                dnsInfoMap.remove(hostname);
            }
        }
        dnsResult = lookupHttpDns(hostname);

        if (dnsResult.isValid()) dnsInfoMap.put(hostname, dnsResult);

        return dnsResult.getInetAddresses();
    }

    public OkHttpClient getOkHttpClient() {
        return okHttpClient;
    }

    public abstract DnsResult lookupHttpDns(String hostname) throws UnknownHostException;

    private boolean isProxyExist() {
        String proxyHost = System.getProperty("http.proxyHost");
        String port = System.getProperty("http.proxyPort");
        int proxyPort = Integer.parseInt(port != null ? port : "-1");
        return proxyHost != null && proxyPort != -1;
    }

    private boolean isIpAddress(String hostname) {
        return hostname.matches("^((25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.){3}(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)$") ||
                hostname.matches("^([\\da-fA-F]{1,4}:){7}[\\da-fA-F]{1,4}$");
    }

    protected static class DnsResult {

        private List<InetAddress> inetAddresses;
        private long expireTime = -1;

        public boolean isValid() {
            return System.currentTimeMillis() > expireTime;
        }

        public List<InetAddress> getInetAddresses() {
            return inetAddresses;
        }

        public DnsResult setInetAddresses(List<InetAddress> inetAddresses) {
            this.inetAddresses = inetAddresses;
            return this;
        }

        public DnsResult setTtl(int ttl) {
            this.expireTime = System.currentTimeMillis() + (ttl * 1000);
            return this;
        }
    }
}
