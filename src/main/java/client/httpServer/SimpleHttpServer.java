package client.httpServer;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class SimpleHttpServer implements Runnable{
    public void run() {
        HttpServer server = null;
        try {
            server = HttpServer.create(new InetSocketAddress(8000), 0);
            server.createContext("/boards", new BoardListHandler());
            server.createContext("/notes", new NotesListHandler());
            server.createContext("/", new HomeListHandler());
            server.setExecutor(null); // creates a default executor
            server.start();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static class NotesListHandler implements HttpHandler {
        public void handle(HttpExchange t) throws IOException {
            String file = "Postits.json";
            String json = null;
            try {
                json = readFileAsString(file);

                String response = json;
                t.sendResponseHeaders(200, response.length());
                OutputStream os = t.getResponseBody();
                os.write(response.getBytes());
                os.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }

        public static String readFileAsString(String file)throws Exception
        {
            return new String(Files.readAllBytes(Paths.get(file)));
        }
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

    static class HomeListHandler implements HttpHandler {
        public void handle(HttpExchange t) throws IOException {

            String response = "<!DOCTYPE html>\n" +
                    "<html>\n" +
                    "<head>\n" +
                    "    <script src=\"https://ajax.googleapis.com/ajax/libs/jquery/3.6.4/jquery.min.js\"></script>\n" +
                    "    <style>\n" +
                    "        h1 {\n" +
                    "            text-align: center;\n" +
                    "            color: #0c4472;\n" +
                    "            font-family: Verdana, Geneva, Tahoma, sans-serif;\n" +
                    "        }\n" +
                    "\n" +
                    "        table {\n" +
                    "            background-color: #2196f3;\n" +
                    "            padding: 10px;\n" +
                    "            border-radius: 0.2rem;\n" +
                    "        }\n" +
                    "\n" +
                    "        td {\n" +
                    "            height: 100px;\n" +
                    "            width: 500px;\n" +
                    "            background-color: rgba(255, 255, 255, 0.8);\n" +
                    "            border: 1px solid #2196f3;\n" +
                    "            padding: 1rem;\n" +
                    "            font-size: 16px;\n" +
                    "            text-align: center;\n" +
                    "        }\n" +
                    "\n" +
                    "        img {\n" +
                    "            display: block;\n" +
                    "            margin: 1rem auto;\n" +
                    "            max-width: 100%;\n" +
                    "        }\n" +
                    "    </style>\n" +
                    "</head>\n" +
                    "<body onload=\"refreshView()\">\n" +
                    "<script>\n" +
                    "\n" +
                    "    function refreshView() {\n" +
                    "        var notes = [];\n" +
                    "        var board = [];\n" +
                    "\n" +
                    "        let request = new XMLHttpRequest();\n" +
                    "\n" +
                    "\n" +
                    "        request.onload = function() {\n" +
                    "            notes = JSON.parse(this.responseText);\n" +
                    "            document.body.innerHTML='';\n" +
                    "            generateTable(board, notes);\n" +
                    "            setTimeout(refreshView, 2000);\n" +
                    "        };\n" +
                    "\n" +
                    "        request.ontimeout = function() {\n" +
                    "            var header = document.createElement(\"h1\");\n" +
                    "            var headerText = document.createTextNode(\"Server timeout, still trying...\");\n" +
                    "            header.appendChild(headerText);\n" +
                    "            document.body.appendChild(header);\n" +
                    "            setTimeout(refreshView, 100);\n" +
                    "        };\n" +
                    "\n" +
                    "        request.onerror = function() {\n" +
                    "            var header = document.createElement(\"h1\");\n" +
                    "            var headerText = document.createTextNode(\"No server reply, still trying...\");\n" +
                    "            header.appendChild(headerText);\n" +
                    "            document.body.appendChild(header);\n" +
                    "            setTimeout(refreshView, 5000);\n" +
                    "        };\n" +
                    "\n" +
                    "        request.open(\"GET\", \"http://localhost:8000/notes\", true);\n" +
                    "        request.timeout = 5000;\n" +
                    "        request.send();\n" +
                    "    }\n" +
                    "    function generateTable(board, notes) {\n" +
                    "        // creates a <table> element and a <tbody> element\n" +
                    "        const tbl = document.createElement(\"table\");\n" +
                    "        const tblBody = document.createElement(\"tbody\");\n" +
                    "        let count = 0;\n" +
                    "\n" +
                    "        // creating all cells\n" +
                    "        for (let i = 1; i <= 5; i++) {\n" +
                    "            // creates a table row\n" +
                    "            const row = document.createElement(\"tr\");\n" +
                    "\n" +
                    "            for (let j = 1; j <= 5; j++) {\n" +
                    "                //count++;\n" +
                    "                // Create a <td> element and a text node, make the text\n" +
                    "                // node the contents of the <td>, and put the <td> at\n" +
                    "                // the end of the table row\n" +
                    "                const cell = document.createElement(\"td\");\n" +
                    "                let cellText = document.createTextNode(\"\");\n" +
                    "                let cellImg = document.createElement(\"img\");\n" +
                    "                /*if (count <= notes.length) {\n" +
                    "                    cellText = document.createTextNode(notes[count - 1].postit.text);\n" +
                    "                    cellImg.src = notes[count - 1].image;\n" +
                    "                    cell.appendChild(cellText);\n" +
                    "                    cell.appendChild(cellImg);\n" +
                    "                }*/\n" +
                    "                notes.map(note => {\n" +
                    "                    if(parseInt(note.postit.row) == i && parseInt(note.postit.column) == j){\n" +
                    "                        cellText = document.createTextNode(note.postit.text);\n" +
                    "                        cellImg.src = note.postit.url;\n" +
                    "                        cell.appendChild(cellText);\n" +
                    "                        cell.appendChild(cellImg);\n" +
                    "                    }\n" +
                    "\n" +
                    "                })\n" +
                    "                row.appendChild(cell);\n" +
                    "            }\n" +
                    "\n" +
                    "            // add the row to the end of the table body\n" +
                    "            tblBody.appendChild(row);\n" +
                    "        }\n" +
                    "\n" +
                    "        // put the <tbody> in the <table>\n" +
                    "        tbl.appendChild(tblBody);\n" +
                    "\n" +
                    "        // appends <table> into <body>\n" +
                    "        var header = document.createElement(\"h1\");\n" +
                    "        var headerText = document.createTextNode(\"Random title\");\n" +
                    "        header.appendChild(headerText);\n" +
                    "        document.body.appendChild(header);\n" +
                    "        document.body.appendChild(tbl);\n" +
                    "\n" +
                    "        // sets the border attribute of tbl to '2'\n" +
                    "    }\n" +
                    "</script>\n" +
                    "</body>\n" +
                    "</html>\n";
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }








}
