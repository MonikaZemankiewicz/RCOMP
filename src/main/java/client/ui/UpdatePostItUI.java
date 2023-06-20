package client.ui;

import client.SharedBoardApp;
import messageUtils.SBPMessage;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

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
            System.out.println("0 - Go back");



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
                    try {
                        updateUrl(in, sOut, sIn);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    break;
                case "3":
                    try {
                        movePostit(in, sOut, sIn);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    break;
                case "4":
                    try {
                        removePostit(in, sOut, sIn);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                case "0":
                    return;

                default:
                    System.out.println("\nInvalid option.\n");
                    break;
            }
        } while (true);



    }

    public static void updateText(BufferedReader in, DataOutputStream sOut, DataInputStream sIn) throws IOException {
        String selectedCell = InputHandler.selectCell(in);
        String newText = InputHandler.inputText(in);

        String dataToSend = selectedCell + "\0" + newText + "\0";
        SBPMessage updatePostItTextMessage = SharedBoardApp.updatePostItTextRequest(in, sOut, sIn, dataToSend);
        System.out.println(updatePostItTextMessage.data());

    }

    public static void updateUrl(BufferedReader in, DataOutputStream sOut, DataInputStream sIn) throws IOException {
        String selectedCell = InputHandler.selectCell(in);
        String newUrl = InputHandler.inputUrl(in);

        String dataToSend = selectedCell + "\0" + newUrl + "\0";
        SBPMessage updatePostItTextMessage = SharedBoardApp.updatePostItUrlRequest(in, sOut, sIn, dataToSend);
        System.out.println(updatePostItTextMessage.data());

    }

    public static void removePostit(BufferedReader in, DataOutputStream sOut, DataInputStream sIn) throws IOException {
        String selectedCell = InputHandler.selectCell(in);

        String dataToSend = selectedCell + "\0";
        SBPMessage updatePostItTextMessage = SharedBoardApp.removePostItRequest(in, sOut, sIn, dataToSend);
        System.out.println(updatePostItTextMessage.data());

    }

    public static void movePostit(BufferedReader in, DataOutputStream sOut, DataInputStream sIn) throws IOException {
        System.out.println("\nSelect Post It to move: ");
        String oldPosition = InputHandler.selectCell(in);
        System.out.println("\nSelect new cell: ");
        String newPosition = InputHandler.selectCell(in);

        String dataToSend = oldPosition + "\0" + newPosition;
        SBPMessage updatePostItTextMessage = SharedBoardApp.movePostItRequest(in, sOut, sIn, dataToSend);
        System.out.println(updatePostItTextMessage.data());

    }

}
