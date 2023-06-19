package boardService;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Scanner;

public class JSONgenerator {
    static String boardName = "src/main/java/client/httpServer/www/index.html";
    public static void generateJSON(String boardName, String title, int rows, int cols) {
        BoardInfo boardInfo = new BoardInfo();
        boardInfo.setBoardName(boardName);
        boardInfo.setTitle(title);

        for (int row = 1; row <= rows; row++) {
            for (int col = 1; col <= cols; col++) {
                CellInfo cellInfo = new CellInfo();
                cellInfo.setId((row - 1) * cols + col);
                cellInfo.setContent("");
                cellInfo.setUrl("");
                boardInfo.addCell(cellInfo);
            }
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(boardInfo);

        // Write the JSON to a file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(boardName+".json"))) {
            writer.write(json);
            System.out.println("JSON file generated successfully.");
        } catch (IOException e) {
            System.out.println("Error generating JSON file: " + e.getMessage());
        }
    }
    public static void updateCellContent(String path) {
        // Read the initial data
        Scanner sc = new Scanner(System.in);
        System.out.println("ID of cell: ");
        int cellId = Integer.parseInt(sc.nextLine());
        System.out.println("Content of postit: ");
        String content = sc.nextLine();

        // Read the JSON file and update the content
        Gson gson = new Gson();
        try {
            String json = Files.readString(Path.of(path+".json"), StandardCharsets.UTF_8);
            BoardInfo boardInfo = gson.fromJson(json, BoardInfo.class);
            CellInfo[] cells = boardInfo.getCells();

            for (CellInfo cell : cells) {
                if (cell.getId() == cellId) {
                    cell.setContent(content);
                    break;
                }else if(cellId > cells.length){
                    System.out.println("No cell with this id!");
                    break;
                }
            }

            String updatedJson = gson.toJson(boardInfo);

            // Write the updated JSON back to the file
            Files.writeString(Path.of(path+".json"), updatedJson, StandardCharsets.UTF_8, StandardOpenOption.WRITE);

            System.out.println("Cell content updated successfully.");
        } catch (IOException e) {
            System.out.println("Error updating cell content: " + e.getMessage());
        }
    }
}
