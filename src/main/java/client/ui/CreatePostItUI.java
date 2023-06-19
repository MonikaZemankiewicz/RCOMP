package client.ui;

import client.SharedBoardApp;
import messageUtils.SBPMessage;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CreatePostItUI implements Runnable {
    static final int ERR_CODE = 3;

    BufferedReader in;
    DataInputStream sIn;
    DataOutputStream sOut;

    public CreatePostItUI(BufferedReader in, DataOutputStream sOut, DataInputStream sIn) {
        this.in = in;
        this.sIn = sIn;
        this.sOut = sOut;
    }

    @Override
    public void run() {
        try {
            SBPMessage ownedBoardsMessage = SharedBoardApp.ownedBoardsRequest(sOut, sIn); //request all owned board by user

            if(ownedBoardsMessage.code() == ERR_CODE) {       //verify message code
                throw new RuntimeException(ownedBoardsMessage.data());
            }

            String selectedBoard = showAndSelectBoard(ownedBoardsMessage, in); //select board

            String selectedCell = selectCell(in); // chose cell position

            String text = inputText(in);

            String url = inputUrl(in);

            String dataToSend = selectedBoard + "\0" + selectedCell + "\0" + text + "\0" + url;
            SBPMessage createPostItMessage = SharedBoardApp.createPostItRequest(in, sOut, sIn, dataToSend);
            System.out.println(createPostItMessage.data());

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static String inputUrl(BufferedReader in) throws IOException {
        String data ="";
        do {
            try{
                System.out.println("\nEnter the post it url: ");
                String input = in.readLine();

                data = data.concat(input + "\0");

                return data;

            } catch(Exception e) {
                System.out.println("\nInvalid option. Please, try again:");
            }

        } while(true);
    }
    public static String inputText(BufferedReader in) throws IOException {
        String data ="";
        do {
            try{
                System.out.println("\nEnter the post it text: ");
                String input = in.readLine();

                data = data.concat(input + "\0");

                return data;

            } catch(Exception e) {
                System.out.println("\nInvalid option. Please, try again:");
            }

        } while(true);
    }
    public static String selectCell(BufferedReader in) throws IOException {
        String data = "";
        do {
            try {
                System.out.println("\nEnter the row number: ");
                String row = in.readLine();

                System.out.println("\nEnter the column number: ");
                String column = in.readLine();

                data = data.concat(row + ";" + column +"\0");

                return data;
            } catch(Exception e) {
                System.out.println("\nInvalid option. Please, try again:");
            }

        } while(true);
    }

    public static String showAndSelectBoard(SBPMessage responseMessage, BufferedReader in) throws IOException {
        String message = responseMessage.data();
        int currentIndex = 0;
        int currentBoardNumber = 1;
        char currentChar;
        String currentBoardName = "";
        List<String> boardsNames = new ArrayList<>();

        System.out.println("\nSelect a board:\n");

        while(currentIndex < (responseMessage.d_length_1() + responseMessage.d_length_2())) {
            while((currentChar = message.charAt(currentIndex)) != '\0') {
                currentBoardName = currentBoardName.concat(String.valueOf(currentChar));
                currentIndex++;
            }
            System.out.printf("%d - %s\n", currentBoardNumber, currentBoardName);  //Print Board information
            boardsNames.add(currentBoardName);
            currentBoardNumber++;
            currentIndex++;
            currentBoardName = "";
        }
        do {
            try {
                int option = Integer.parseInt(in.readLine());
                if(option < 0 || option > boardsNames.size()) {
                    throw new Exception();
                }
                return (boardsNames.get(option - 1));
            } catch(Exception e) {
                System.out.println("\nInvalid option. Please, try again:");
            }
        } while(true);
    }
}
