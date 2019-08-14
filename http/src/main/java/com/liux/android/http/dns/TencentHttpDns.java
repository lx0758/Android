package com.liux.android.http.dns;

import com.liux.android.http.Http;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class TencentHttpDns extends HttpDns {

    public TencentHttpDns(int time, TimeUnit timeUnit) {
        super(time, timeUnit);
    }

    @Override
    public DnsResult lookupHttpDns(String hostname) throws UnknownHostException {
        HttpUrl httpUrl = new HttpUrl.Builder()
                .scheme("http")
                .host("119.29.29.29")
                .addPathSegment("d")
                .addQueryParameter("dn", hostname)
                .addQueryParameter("ttl", "1")
                .build();
        Request request = new Request.Builder().addHeader("User-Agent", Http.get().getUserAgent()).url(httpUrl).build();
        Call call = getOkHttpClient().newCall(request);
        try {
            Response response = call.execute();
            if (!response.isSuccessful()) throw new UnknownHostException("Broken system behaviour for dns lookup of " + hostname);

            ResponseBody responseBody = response.body();
            if (responseBody == null || responseBody.contentLength() <= 0) return new DnsResult().setInetAddresses(Collections.<InetAddress>emptyList());

            String result = responseBody.string();

            DnsResult dnsResult = new DnsResult();
            if (result.contains(",")) {
                String ttlString = result.substring(result.indexOf(',') + 1);
                result = result.substring(0, result.indexOf(','));
                dnsResult.setTtl(Integer.parseInt(ttlString));
            }
            dnsResult.setInetAddresses(new LinkedList<InetAddress>());
            if (result.contains(";")) {
                String[] ips = result.split(";");
                for (String ip : ips) {
                    dnsResult.getInetAddresses().add(InetAddress.getByName(ip));
                }
            } else {
                dnsResult.getInetAddresses().add(InetAddress.getByName(result));
            }
            return dnsResult;
        } catch (IOException e) {
            UnknownHostException unknownHostException = new UnknownHostException("Broken system behaviour for dns lookup of " + hostname);
            unknownHostException.initCause(e);
            throw unknownHostException;
        }
    }
}