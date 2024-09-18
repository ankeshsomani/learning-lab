package com.ankesh.cryptography;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyStore;

public class MutualTLSClient {

    public static final String CLIENT_KEYSTORE = "/Users/ankeshsomani/temp/client.keystore";
    public static final String CLIENT_TRUSTSTORE = "/Users/ankeshsomani/temp/client.truststore";
    public static final String PASSWORD = "password";
    public static final String JKS = "JKS";
    public static final String TLS = "TLSv1.2";

    public void ConnectToServer() throws Exception {
        // Load client keystore

        KeyStore keyStore = KeyStore.getInstance(JKS);
        try (FileInputStream keyStoreInput = new FileInputStream(CLIENT_KEYSTORE)) {
            System.out.println("before loading client keystore");
            keyStore.load(keyStoreInput, PASSWORD.toCharArray());
        }


        // Create KeyManagerFactory
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keyStore, PASSWORD.toCharArray());

        // Load client truststore
        KeyStore trustStore = KeyStore.getInstance(JKS);
        try (FileInputStream trustStoreInput = new FileInputStream(CLIENT_TRUSTSTORE)) {
            System.out.println("before loading client truststore");
            trustStore.load(trustStoreInput, PASSWORD.toCharArray());
        }


        // Create TrustManagerFactory
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(trustStore);

        // Set up SSLContext
        SSLContext sslContext = SSLContext.getInstance(TLS);
        sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);

        // Create socket and connect to server
        SSLSocketFactory socketFactory = sslContext.getSocketFactory();
        SSLSocket socket = (SSLSocket) socketFactory.createSocket("localhost", 8443);
         socket.setNeedClientAuth(true);  // This forces client authentication
        socket.setEnabledCipherSuites(socket.getSupportedCipherSuites());



            System.out.println("Connected to server");

            // Handle communication with server
            try (InputStream input = socket.getInputStream();
                 OutputStream output = socket.getOutputStream()) {
                output.write("Hello from client".getBytes());
                byte[] buffer = new byte[1024];
                int bytesRead = input.read(buffer);
                System.out.println("Received from server: " + new String(buffer, 0, bytesRead));
            }

    }
}
