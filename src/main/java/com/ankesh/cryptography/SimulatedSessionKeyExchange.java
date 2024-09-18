package com.ankesh.cryptography;

import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.IvParameterSpec;
import java.security.*;
import java.util.Base64;

public class SimulatedSessionKeyExchange {

    public static void main(String[] args) throws Exception {
        // Step 1: Generate public and private key pairs for client and server (using Diffie-Hellman)
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("DH"); // Diffie-Hellman
        keyPairGenerator.initialize(2048);

        // Client generates its key pair (private and public)
        KeyPair clientKeyPair = keyPairGenerator.generateKeyPair();
        PrivateKey clientPrivateKey = clientKeyPair.getPrivate();
        PublicKey clientPublicKey = clientKeyPair.getPublic();

        // Server generates its key pair (private and public)
        KeyPair serverKeyPair = keyPairGenerator.generateKeyPair();
        PrivateKey serverPrivateKey = serverKeyPair.getPrivate();
        PublicKey serverPublicKey = serverKeyPair.getPublic();

        // Step 2: Each party generates the shared secret (session key) using the other's public key
        // Client generates the shared secret using its private key and the server's public key
        KeyAgreement clientKeyAgreement = KeyAgreement.getInstance("DH");
        clientKeyAgreement.init(clientPrivateKey);
        clientKeyAgreement.doPhase(serverPublicKey, true);
        byte[] clientSharedSecret = clientKeyAgreement.generateSecret();

        // Server generates the shared secret using its private key and the client's public key
        KeyAgreement serverKeyAgreement = KeyAgreement.getInstance("DH");
        serverKeyAgreement.init(serverPrivateKey);
        serverKeyAgreement.doPhase(clientPublicKey, true);
        byte[] serverSharedSecret = serverKeyAgreement.generateSecret();

        // Both the client and server should have the same shared secret now
        System.out.println("Client Shared Secret: " + Base64.getEncoder().encodeToString(clientSharedSecret));
        System.out.println("Server Shared Secret: " + Base64.getEncoder().encodeToString(serverSharedSecret));

        // Step 3: Derive an AES key from the shared secret (session key)
        SecretKey sessionKey = new SecretKeySpec(clientSharedSecret, 0, 16, "AES");

        // Step 4: Encrypt and decrypt a message using the session key (AES)
        String message = "Hello, secure world!";

        // Encrypt the message using AES
        Cipher encryptCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        IvParameterSpec iv = new IvParameterSpec(new byte[16]); // Initialization vector (just zeros for simplicity)
        encryptCipher.init(Cipher.ENCRYPT_MODE, sessionKey, iv);
        byte[] encryptedMessage = encryptCipher.doFinal(message.getBytes());

        System.out.println("Encrypted Message: " + Base64.getEncoder().encodeToString(encryptedMessage));

        // Decrypt the message using AES (using the same session key)
        Cipher decryptCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        decryptCipher.init(Cipher.DECRYPT_MODE, sessionKey, iv);
        byte[] decryptedMessage = decryptCipher.doFinal(encryptedMessage);

        System.out.println("Decrypted Message: " + new String(decryptedMessage));
    }
}

