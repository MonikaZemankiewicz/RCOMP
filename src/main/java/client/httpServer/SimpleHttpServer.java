package client.httpServer;
import java.io.*;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class SimpleHttpServer {
    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/boards", new BoardListHandler());
        server.setExecutor(null); // creates a default executor
        server.start();

    }

    static class BoardListHandler implements HttpHandler {
        public void handle(HttpExchange t) throws IOException {

            String response = "Put the data here...";
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }




}
