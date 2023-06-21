package client.ui;
import messageUtils.MessageService;
import messageUtils.SBPMessage;
import messageUtils.SharedConstants;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import javax.xml.crypto.Data;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import static org.mockito.Mockito.mock;

public class MessageServiceTest {
    private MessageService messageService;
    private DataOutputStream outputStream;
    private DataInputStream inputStream;

    @BeforeEach
    public void setUp() {
        messageService = new MessageService();
        outputStream =  mock(DataOutputStream.class);
        inputStream = mock(DataInputStream.class);
    }

    @Test
    public void testSendMessage() throws IOException {
        // Create a sample SBPMessage
        SBPMessage message = new SBPMessage(1, 3, "Test Message");

        // Verify the output bytes
        String expectedOutput = "Test Message";
        String actualOutput = message.data();
        Assertions.assertEquals(expectedOutput, actualOutput);
    }


    @Test
    public void testReadMessage() throws IOException {
        SBPMessage sentMessage = new SBPMessage(1, 2, "Test Message");
        // Verify the returned SBPMessage
        Assertions.assertEquals(1, sentMessage.version());
        Assertions.assertEquals(2, sentMessage.code());
        Assertions.assertEquals("Test Message", sentMessage.data());
        }
}
