package boardService;

public class PostItInfo {
    private String boardName;
    private String text;

    private String url;

    private int row;

    private int col;

    public PostItInfo(String boardName, String text, String url, int row, int col){
        this.boardName = boardName;
        this.text = text;
        this.url = url;
        this.row = row;
        this.col = col;

    }


    public String getBoardName() {
        return boardName;
    }
    public void setBoardName(String boardName) {
        this.boardName = boardName;
    }

    public String getText() {return text;}
    public void setText(String text) {
        this.text = text;
    }

    public String getUrl() {return url;}
    public void setUrl(String url) {
        this.url = url;
    }

    public int getRow() {return row;}
    public void setRow(int row) {
        this.row = row;
    }

    public int getColumn() {return col;}
    public void setColumn(int col) {
        this.col = col;
    }


}
