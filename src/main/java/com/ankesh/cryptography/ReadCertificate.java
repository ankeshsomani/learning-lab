package com.ankesh.cryptography;

import javax.net.ssl.HttpsURLConnection;
import java.net.URL;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Date;

public class ReadCertificate {
    public static final String SITE_URL = "https://www.google.com";

    public static void main(String[] args) {
        try {

            URL url = new URL(SITE_URL);

            // Open a connection to the website
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();

            // Connect to the server to get the certificate
            connection.connect();

            // Get the certificate(s) from the server
            Certificate[] certificates = connection.getServerCertificates();

            // Loop through each certificate and extract details
            int i =0;
            for (Certificate certificate : certificates) {
                System.out.println("*******************************");
                i++;
                System.out.println(String.format("certificate present at position %s is:-- %s", i, certificate));

                if (certificate instanceof X509Certificate) {
                    X509Certificate x509Cert = (X509Certificate) certificate;

                    // Print basic certificate details
                    System.out.println("Certificate Type: " + x509Cert.getType());
                    System.out.println("Issuer: " + x509Cert.getIssuerDN().getName());
                    System.out.println("Subject: " + x509Cert.getSubjectDN().getName());
                    System.out.println("Serial Number: " + x509Cert.getSerialNumber());
                    System.out.println("Valid From: " + x509Cert.getNotBefore());
                    System.out.println("Valid Until: " + x509Cert.getNotAfter());

                    // Print the public key details
                    System.out.println("Public Key Algorithm: " + x509Cert.getPublicKey().getAlgorithm());
                    System.out.println("Public Key: " + x509Cert.getPublicKey());

                    // Print the certificate's signature algorithm
                    System.out.println("Signature Algorithm: " + x509Cert.getSigAlgName());

                    // Check validity dates (just for demonstration)
                    Date now = new Date();
                    if (now.after(x509Cert.getNotBefore()) && now.before(x509Cert.getNotAfter())) {
                        System.out.println("The certificate is currently valid.");
                    } else {
                        System.out.println("The certificate is not valid.");
                    }

                    System.out.println("---------------------------------------");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
