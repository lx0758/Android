package com.liux.android.tool;

import android.content.Context;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Enumeration;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import javax.security.auth.x500.X500Principal;

public class SSLCreator {

    private final KeyStore keyStore;
    private final X509TrustManager x509TrustManager;
    private final SSLSocketFactory sslSocketFactory;

    private SSLCreator(Builder builder) {
        try {
            keyStore = builder.keyStore;

            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);
            TrustManager trustManager = trustManagerFactory.getTrustManagers()[0];
            x509TrustManager = (X509TrustManager) trustManager;

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{x509TrustManager}, null);
            sslSocketFactory = sslContext.getSocketFactory();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public KeyStore getKeyStore() {
        return keyStore;
    }

    public X509TrustManager getX509TrustManager() {
        return x509TrustManager;
    }

    public SSLSocketFactory getSSLSocketFactory() {
        return sslSocketFactory;
    }

    public static class Builder {

        private final KeyStore keyStore;
        private final CertificateFactory certificateFactory;

        public Builder() {
            try {
                keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
                keyStore.load(null, null);

                certificateFactory = CertificateFactory.getInstance("X.509");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        public Builder addCertificateForPlatform() {
            try {
                // Get the Android trust store.
                // com.android.org.conscrypt.Platform.getDefaultCertKeyStore()
                KeyStore androidCAKeyStore = KeyStore.getInstance("AndroidCAStore");
                androidCAKeyStore.load(null, null);
                Enumeration<String> aliases = androidCAKeyStore.aliases();
                while (aliases.hasMoreElements()) {
                    String alias = aliases.nextElement();
                    X509Certificate x509Certificate = (X509Certificate) androidCAKeyStore.getCertificate(alias);
                    keyStore.setCertificateEntry(alias, x509Certificate);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return this;
        }

        public Builder addCertificateForAssets(Context context, String fileName) {
            try {
                InputStream inputStream = context.getAssets().open(fileName);
                addCertificate(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return this;
        }

        public Builder addCertificateForRaw(Context context, int id) {
            return addCertificate(
                    context.getResources().openRawResource(id)
            );
        }

        public Builder addCertificate(String string) {
            return addCertificate(string.getBytes());
        }

        public Builder addCertificate(byte[] bytes) {
            return addCertificate(new ByteArrayInputStream(bytes));
        }

        public Builder addCertificate(InputStream inputStream) {
            try {
                Collection<? extends Certificate> certificates = certificateFactory.generateCertificates(inputStream);
                for (Certificate certificate : certificates) {
                    if (certificate instanceof X509Certificate) {
                        addCertificate((X509Certificate) certificate);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return this;
        }

        public Builder addCertificate(X509Certificate x509Certificate) {
            try {
                keyStore.setCertificateEntry(getCertificateAlias(x509Certificate), x509Certificate);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return this;
        }

        public SSLCreator build() {
            return new SSLCreator(this);
        }
    }

    public static String getCertificateAlias(X509Certificate x509Certificate) throws NoSuchAlgorithmException {
        return getCertificateAlias(x509Certificate, "application:", ".0");
    }

    public static String getCertificateAlias(X509Certificate x509Certificate, String prefix, String suffix) throws NoSuchAlgorithmException {
        X500Principal x500Principal = x509Certificate.getSubjectX500Principal();

        // com.android.org.conscrypt.TrustedCertificateStore.hash()
        // com.android.org.conscrypt.NativeCrypto.X509_NAME_hash_old()
        // com.android.org.conscrypt.Hex.intToHexString()
        byte[] digest = MessageDigest.getInstance("MD5").digest(x500Principal.getEncoded());
        int offset = 0;
        int hash = (((digest[offset++] & 0xff) << 0) | ((digest[offset++] & 0xff) << 8)
                | ((digest[offset++] & 0xff) << 16) | ((digest[offset] & 0xff) << 24));
        String hex;
        StringBuilder stringBuilder = new StringBuilder();
        if (prefix != null) {
            stringBuilder.append(prefix);
        }
        for (int i = 3; i >= 0; i--) {
            hex = Integer.toHexString((hash >> (8 * i)) & 0xFF);
            if (hex.length() == 1) {
                stringBuilder.append("0");
            }
            stringBuilder.append(hex);
        }
        if (suffix != null) {
            stringBuilder.append(suffix);
        }
        return stringBuilder.toString();
    }
}
