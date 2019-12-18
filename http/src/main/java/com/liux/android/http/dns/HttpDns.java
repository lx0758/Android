package com.liux.android.http.dns;

import androidx.annotation.NonNull;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public abstract class HttpDns extends TimeOutDns {

    private OkHttpClient okHttpClient;
    private Map<String, DnsResult> dnsInfoMap = new ConcurrentHashMap<>();

    public HttpDns() {
        this(500, TimeUnit.MILLISECONDS, 2);
    }

    public HttpDns(int time, TimeUnit timeUnit, int maxRetryCount) {
        super(time, timeUnit, 2);
        okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(time, timeUnit)
                .writeTimeout(time, timeUnit)
                .readTimeout(time, timeUnit)
                .retryOnConnectionFailure(false)
                .build();
    }

    @Override
    public List<InetAddress> lookup(String hostname) throws UnknownHostException {
        if (isProxyExist() || isIpAddress(hostname)) return super.lookup(hostname);

        DnsResult dnsResult = dnsInfoMap.get(hostname);
        if (dnsResult != null && dnsResult.valid()) return dnsResult.getInetAddresses();

        dnsInfoMap.remove(hostname);
        dnsResult = null;
        UnknownHostException finalException = null;

        for (int i = 0; i < maxRetryCount; i++) {
            finalException = null;
            try {
                dnsResult = lookupHttpDns(hostname);
                break;
            } catch (UnknownHostException e) {
                finalException = e;
            }
        }
        if (finalException != null) throw finalException;

        dnsInfoMap.put(hostname, dnsResult);
        assert dnsResult != null;
        return dnsResult.getInetAddresses();
    }

    public OkHttpClient getOkHttpClient() {
        return okHttpClient;
    }

    public abstract @NonNull DnsResult lookupHttpDns(String hostname) throws UnknownHostException;

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
        private long expireTime = 0;

        public boolean valid() {
            return inetAddresses != null &&
                    !inetAddresses.isEmpty() &&
                    System.currentTimeMillis() < expireTime;
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
