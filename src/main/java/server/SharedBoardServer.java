package server;

import client.httpServer.SimpleHttpServer;
import messageUtils.SharedConstants;


import java.awt.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;


public class SharedBoardServer {
    static ServerSocket sock;

    public static void main(String[] args) {
        Socket cliSock;

        try {
            sock = new ServerSocket(SharedConstants.DEFAULT_PORT);
            System.out.println("Server running. Address: 127.0.0.1:"+sock.getLocalPort());
        }
        catch (IOException e){
            System.out.println("Failed TCP connection");
            System.exit(1);
        }
        boolean alreadyExecuted = false;
        while(true) {
            try{
                cliSock=sock.accept();
                new Thread(new SharedBoardServerThread(cliSock)).start();
                if(!alreadyExecuted){
                    SimpleHttpServer server = new SimpleHttpServer();
                    server.run();
                    System.out.println("HTTP Server started");
                    alreadyExecuted = true;
                }
            }
            catch (IOException e){
                System.out.println("Could not establish connection, because of the error: "+e.getMessage());
                System.exit(1);
            }
        }
    }
}

