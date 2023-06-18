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
            String selectedBoard = showAndSelectBoard(in); //select board

            String selectedCell = selectCell(in); // chose cell position

            String text = inputText(in);

            String dataToSend = selectedBoard + "\0" + selectedCell + "\0" + text;
            SBPMessage createPostItMessage = SharedBoardApp.createPostItRequest(in, sOut, sIn, dataToSend);
            System.out.println(createPostItMessage.data());

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
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

    public static String showAndSelectBoard(BufferedReader in) throws Exception{
        File dir = new File(".");
        List<String> list = Arrays.asList(dir.list(
                new FilenameFilter() {
                    @Override public boolean accept(File dir, String name) {
                        return name.endsWith(".html");
                    }
                }
        ));

        System.out.println("\nSelect a board by index:");

        int idx = 1;
        for (String s:
             list) {
            System.out.println(idx+". "+s);
            idx++;
        }
        int option = Integer.parseInt(in.readLine());
        if(option>list.size()){
            System.out.println("\nInvalid option! Try again:");
            System.out.println("Select board by number: ");
            option = Integer.parseInt(in.readLine());
        }
        return list.get(option-1);
    }
}
