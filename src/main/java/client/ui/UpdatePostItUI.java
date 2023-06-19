package client.ui;

import client.SharedBoardApp;
import messageUtils.SBPMessage;

import java.awt.*;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class UpdatePostItUI implements Runnable {
    static final int ERR_CODE = 3;

    BufferedReader in;
    DataInputStream sIn;
    DataOutputStream sOut;

    public UpdatePostItUI(BufferedReader in, DataOutputStream sOut, DataInputStream sIn) {
        this.in = in;
        this.sIn = sIn;
        this.sOut = sOut;
    }

    @Override
    public void run() {
        String option;
        do {
            System.out.println("Choose the action:\n");
            System.out.println("1 - Update text");
            System.out.println("2 - Update url");
            System.out.println("3 - Move to a different cell");
            System.out.println("4 - Remove post it");


            try {
                option = in.readLine();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            SBPMessage responseMessage;
            switch(option) {
                case "1":
                    try {
                        updateText(in, sOut, sIn);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                case "2":

                    break;
                case "3":

                    break;
                case "4":

                    break;
                default:
                    System.out.println("\nInvalid option.\n");
                    break;
            }
        } while (true);



    }

    public static void updateText(BufferedReader in, DataOutputStream sOut, DataInputStream sIn) throws IOException {
        String selectedCell = selectCell(in);
        String newText = inputText(in);

        String dataToSend = selectedCell + "\0" + newText + "\0";
        SBPMessage updatePostItTextMessage = SharedBoardApp.updatePostItTextRequest(in, sOut, sIn, dataToSend);
        System.out.println(updatePostItTextMessage.data());

    }
    public static String inputText(BufferedReader in) throws IOException {
        String data ="";
        do {
            try{
                System.out.println("\nEnter the new post it text: ");
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
