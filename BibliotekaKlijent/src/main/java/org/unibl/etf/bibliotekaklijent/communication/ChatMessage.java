package org.unibl.etf.bibliotekaklijent.communication;

import org.unibl.etf.bibliotekaklijent.model.Message;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import java.io.*;
import java.net.InetAddress;
import java.security.KeyStore;

import com.google.gson.Gson;

public class ChatMessage {
    public static SSLContext createSSLContext() throws Exception {
        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        try (FileInputStream keyStoreStream = new FileInputStream("keystore.jks")) {
            keyStore.load(keyStoreStream, "sigurnost".toCharArray());
        }

        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(keyStore);

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, tmf.getTrustManagers(), null);
        return sslContext;
    }
}
