package boardService;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Scanner;

import static boardService.HTMLFileOpener.openFile;

public class GridGenerator {
    public static void generateHTMLGrid(int rows, int cols, String name, String title) {
        String path = name.replaceAll("\\s","");
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>\n");
        html.append("<html>\n");
        html.append("<head>\n");
        html.append("<title>"+name+"</title>\n");
        html.append("<script src=\"https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js\"></script>\n");

        // STYLEING
        html.append("<script src=\"script.js\"></script>\n");
        html.append("<style>\n");
        html.append(".grid {\n");
        html.append("  display: grid;\n");
        html.append("  grid-template-columns: 150px repeat(" + cols + ", 1fr);\n");
        html.append("  grid-template-rows: 30px repeat(" + rows + ", " +(90/rows) +"vh);\n");
        html.append("  gap: 1px;\n");
        html.append("  padding: 10px;\n");
        html.append("}\n");
        html.append(".grid-item {\n");
        html.append("  background-color: lightblue;\n");
        html.append("  padding: 10px;\n");
        html.append("  color: white;\n");
        html.append("  font-size: 16px;");
        html.append("  text-align: center;\n");
        html.append("}\n");
        html.append(".grid-title {\n");
        html.append("  background-color: darkblue;\n");
        html.append("  border-style: solid;\n");
        html.append("  border-color: darkblue;\n");
        html.append("  padding: 10px;\n");
        html.append("  border-radius: 2px;\n");
        html.append("  color: white;\n");
        html.append("  font-size: 16px;");
        html.append("  font-weight: bold;");
        html.append("  text-align: center;\n");
        html.append("  vertical-align: middle;\n");
        html.append("}\n");
        html.append("</style>\n");
/*
        // SCRIPT
        html.append("<script>\n");
        html.append("$(document).ready(function() {\n");
        html.append(   "  loadGrid();\n");
        html.append(   "});\n");
        html.append(   "\n");
        html.append(   "function loadGrid() {\n");
        html.append(   "  $.getJSON("+path+"'.json', function(data) {\n");
        html.append(   "    var boardName = data.boardName;\n");
        html.append(   "    var boardTitle = data.boardTitle;\n");
        html.append(   "    var cells = data.cells;\n");
        html.append(   "    var grid = $('.grid');\n");
        html.append(   "    grid.empty();\n");
        html.append(   "\n");
        html.append(   "    cells.forEach(function(cell) {\n");
        html.append(   "      var cellId = cell.id;\n");
        html.append(   "      var cellContent = cell.content;\n");
        html.append(   "      var cellUrl = cell.url;\n");
        html.append(   "      var gridItem = $('<div>', {id: 'grid-item-' + cellId, class: 'grid-item'}).text(cellContent);\n");
        html.append(   "      grid.append(gridItem);\n");
        html.append(   "    });\n");
        html.append(   "  });\n");
        html.append(   "}\n");
        html.append(   "\n");
        html.append(   "function updateCellContent(cellId) {\n");
        html.append(   "  var text = prompt('Enter the new text for cell ' + cellId);\n");
        html.append(   "  if (text !== null) {\n");
        html.append(   "    $.ajax({\n");
        html.append(   "      url: '/update-cell',\n");
        html.append(   "      type: 'POST',\n");
        html.append(   "      data: JSON.stringify({\n");
        html.append(   "        cellId: cellId,\n");
        html.append(   "        text: text\n");
        html.append(   "      }),\n");
        html.append(   "      contentType: 'application/json',\n");
        html.append(   "      success: function(response) {\n");
        html.append(   "        console.log(response);\n");
        html.append(   "        loadGrid();\n");
        html.append(   "      },\n");
        html.append(   "      error: function(xhr, status, error) {\n");
        html.append(   "        console.error(xhr.responseText);\n");
        html.append(   "      }\n");
        html.append(   "    });\n");
        html.append(   "  }\n");
        html.append(   "}\n");
        html.append(   "</script>\n");
 */

        // SCRIPTING
        html.append("<script>\n");
        html.append("function updateCellContent(cellId, text) {\n");
        html.append("  var cellElement = document.getElementById(cellId);\n");
        html.append("  cellElement.innerText = text;\n");
        html.append("}\n");
        html.append("function fetchCellContent() {\n");
        html.append("  fetch('"+path+".json')\n");
        html.append("    .then(response => response.json())\n");
        html.append("    .then(data => {\n");
        html.append("      data.cells.forEach(cell => {\n");
        html.append("        var cellId = 'grid-item-' + cell.id;\n");
        html.append("        var text = cellContents[cellId];\n");
        html.append("        if (text) {\n");
        html.append("          updateCellContent(cellId, text);\n");
        html.append("        }\n");
        html.append("      });\n");
        html.append("    });\n");
        html.append("}\n");
        html.append("fetchCellContent();\n");
        html.append("</script>\n");

        html.append("</head>\n");
        html.append("<body>\n");
        html.append("<h1 align=\"center\">"+title+"</h1>");
        html.append("<div class=\"grid\">\n");

        int id = 1;
        int rowId = 1;
        int colId = 1;
        for (int row = 0; row <= rows; row++) {
            for (int col = 0; col <= cols; col++) {
                if( (col == 0 && row == 0) ){
                    html.append("<div></div>");
                }
                else if( (row == 0 && col !=0) ){
                    html.append("<div id=\"grid-col-title" + rowId + "\" class=\"grid-title\">row title " + rowId + "</div>\n");
                    rowId++;
                }
                else if( (col == 0 && row != 0)){
                    html.append("<div id=\"grid-row-title" + colId + "\" class=\"grid-title\"> col title " + colId + "</div>\n");
                    colId++;
                }else{
                    html.append("<div id=\"grid-item-" + id + "\" class=\"grid-item\">cell id: " + id + "</div>\n");
                    id++;
                }

            }
        }

        html.append("</div>\n");
        html.append("</body>\n");
        html.append("</html>");
        path = path+".html";
        // Write the HTML to a file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
            writer.write(html.toString());
            System.out.println("\nHTML grid generated successfully.\n");
        } catch (IOException e) {
            System.out.println("Error generating HTML grid: " + e.getMessage());
        }
        generateJSON(name, title, rows, cols);
        openFile(path);

