package boardService;

public class BoardInfo {
    private String boardName;

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    private String title;
    private CellInfo[] cells;

    public String getBoardName() {
        return boardName;
    }


    public void setBoardName(String boardName) {
        this.boardName = boardName;
    }

    public CellInfo[] getCells() {
        return cells;
    }

    public void setCells(CellInfo[] cells) {
        this.cells = cells;
    }

    public void addCell(CellInfo cell) {
        if (cells == null) {
            cells = new CellInfo[1];
            cells[0] = cell;
        } else {
            CellInfo[] updatedCells = new CellInfo[cells.length + 1];
            System.arraycopy(cells, 0, updatedCells, 0, cells.length);
            updatedCells[cells.length] = cell;
            cells = updatedCells;
        }
    }
}
