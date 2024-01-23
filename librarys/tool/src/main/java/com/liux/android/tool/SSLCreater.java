package com.liux.android.tool;

import android.content.Context;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Collection;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public class SSLCreater {

    private final X509TrustManager x509TrustManager;
    private final SSLSocketFactory sslSocketFactory;

    private SSLCreater(X509TrustManager x509TrustManager, SSLSocketFactory sslSocketFactory) {
        this.x509TrustManager = x509TrustManager;
        this.sslSocketFactory = sslSocketFactory;
    }

    public X509TrustManager getX509TrustManager() {
        return x509TrustManager;
    }

    public SSLSocketFactory getSSLSocketFactory() {
        return sslSocketFactory;
    }

    public static class Builder {

        private final CertificateFactory certificateFactory;
        private final KeyStore keyStore;

        public Builder() {
            try {
                certificateFactory = CertificateFactory.getInstance("X.509");

                keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
                keyStore.load(null, null);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        public Builder addPlatformCertificates() {
            try {
                TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                trustManagerFactory.init((KeyStore) null);
                TrustManager trustManager = trustManagerFactory.getTrustManagers()[0];
                X509TrustManager x509TrustManager = (X509TrustManager) trustManager;
                X509Certificate[] x509Certificates = x509TrustManager.getAcceptedIssuers();
                for (X509Certificate x509Certificate : x509Certificates) {
                    keyStore.setCertificateEntry(getIdentifier(x509Certificate), x509Certificate);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return this;
        }

        public Builder addX509CertificateForAssets(Context context, String fileName) {
            try {
                InputStream inputStream = context.getAssets().open(fileName);
                addX509Certificate(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return this;
        }

        public Builder addX509CertificateForRaw(Context context, int id) {
            return addX509Certificate(
                    context.getResources().openRawResource(id)
            );
        }

        public Builder addX509Certificate(String string) {
            return addX509Certificate(string.getBytes());
        }

        public Builder addX509Certificate(byte[] bytes) {
            return addX509Certificate(new ByteArrayInputStream(bytes));
        }

        public Builder addX509Certificate(InputStream inputStream) {
            try {
                Collection<? extends Certificate> certificates = certificateFactory.generateCertificates(inputStream);
                for (Certificate certificate : certificates) {
                    if (certificate instanceof X509Certificate) {
                        X509Certificate x509Certificate = (X509Certificate) certificate;
                        keyStore.setCertificateEntry(getIdentifier(x509Certificate), x509Certificate);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return this;
        }

        public SSLCreater build() {
            try {
                TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                trustManagerFactory.init(keyStore);
                TrustManager trustManager = trustManagerFactory.getTrustManagers()[0];
                X509TrustManager x509TrustManager = (X509TrustManager) trustManager;

                SSLContext sslContext = SSLContext.getInstance("TLS");
                sslContext.init(null, new TrustManager[]{x509TrustManager}, null);
                SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

                return new SSLCreater(x509TrustManager, sslSocketFactory);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        private String getIdentifier(X509Certificate x509Certificate) {
            return x509Certificate.getIssuerDN().getName();
        }
    }
}
