package client.ui;

import messageUtils.SBPMessage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.net.URL;

public class InputHandler {
    public static String inputUrl(BufferedReader in) throws IOException {
        String data ="";
        do {
            try{
                System.out.println("\nEnter the post it url: ");
                String input = in.readLine();

                Image image = ImageIO.read(new URL(input));
                while(image==null){
                    System.out.println("NOT IMAGE");
                    System.out.println("Enter the post it url: ");
                    input = in.readLine();
                    image = ImageIO.read(new URL(input));
                }
                System.out.println("Image imported");

                data = data.concat(input + "\0");

                return data;

            } catch(Exception e) {
                System.out.println("Error: " + e.getMessage());
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
