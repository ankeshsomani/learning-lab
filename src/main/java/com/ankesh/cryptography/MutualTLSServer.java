package com.ankesh.cryptography;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.SSLContext;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyStore;

public class MutualTLSServer {

    public static final String SERVER_KEYSTORE = "/Users/ankeshsomani/temp/server.keystore";
    public static final String SERVER_TRUSTSTORE = "/Users/ankeshsomani/temp/server.truststore";
    public static final String PASSWORD = "password";
    public static final String JKS = "JKS";
    public static final String TLS = "TLSv1.2";

    public void StartServer() throws Exception {
        // Load server keystore
        KeyStore keyStore = KeyStore.getInstance(JKS);
        try (FileInputStream keyStoreInput = new FileInputStream(SERVER_KEYSTORE)) {
            System.out.println("before loading server keystore");
            keyStore.load(keyStoreInput, PASSWORD.toCharArray());
        }

        // Create KeyManagerFactory
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keyStore, PASSWORD.toCharArray());

        // Load server truststore
        KeyStore trustStore = KeyStore.getInstance(JKS);
        try (FileInputStream trustStoreInput = new FileInputStream(SERVER_TRUSTSTORE)) {
            System.out.println("before loading server truststore");
            trustStore.load(trustStoreInput, PASSWORD.toCharArray());
        }

        // Create TrustManagerFactory
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(trustStore);

        // Set up SSLContext
        SSLContext sslContext = SSLContext.getInstance(TLS);
        sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);

        // Create server socket
        SSLServerSocketFactory serverSocketFactory = sslContext.getServerSocketFactory();
        try (SSLServerSocket serverSocket = (SSLServerSocket) serverSocketFactory.createServerSocket(8443)) {
            System.out.println("Server listening on port 8443");
            serverSocket.setNeedClientAuth(true);
            serverSocket.setEnabledCipherSuites(serverSocket.getSupportedCipherSuites());

            // Accept client connections
            try (SSLSocket clientSocket = (SSLSocket) serverSocket.accept()) {
                System.out.println("Client connected");

                // Handle client communication
                try (InputStream input = clientSocket.getInputStream();
                     OutputStream output = clientSocket.getOutputStream()) {
                    byte[] buffer = new byte[1024];
                    int bytesRead = input.read(buffer);
                    System.out.println("Received from client: " + new String(buffer, 0, bytesRead));
                    output.write("Hello from server".getBytes());
                }
            }
        }
    }
}

