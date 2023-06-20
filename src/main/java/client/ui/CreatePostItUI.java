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

            String selectedBoard = inputHandler.showAndSelectBoard(ownedBoardsMessage, in); //select board



            //String selectedBoard = "Board 1";

            String selectedCell = inputHandler.selectCell(in); // chose cell position

            String text = inputHandler.inputText(in);

            String url = inputHandler.inputUrl(in);

            String dataToSend = selectedBoard + "\0" + selectedCell + "\0" + text + "\0" + url;

            SBPMessage createPostItMessage = SharedBoardApp.createPostItRequest(in, sOut, sIn, dataToSend);
            System.out.println(createPostItMessage.data());

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }


    }

}
