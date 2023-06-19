package server;

import boardService.PostItInfo;
import client.httpServer.SimpleHttpServer;
import com.google.gson.stream.JsonReader;
import messageUtils.MessageService;
import messageUtils.SBPMessage;
import messageUtils.SharedConstants;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;




import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class SharedBoardServerThread implements Runnable {

    private Socket s;
    private DataOutputStream sOut;
    private DataInputStream sIn;
    private static MessageService messageService;

    public SharedBoardServerThread(Socket cli_s) {
        s = cli_s;
    }

    public void run() {
        InetAddress clientIP;

        clientIP = s.getInetAddress();
        System.out.println("New client connection from " + clientIP.getHostAddress() +
                ", port number " + s.getPort());

        SimpleHttpServer server = new SimpleHttpServer();
        server.run();
        System.out.println("HTTP Server started");

        try {
            sOut = new DataOutputStream(s.getOutputStream());
            sIn = new DataInputStream(s.getInputStream());
            messageService = new MessageService();
            do {
                SBPMessage message = messageService.readMessage(sIn);

                switch (message.code()) {
                    case SharedConstants.COMMTEST_CODE:
                        System.out.println("Test request coming from " + clientIP.getHostAddress() + ", port number " + s.getPort());
                        commtestResponse(sOut, sIn);
                        break;

                    case SharedConstants.DISCONN_CODE:
                        System.out.println("Disconnect request coming from " + clientIP.getHostAddress() + ", port number " + s.getPort());
                        disconnResponse(sOut, sIn, s);
                        break;

                    case SharedConstants.AUTH_CODE:
                        System.out.println("Authentication request coming from " + clientIP.getHostAddress() + ", port number " + s.getPort());
                        authResponse(sOut, sIn, message);
                        break;

                    case SharedConstants.OWNED_BOARDS_REQUEST_CODE:
                        System.out.println("Owned boards request coming from " + clientIP.getHostAddress() + ", port number " + s.getPort());
                        ownedBoardsResponse(sOut, sIn);
                        break;

                    case SharedConstants.SHARE_BOARD_REQUEST_CODE:
                        System.out.println("Share board request coming from " + clientIP.getHostAddress() + ", port number " + s.getPort());
                        shareBoardResponse(message, sOut, sIn);
                        break;

                    case SharedConstants.CREATE_POST_IT_REQUEST_CODE:
                        System.out.println("Create Post It request coming from " + clientIP.getHostAddress() + ", port number " + s.getPort());
                        createPostItResponse(message, sOut, sIn);
                        break;
                }
            } while (true);
        } catch (IOException ex) {
            System.out.println(clientIP.getHostAddress() + " disconnected successfully");
        }
    }

    public static void commtestResponse(DataOutputStream sOut, DataInputStream sIn) throws IOException {
        //write response
        SBPMessage responseMessage = new SBPMessage(1, SharedConstants.ACK_CODE, 0, 0, "");
        messageService.sendMessage(responseMessage, sOut);
    }

    public static void disconnResponse(DataOutputStream sOut, DataInputStream sIn, Socket s) throws IOException {
        //write response and close tcp connection
        SBPMessage responseMessage = new SBPMessage(1, SharedConstants.ACK_CODE, 0, 0, "");
        messageService.sendMessage(responseMessage, sOut);

        sOut.close();
        sIn.close();
        s.close();
    }

    public static void authResponse(DataOutputStream sOut, DataInputStream sIn, SBPMessage message) throws IOException {
        String username = "";
        String password = "";
        char currentChar;
        int currentCharIndex = 0;

        while (((currentChar = message.data().charAt(currentCharIndex)) != '\0') && currentCharIndex < (message.d_length_1() + message.d_length_2())) {  //read username
            username = username.concat(String.valueOf(currentChar));
            currentCharIndex++;
        }
        currentCharIndex++;

        while (((currentChar = message.data().charAt(currentCharIndex)) != '\0') && currentCharIndex < (message.d_length_1() + message.d_length_2())) {  //read password
            password = password.concat(String.valueOf(currentChar));
            currentCharIndex++;
        }

        SBPMessage responseMessage = new SBPMessage(1, SharedConstants.ACK_CODE, 0, 0, "");
        messageService.sendMessage(responseMessage, sOut);

    }

    public static void ownedBoardsResponse(DataOutputStream sOut, DataInputStream sIn) throws IOException {
        SBPMessage responseMessage;

        File dir = new File(".");
        List<String> list = Arrays.asList(dir.list(
                new FilenameFilter() {
                    @Override public boolean accept(File dir, String name) {
                        return name.endsWith(".html");
                    }
                }
        ));

        String data = "";
        int idx = 1;
        for (String s:
                list) {
            data = data.concat(s + "\0");
            idx++;
        }


        responseMessage = new SBPMessage(SharedConstants.MESSAGE_VERSION, SharedConstants.OWNED_BOARDS_RESPONSE_CODE, data);

        //send response
        messageService.sendMessage(responseMessage, sOut);
    }

    public static void shareBoardResponse(SBPMessage message, DataOutputStream sOut, DataInputStream sIn) throws IOException {
        SBPMessage responseMessage;
        String notAddedUsers = "";
        char currentChar;
        int currentCharIndex = 0;
        boolean addedAtLeastOneUser = false;

        //read selected board name
        String boardName = "";

        while ((currentChar = message.data().charAt(currentCharIndex)) != '\0') {
            boardName = boardName.concat(String.valueOf(currentChar));
            currentCharIndex++;
        }

        currentCharIndex++;


        //read users info and share the board to them
        do {
            String userInfo = "";

            while ((currentChar = message.data().charAt(currentCharIndex)) != '\0') {
                userInfo = userInfo.concat(String.valueOf(currentChar));
                currentCharIndex++;
            }

            String[] splittedUserInfo = userInfo.split(";");



            currentCharIndex++;

        } while (currentCharIndex < (message.d_length_1() + message.d_length_2()));



        responseMessage = new SBPMessage(SharedConstants.MESSAGE_VERSION, SharedConstants.SHARE_BOARD_RESPONSE_CODE, "\nThe board was shared with the users successfully!\n");

        //finally send response
        messageService.sendMessage(responseMessage, sOut);
    }

    public static void createPostItResponse(SBPMessage message, DataOutputStream sOut, DataInputStream sIn) throws IOException {
        SBPMessage responseMessage;
        char currentChar;
        int currentCharIndex = 0;

        String boardName = "";
        String positionData = "";
        String text = "";
        String url = "";

        while ((currentChar = message.data().charAt(currentCharIndex)) != '\0') {
            boardName = boardName.concat(String.valueOf(currentChar));
            currentCharIndex++;
        }

        currentCharIndex++;



        while ((currentChar = message.data().charAt(currentCharIndex)) != '\0') {
            positionData = positionData.concat(String.valueOf(currentChar));
            currentCharIndex++;

        }

        currentCharIndex++;
        currentCharIndex++;

        while ((currentChar = message.data().charAt(currentCharIndex)) != '\0') {
            text = text.concat(String.valueOf(currentChar));
            currentCharIndex++;
        }
        currentCharIndex++;


        do {
            while ((currentChar = message.data().charAt(currentCharIndex)) != '\0') {
                url = url.concat(String.valueOf(currentChar));
                currentCharIndex++;
            }
            currentCharIndex++;

        } while (currentCharIndex < (message.d_length_1() + message.d_length_2()));

        //Below data should be saved in the database


        System.out.println("boardName: ");
        System.out.println(boardName);
        String[] splittedUserInfo = positionData.split(";");
        System.out.println("row: ");
        System.out.println(splittedUserInfo[0]);
        System.out.println("column: ");
        System.out.println(splittedUserInfo[1]);
        System.out.println("text: ");
        System.out.println(text);
        System.out.println("url: ");
        System.out.println(url);

        JSONParser jsonParser = new JSONParser();

        try (FileReader reader = new FileReader("Postits.json"))
        {
            //Read JSON file
            Object obj = jsonParser.parse(reader);

            JSONArray postItsList = (JSONArray) obj;

            JSONObject postItDetails = new JSONObject();
            postItDetails.put("board", boardName);
            postItDetails.put("row", splittedUserInfo[0]);
            postItDetails.put("column", splittedUserInfo[1]);
            postItDetails.put("text", text);
            postItDetails.put("url", url);

            JSONObject postItObject = new JSONObject();
            postItObject.put("postit", postItDetails);

            postItsList.add(postItObject);

            try (FileWriter file = new FileWriter("Postits.json")) {
                //We can write any JSONArray or JSONObject instance to the file
                file.write(postItsList.toJSONString());
                file.flush();

            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //SimpleHttpServer server = new SimpleHttpServer();
        //server.run();
        responseMessage = new SBPMessage(SharedConstants.MESSAGE_VERSION, SharedConstants.CREATE_POST_IT_RESPONSE_CODE, "\nThe post it was created successfully!\n");

        //finally send response
        messageService.sendMessage(responseMessage, sOut);

    }

    protected static void sendMessage(SBPMessage message, DataOutputStream sOut) {
        try {
            messageService.sendMessage(message, sOut);
        } catch (Exception e) {
            System.out.println("Could not send message due to the following error-> " + e.getMessage());
            System.exit(1);
        }
    }

    protected static SBPMessage readMessage(DataInputStream sIn) {
        try {
            return messageService.readMessage(sIn);
        } catch (SocketTimeoutException e) {
            System.out.println("Connection Timeout!");
            System.exit(1);
        } catch (Exception e) {
            System.out.println("Could not read message due to the following error -> " + e.getMessage());
            System.exit(1);
        }
        return null;
    }


    private static PostItInfo parseNoteObject(JSONObject postit)
    {

        //Get employee object within list
        JSONObject postitObject = (JSONObject) postit.get("postit");

        //Get employee first name
        String board = (String) postitObject.get("board");

        //Get employee first name
        String row = (String) postitObject.get("row");
        int rowToSave = Integer.parseInt(row);

        //Get employee last name
        String col = (String) postitObject.get("column");
        int colToSave = Integer.parseInt(col);

        //Get employee website name
        String text = (String) postitObject.get("text");

        String url = (String) postitObject.get("url");

        PostItInfo postitToSave = new PostItInfo(board, text, url ,rowToSave, colToSave);

        return postitToSave;
    }
}
