package client.ui;

import client.SharedBoardApp;
import messageUtils.SBPMessage;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ShareBoardUI implements Runnable {

    static final int ACK_CODE = 2;
    static final int ERR_CODE = 3;

    BufferedReader in;
    DataInputStream sIn;
    DataOutputStream sOut;

    public ShareBoardUI(BufferedReader in, DataOutputStream sOut, DataInputStream sIn) {
        this.in = in;
        this.sIn = sIn;
        this.sOut = sOut;
    }

    @Override
    public void run() {
        try {
            SBPMessage ownedBoardsMessage = SharedBoardApp.ownedBoardsRequest(in, sOut, sIn); //request all owned board by user

            if(ownedBoardsMessage.code() == ERR_CODE) {       //verify message code
                throw new RuntimeException(ownedBoardsMessage.data());
            }

            String selectedBoard = showAndSelectBoard(ownedBoardsMessage, in); //select board

            String usersToShare = selectUsersToShare(in); // chose all board participants
            if(!usersToShare.isEmpty()) {
                String dataToSend = selectedBoard + "\0" + usersToShare;
                SBPMessage shareBoardMessage = SharedBoardApp.shareBoardRequest(in, sOut, sIn, dataToSend); //send share request
                System.out.println(shareBoardMessage.data()); // show responses
            } else {
                System.out.println("\nOperation canceled.\n");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    public static String selectUsersToShare(BufferedReader in) throws IOException {
        String data = "";
        String readPermissions;
        String writePermissions;
        do {
            System.out.println("\nEnter the email addresses of the people you want to add to the board  (0 to stop adding)");
            String email = in.readLine();
            if(!email.equals("0")) {
                System.out.println("\nRead permissions? (Y/N)"); //Permission
                do {
                    readPermissions = in.readLine();
                    if(!readPermissions.equals("Y") && !readPermissions.equals("N")) {
                        System.out.println("\nInvalid option. Please, try again:");
                    } else {
                        break;
                    }
                } while(true);
                System.out.println("\nWrite permissions? (Y/N)");
                do {
                    writePermissions = in.readLine();
                    if(!writePermissions.equals("Y") && !writePermissions.equals("N")) {
                        System.out.println("\nInvalid option. Please, try again:");
                    } else {
                        break;
                    }
                } while(true);
                data = data.concat(email + ";" + readPermissions + ";" + writePermissions + "\0"); //User info (add)
            } else {
                return data;
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