        System.out.println("Add content:");
        Scanner sc = new Scanner(System.in);
        System.out.println("ID of cell: ");
        int idx = sc.nextInt();
        sc.nextLine();
        System.out.println("Content to put: ");
        String content = sc.nextLine();
        System.out.println("Image URL to put: ");
        String url = sc.nextLine();
        //editHTMLGrid(path, String.valueOf(idx), content, url);
    }
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
    /*

    public static void editHTMLGrid(String path, String cellId, String cellText, String imageUrl) {
        File htmlFile = new File(path);

        try {
            // Read the HTML file content
            String htmlContent = Files.readString(htmlFile.toPath(), StandardCharsets.UTF_8);

            // Replace the text and image URL of the specified cell using JavaScript function
            String replacement = "<script>updateCellContent(\"" + cellId + "\", \"" + cellText + "\", \"" + imageUrl + "\");</script>";
            htmlContent = htmlContent.replaceAll("</body>", replacement + "</body>");

            // Write the updated HTML to the file
            Path filePath = htmlFile.toPath();
            Files.writeString(filePath, htmlContent, StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING);

        } catch (IOException e) {
            System.out.println("Error editing HTML grid: " + e.getMessage());
        }
    }

    public static void updateCellContent(String cellId, String text) {
        .put(cellId, text);

        // Update the JSON file with the new cell content
        try {
            String json = Files.readString(Path.of("board.json"), StandardCharsets.UTF_8);
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            BoardInfo boardInfo = gson.fromJson(json, BoardInfo.class);

            for (CellInfo cell : boardInfo.getCells()) {
                if (("grid-item-" + cell.getId()).equals(cellId)) {
                    cell.setContent(text);
                    break;
                }
            }

            String updatedJson = gson.toJson(boardInfo);
            Files.writeString(Path.of("board.json"), updatedJson, StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING);

            System.out.println("Cell content updated successfully.");
        } catch (IOException e) {
            System.out.println("Error updating cell content: " + e.getMessage());
        }
    }

     */
}


