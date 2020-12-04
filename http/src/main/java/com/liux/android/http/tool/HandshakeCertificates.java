package com.liux.android.http.tool;

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

public class HandshakeCertificates {

    private final X509TrustManager x509TrustManager;
    private final SSLSocketFactory sslSocketFactory;

    private HandshakeCertificates(X509TrustManager x509TrustManager, SSLSocketFactory sslSocketFactory) {
        this.x509TrustManager = x509TrustManager;
        this.sslSocketFactory = sslSocketFactory;
    }

    public X509TrustManager getX509TrustManager() {
        return x509TrustManager;
    }

    public SSLSocketFactory getSslSocketFactory() {
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

        public Builder addPlatformTrustedCertificates() {
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

        public Builder addTrustedCertificateForAssets(Context context, String fileName) {
            try {
                InputStream inputStream = context.getAssets().open(fileName);
                addTrustedCertificate(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return this;
        }

        public Builder addTrustedCertificateForRaw(Context context, int id) {
            InputStream inputStream = context.getResources().openRawResource(id);
            return addTrustedCertificate(inputStream);
        }

        public Builder addTrustedCertificate(String string) {
            return addTrustedCertificate(string.getBytes());
        }

        public Builder addTrustedCertificate(byte[] bytes) {
            return addTrustedCertificate(new ByteArrayInputStream(bytes));
        }

        public Builder addTrustedCertificate(InputStream inputStream) {
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

        public HandshakeCertificates build() {
            try {
                TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                trustManagerFactory.init(keyStore);
                TrustManager trustManager = trustManagerFactory.getTrustManagers()[0];
                X509TrustManager x509TrustManager = (X509TrustManager) trustManager;

                SSLContext sslContext = SSLContext.getInstance("TLS");
                sslContext.init(null, new TrustManager[]{x509TrustManager}, null);
                SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

                return new HandshakeCertificates(x509TrustManager, sslSocketFactory);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        private String getIdentifier(X509Certificate x509Certificate) {
            return x509Certificate.getIssuerDN().getName();
        }
    }
}
