package server;

import messageUtils.MessageService;
import messageUtils.SBPMessage;
import messageUtils.SharedConstants;

import java.awt.*;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;


import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;

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
                        System.out.println("Open board request coming from " + clientIP.getHostAddress() + ", port number " + s.getPort());
                        shareBoardResponse(sOut, sIn);
                        break;

                    case SharedConstants.CREATE_POST_IT_REQUEST_CODE:
                        System.out.println("Create Post It request coming from " + clientIP.getHostAddress() + ", port number " + s.getPort());
                        createPostItResponse(message, sOut, sIn);
                        break;

                    case SharedConstants.UPDATE_POST_IT_TEXT_REQUEST_CODE:
                        System.out.println("Update Post It text request coming from " + clientIP.getHostAddress() + ", port number " + s.getPort());
                        updatePostItTextResponse(message, sOut, sIn);
                        break;

                    case SharedConstants.UPDATE_POST_IT_URL_REQUEST_CODE:
                        System.out.println("Update Post It url request coming from " + clientIP.getHostAddress() + ", port number " + s.getPort());
                        updatePostItUrlResponse(message, sOut, sIn);
                        break;

                    case SharedConstants.REMOVE_POST_IT_REQUEST_CODE:
                        System.out.println("Remove Post It url request coming from " + clientIP.getHostAddress() + ", port number " + s.getPort());
                        removePostItResponse(message, sOut, sIn);
                        break;

                    case SharedConstants.UPDATE_POST_IT_CELL_REQUEST_CODE:
                        System.out.println("Update Post It cell request coming from " + clientIP.getHostAddress() + ", port number " + s.getPort());
                        movePostItResponse(message, sOut, sIn);
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
        String[][] userdata = {
            {"username","password"},
            {"admin","password"},
        };
        String username = message.data().split("\0")[0];
        String password = message.data().split("\0")[1];

        for (int i=0; i < userdata.length; i++){
            if(userdata[i][0].equals(username) && userdata[i][1].equals(password)){
                SBPMessage responseMessage = new SBPMessage(1, SharedConstants.ACK_CODE, 0, 0, "");
                messageService.sendMessage(responseMessage, sOut);
                break;
            }
        }
        SBPMessage responseMessage = new SBPMessage(1, SharedConstants.ERR_CODE,"");
        messageService.sendMessage(responseMessage, sOut);

    }

    public static void ownedBoardsResponse(DataOutputStream sOut, DataInputStream sIn) throws IOException {
        SBPMessage responseMessage;

        /*File dir = new File(".");
        List<String> list = Arrays.asList(dir.list(
                new FilenameFilter() {
                    @Override public boolean accept(File dir, String name) {
                        return name.endsWith(".html");
                    }
                }
        ));
        */

        String data = "Board 1" + "\0";
        /*
        int idx = 1;
        for (String s:
                list) {
            data = data.concat(s + "\0");
            idx++;
        }

         */


        responseMessage = new SBPMessage(SharedConstants.MESSAGE_VERSION, SharedConstants.OWNED_BOARDS_RESPONSE_CODE, data);

        //send response
        messageService.sendMessage(responseMessage, sOut);
    }

    public static void shareBoardResponse(DataOutputStream sOut, DataInputStream sIn) throws IOException {
        Desktop desktop = java.awt.Desktop.getDesktop();
        URI uri = URI.create("http://localhost:8000/");
        try {
            desktop.browse(uri);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        SBPMessage responseMessage = new SBPMessage(SharedConstants.MESSAGE_VERSION, SharedConstants.SHARE_BOARD_RESPONSE_CODE, "Board opened in the browser");
        //finally send response
        messageService.sendMessage(responseMessage, sOut);
    }

    public static void createPostItResponse(SBPMessage message, DataOutputStream sOut, DataInputStream sIn) throws IOException {
        SBPMessage responseMessage;
        char currentChar;
        int currentCharIndex = 0;
        boolean alreadyExists = false;
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

        String[] splittedUserInfo = positionData.split(";");

        JSONParser jsonParser = new JSONParser();

        try (FileReader reader = new FileReader("Postits.json"))
        {


            //Read JSON file
            Object obj = jsonParser.parse(reader);

            JSONArray postItsList = (JSONArray) obj;

            for (int i = 0; i < postItsList.size(); ++i) {
                JSONObject postit = (JSONObject) postItsList.get(i);
                JSONObject postitData = (JSONObject) postit.get("postit");
                if (postitData.get("row").toString().equals(splittedUserInfo[0].toString()) && postitData.get("column").toString().equals(splittedUserInfo[1].toString())) {
                    alreadyExists = true;
                }
            }

            if(!alreadyExists){
                JSONObject newPostitDetails = new JSONObject();
                newPostitDetails.put("board", boardName);
                newPostitDetails.put("row", splittedUserInfo[0]);
                newPostitDetails.put("column", splittedUserInfo[1]);
                newPostitDetails.put("text", text);
                newPostitDetails.put("url", url);

                JSONObject postItObject = new JSONObject();
                postItObject.put("postit", newPostitDetails);

                postItsList.add(postItObject);

                try (FileWriter file = new FileWriter("Postits.json")) {
                    file.write(postItsList.toJSONString());
                    file.flush();

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(alreadyExists){
            responseMessage = new SBPMessage(SharedConstants.MESSAGE_VERSION, SharedConstants.CREATE_POST_IT_RESPONSE_CODE, "\nThere is already a post it in that cell\n");
        } else {
            responseMessage = new SBPMessage(SharedConstants.MESSAGE_VERSION, SharedConstants.CREATE_POST_IT_RESPONSE_CODE, "\nThe post it was created successfully!\n");
        }


        //finally send response
        messageService.sendMessage(responseMessage, sOut);

    }

    public static void updatePostItTextResponse(SBPMessage message, DataOutputStream sOut, DataInputStream sIn) throws IOException {
        SBPMessage responseMessage;
        char currentChar;
        int currentCharIndex = 0;
        String positionData = "";
        String text = "";
        boolean found = false;

        while ((currentChar = message.data().charAt(currentCharIndex)) != '\0') {
            positionData = positionData.concat(String.valueOf(currentChar));
            currentCharIndex++;

        }

        currentCharIndex++;
        currentCharIndex++;

        do {
            while ((currentChar = message.data().charAt(currentCharIndex)) != '\0') {
                text = text.concat(String.valueOf(currentChar));
                currentCharIndex++;
            }
            currentCharIndex++;

        } while (currentCharIndex < (message.d_length_1() + message.d_length_2()));

        String[] splittedUserInfo = positionData.split(";");

        JSONParser jsonParser = new JSONParser();

        try (FileReader reader = new FileReader("Postits.json"))
        {

            Object obj = jsonParser.parse(reader);
            JSONArray postItsList = (JSONArray) obj;

            for (int i = 0; i < postItsList.size(); ++i) {
                JSONObject postit = (JSONObject) postItsList.get(i);
                JSONObject postitData = (JSONObject) postit.get("postit");
                if (postitData.get("row").toString().equals(splittedUserInfo[0].toString()) && postitData.get("column").toString().equals(splittedUserInfo[1].toString())){
                    found = true;
                    postItsList.remove(i);
                    postitData.put("text", text);
                    postit.put("postit", postitData);
                    postItsList.add(postit);

                    try (FileWriter file = new FileWriter("Postits.json")) {
                        //We can write any JSONArray or JSONObject instance to the file
                        file.write(postItsList.toJSONString());
                        file.flush();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(found){
            responseMessage = new SBPMessage(SharedConstants.MESSAGE_VERSION, SharedConstants.UPDATE_POST_IT_RESPONSE_CODE, "\nThe post it was updated successfully!\n");
        } else {
            responseMessage = new SBPMessage(SharedConstants.MESSAGE_VERSION, SharedConstants.UPDATE_POST_IT_RESPONSE_CODE, "\nThe post it with chosen position does not exist\n");

        }

        //finally send response
        messageService.sendMessage(responseMessage, sOut);

    }

    public static void updatePostItUrlResponse(SBPMessage message, DataOutputStream sOut, DataInputStream sIn) throws IOException {
        SBPMessage responseMessage;
        char currentChar;
        int currentCharIndex = 0;
        String positionData = "";
        String url = "";
        boolean found = false;

        while ((currentChar = message.data().charAt(currentCharIndex)) != '\0') {
            positionData = positionData.concat(String.valueOf(currentChar));
            currentCharIndex++;

        }

        currentCharIndex++;
        currentCharIndex++;

        do {
            while ((currentChar = message.data().charAt(currentCharIndex)) != '\0') {
                url = url.concat(String.valueOf(currentChar));
                currentCharIndex++;
            }
            currentCharIndex++;

        } while (currentCharIndex < (message.d_length_1() + message.d_length_2()));

        String[] splittedUserInfo = positionData.split(";");

        JSONParser jsonParser = new JSONParser();

        try (FileReader reader = new FileReader("Postits.json"))
        {

            Object obj = jsonParser.parse(reader);
            JSONArray postItsList = (JSONArray) obj;

            for (int i = 0; i < postItsList.size(); ++i) {
                JSONObject postit = (JSONObject) postItsList.get(i);
                JSONObject postitData = (JSONObject) postit.get("postit");
                if (postitData.get("row").toString().equals(splittedUserInfo[0].toString()) && postitData.get("column").toString().equals(splittedUserInfo[1].toString())){
                    found = true;
                    postItsList.remove(i);
                    postitData.put("url", url);
                    postit.put("postit", postitData);
                    postItsList.add(postit);

                    try (FileWriter file = new FileWriter("Postits.json")) {
                        //We can write any JSONArray or JSONObject instance to the file
                        file.write(postItsList.toJSONString());
                        file.flush();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(found){
            responseMessage = new SBPMessage(SharedConstants.MESSAGE_VERSION, SharedConstants.UPDATE_POST_IT_RESPONSE_CODE, "\nThe post it was updated successfully!\n");
        } else {
            responseMessage = new SBPMessage(SharedConstants.MESSAGE_VERSION, SharedConstants.UPDATE_POST_IT_RESPONSE_CODE, "\nThe post it with chosen position does not exist\n");

        }

        messageService.sendMessage(responseMessage, sOut);
    }

    public static void removePostItResponse(SBPMessage message, DataOutputStream sOut, DataInputStream sIn) throws IOException {
        SBPMessage responseMessage;
        char currentChar;
        int currentCharIndex = 0;
        String positionData = "";
        boolean found = false;

        while ((currentChar = message.data().charAt(currentCharIndex)) != '\0') {
            positionData = positionData.concat(String.valueOf(currentChar));
            currentCharIndex++;

        }

        String[] splittedUserInfo = positionData.split(";");

        JSONParser jsonParser = new JSONParser();

        try (FileReader reader = new FileReader("Postits.json"))
        {

            Object obj = jsonParser.parse(reader);
            JSONArray postItsList = (JSONArray) obj;

            for (int i = 0; i < postItsList.size(); ++i) {
                JSONObject postit = (JSONObject) postItsList.get(i);
                JSONObject postitData = (JSONObject) postit.get("postit");
                if (postitData.get("row").toString().equals(splittedUserInfo[0].toString()) && postitData.get("column").toString().equals(splittedUserInfo[1].toString())){
                    found = true;
                    postItsList.remove(i);

                    try (FileWriter file = new FileWriter("Postits.json")) {
                        //We can write any JSONArray or JSONObject instance to the file
                        file.write(postItsList.toJSONString());
                        file.flush();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(found){
            responseMessage = new SBPMessage(SharedConstants.MESSAGE_VERSION, SharedConstants.UPDATE_POST_IT_RESPONSE_CODE, "\nThe post it was deleted successfully!\n");
        } else {
            responseMessage = new SBPMessage(SharedConstants.MESSAGE_VERSION, SharedConstants.UPDATE_POST_IT_RESPONSE_CODE, "\nThe post it with chosen position does not exist\n");

        }

        messageService.sendMessage(responseMessage, sOut);
    }

    public static void movePostItResponse(SBPMessage message, DataOutputStream sOut, DataInputStream sIn) throws IOException {
        SBPMessage responseMessage;
        char currentChar;
        int currentCharIndex = 0;
        String oldPositionData = "";
        String newPositionData = "";
        String url = "";
        boolean found = false;

        while ((currentChar = message.data().charAt(currentCharIndex)) != '\0') {
            oldPositionData = oldPositionData.concat(String.valueOf(currentChar));
            currentCharIndex++;

        }

        currentCharIndex++;
        currentCharIndex++;

        do {
            while ((currentChar = message.data().charAt(currentCharIndex)) != '\0') {
                newPositionData = newPositionData.concat(String.valueOf(currentChar));
                currentCharIndex++;
            }
            currentCharIndex++;

        } while (currentCharIndex < (message.d_length_1() + message.d_length_2()));

        String[] oldPosition = oldPositionData.split(";");
        String[] newPosition = newPositionData.split(";");


        JSONParser jsonParser = new JSONParser();

        try (FileReader reader = new FileReader("Postits.json"))
        {

            Object obj = jsonParser.parse(reader);
            JSONArray postItsList = (JSONArray) obj;

            for (int i = 0; i < postItsList.size(); ++i) {
                JSONObject postit = (JSONObject) postItsList.get(i);
                JSONObject postitData = (JSONObject) postit.get("postit");
                if (postitData.get("row").toString().equals(oldPosition[0].toString()) && postitData.get("column").toString().equals(oldPosition[1].toString())){
                    found = true;
                    postItsList.remove(i);
                    postitData.put("row", newPosition[0]);
                    postitData.put("column", newPosition[1]);
                    postit.put("postit", postitData);
                    postItsList.add(postit);

                    try (FileWriter file = new FileWriter("Postits.json")) {
                        //We can write any JSONArray or JSONObject instance to the file
                        file.write(postItsList.toJSONString());
                        file.flush();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }



        if(found){
            responseMessage = new SBPMessage(SharedConstants.MESSAGE_VERSION, SharedConstants.UPDATE_POST_IT_RESPONSE_CODE, "\nThe post it was updated successfully!\n");
        } else {
            responseMessage = new SBPMessage(SharedConstants.MESSAGE_VERSION, SharedConstants.UPDATE_POST_IT_RESPONSE_CODE, "\nThe post it with chosen position does not exist\n");

        }

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

}
