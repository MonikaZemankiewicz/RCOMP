package client.ui;

import org.junit.*;
import java.io.*;

public class CreatePostItUITest {
    private CreatePostItUI createPostItUI;
    private BufferedReader in;
    private ByteArrayOutputStream out;

    @Before
    public void setUp() {
        in = new BufferedReader(new StringReader(""));
        out = new ByteArrayOutputStream();
        createPostItUI = new CreatePostItUI(in, new DataOutputStream(out), new DataInputStream(new ByteArrayInputStream("".getBytes())));
    }

    @Test
    public void testRun() {
        // Set up input for the test
        String input = "n";
        in = new BufferedReader(new StringReader(input + System.lineSeparator()));
        createPostItUI.in = in;

        // Run the method under test
        createPostItUI.run();

        // Assert on the output or behavior
        String output = out.toString();
        Assert.assertTrue(output.contains("Would you like to post image? (y/n)"));
        // ... add more assertions based on the expected behavior
    }

    // Add more test cases for different scenarios and edge cases

    // Helper method for setting up mock input
    private void setInput(String input) {
        in = new BufferedReader(new StringReader(input + System.lineSeparator()));
        createPostItUI.in=in;
    }
}