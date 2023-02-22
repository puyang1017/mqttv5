package com.qiyou.mqtt.mqttv5;

import android.util.Log;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public class SSLUtil {

   /* public static SSLSocketFactory getSSLSocketFactory() {
        try {
            SSLContext sslContext = null;
            sslContext = SSLContext.getInstance("TLSv1.2");
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            InputStream in = App.getContext().getAssets().open("ca.");
            Certificate ca = cf.generateCertificate(in);
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);
            trustStore.setCertificateEntry("ca", ca);
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(trustStore);
            sslContext.init(null, trustManagerFactory.getTrustManagers(), null);
            return sslContext.getSocketFactory();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }*/

    public static SSLSocketFactory getSSL() {
        try {
            SSLContext ctx;
            ctx = SSLContext.getInstance("TLS");
//            Security.addProvider(new BouncyCastleProvider());
//            KeyStore bks = KeyStore.getInstance("BKS");
//            InputStream in = App.getContext().getAssets().open("qiyouEMQ1.bks");
//            bks.load(in, "Q^Sre5y".toCharArray());
            ctx.init(null, new TrustManager[]{new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) {

                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType) {

                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            }}, null);
            Log.i("SSLUtil","ssl 生成ok");
            return ctx.getSocketFactory();
        }  catch (Exception e) {
            Log.i("SSLUtil",e.getMessage());
        }
        return null;
    }
}