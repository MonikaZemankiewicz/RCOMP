package client.ui;

import messageUtils.SBPMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class InputHandlerTest {
    private BufferedReader bufferedReader;

    @BeforeEach
    void setUp() {
        bufferedReader = mock(BufferedReader.class);
    }

    @Test
    void validUrlTest() throws IOException {
        String validUrl = "https://hungarytoday.hu/wp-content/uploads/2022/04/277176181_499116548245498_3884589768241132949_n.jpg";
        when(bufferedReader.readLine()).thenReturn(validUrl);

        String expectedData = validUrl + "\0";
        String actualData = InputHandler.inputUrl(bufferedReader);

        assertEquals(expectedData, actualData);
    }

    @Test
    void validInputText() throws IOException {
        String validInput = "Hello, world!";
        when(bufferedReader.readLine()).thenReturn(validInput);

        String expectedData = validInput + "\0";
        String actualData = InputHandler.inputText(bufferedReader);

        assertEquals(expectedData, actualData);
    }

    @Test
    void selectCell_ValidInput_ReturnsData() throws IOException {
        String validRow = "2";
        String validColumn = "3";
        when(bufferedReader.readLine()).thenReturn(validRow, validColumn);

        String expectedData = validRow + ";" + validColumn + "\0";
        String actualData = InputHandler.selectCell(bufferedReader);

        assertEquals(expectedData, actualData);
    }

}