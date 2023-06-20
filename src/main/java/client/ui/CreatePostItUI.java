package client.ui;

import client.SharedBoardApp;
import messageUtils.SBPMessage;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;

public class CreatePostItUI implements Runnable {
    static final int ERR_CODE = 3;

    public BufferedReader in;
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

            String selectedBoard = InputHandler.showAndSelectBoard(ownedBoardsMessage, in); //select board



            //String selectedBoard = "Board 1";

            String selectedCell = InputHandler.selectCell(in); // chose cell position

            String text = InputHandler.inputText(in);



            System.out.println("Would you like to post image? (y/n)");
            String input = in.readLine();
            boolean rightAnswer = rightAnswer(input);
            while(!rightAnswer){
                System.out.println("\nIncorrect input");
                System.out.println("Would you like to post image? (y/n)");
                input = in.readLine();
                rightAnswer = rightAnswer(input);
            }

            String dataToSend;
            if(input.equals("y")){
                String url = InputHandler.inputUrl(in);
                dataToSend = selectedBoard + "\0" + selectedCell + "\0" + text + "\0" + url;
            }else{
                dataToSend = selectedBoard + "\0" + selectedCell + "\0" + text + "\0" + "";
            }
            SBPMessage createPostItMessage = SharedBoardApp.createPostItRequest(in, sOut, sIn, dataToSend);
            System.out.println(createPostItMessage.data());

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }


    }

    public boolean rightAnswer(String input){
        if(input.equals("y") ||input.equals("n")){
            return true;
        }
        return false;
    }

}
