package client.ui;

import client.SharedBoardApp;
import messageUtils.MessageService;
import org.junit.*;
import java.io.*;
import java.net.*;

public class SharedBoardAppTest {
    private static final String SERVER_IP = "127.0.0.1";
    private static final int DEFAULT_PORT = 12345;

    private SharedBoardApp app;
    private BufferedReader in;
    private ByteArrayOutputStream out;
    MessageService messageService;

    @Before
    public void setUp() {
        app = new SharedBoardApp();
        in = new BufferedReader(new StringReader(""));
        out = new ByteArrayOutputStream();
        messageService = new MessageService();
    }

    @Test
    public void testAuthRequest() throws IOException {
        Socket mockSocket = createMockSocket();
        app.sock = mockSocket;
        app.authRequest(in, new DataOutputStream(out), new DataInputStream(mockSocket.getInputStream()));
        String output = out.toString();
        Assert.assertTrue(output.contains("Invalid option.")); // Assuming an invalid option is selected
    }

    // Add more test cases for other methods

    private Socket createMockSocket() {
        return new Socket() {
            @Override
            public OutputStream getOutputStream() {
                return new ByteArrayOutputStream();
            }

            @Override
            public InputStream getInputStream() {
                return new ByteArrayInputStream("".getBytes());
            }
        };
    }
}