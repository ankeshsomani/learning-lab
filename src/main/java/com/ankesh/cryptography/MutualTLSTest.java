package com.ankesh.cryptography;

public class MutualTLSTest {
    public static void main(String[] args) {
        Runnable server = () -> {
            MutualTLSServer server1 = new MutualTLSServer();
            try {
                server1.StartServer();
            }
            catch( Exception ex){
                ex.printStackTrace();
                System.out.println("sever start failed");
            }
        };

        Runnable client = () -> {
            MutualTLSClient client1 = new MutualTLSClient();
            try {
                System.out.println( "Sleeping while server is starting and then will attempt connection");
                Thread.sleep(8000);
                client1.ConnectToServer();
            } catch (Exception ex) {
                ex.printStackTrace();
                System.out.println(" Problem in connecting to server");
            }
        };

        new Thread(server).start();
        new Thread(client).start();

    }

}
