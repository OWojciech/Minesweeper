import javax.swing.*;
import java.util.Random;

public class RealBoard {
    private int width;
    private int height;
    private int[][] board;
    private boolean[][] flagPlacement;
    private int flagsLeft;
    private int mines;
    private int fieldsToClear;
    final static int MINE_FIELD = -1;
    final static int EMPTY_FIELD = 0;
    final static int ONE_NEARBY_FIELD = 1;
    final static int TWO_NEARBY_FIELD = 2;
    final static int THREE_NEARBY_FIELD = 3;
    final static int FOUR_NEARBY_FIELD = 4;
    final static int FIVE_NEARBY_FIELD = 5;
    final static int SIX_NEARBY_FIELD = 6;
    final static int SEVEN_NEARBY_FIELD = 7;
    final static int EIGHT_NEARBY_FIELD = 8;
    final static int SAFETY_FLAG_FIELD = 9;
    final static int UNCOVERED_FIELD = 10;

    public RealBoard(int width, int height, int mines){
        setHeight(height);
        setWidth(width);
        setMines(mines);
        setFlagsLeft(mines);
        calculateFieldsToClear();
        setBoard(new int[getWidth()][getHeight()]);
        setFlagPlacement(new boolean[getWidth()][getHeight()]);
        fillGameBoard();
        showGameBoard();
    }

    private void calculateFieldsToClear(){
        fieldsToClear =  getWidth()*getHeight()-getMines();
    }

    public void progress(){
        fieldsToClear--;
        if (fieldsToClear == 0)
            JOptionPane.showMessageDialog(new JFrame(),
                    "Wygrałeś!");
    }

    private void showGameBoard() {
        System.out.println("ŚCIĄGAWKA");
        for (int i = 0; i < board.length; i++){
            for (int j = 0; j < board[i].length; j++){
                System.out.print(board[j][i]+" ");
            }
            System.out.println();
        }
    }

    public int getMines() {
        return mines;
    }

    public void setMines(int mines) {
        this.mines = mines;
    }

    private void fillGameBoard() {
        for(int i = getMines(); i > 0; i--){
            GenerateMineFieldAndProximity();
        }
    }

    private boolean yIndexInbounds(int j) {
        return (j >= 0) && (j < getHeight());
    }

    private boolean xIndexInbounds(int i) {
        return (i >= 0) && (i < getWidth());
    }

    private void GenerateMineFieldAndProximity(){
        Random rng = new Random();
        int randomX = rng.nextInt(getWidth());
        int randomY = rng.nextInt(getHeight());
        if (getBoard()[randomX][randomY] == MINE_FIELD)
            GenerateMineFieldAndProximity();
        else {
            getBoard()[randomX][randomY] = MINE_FIELD;
            for (int i = (randomX - 1); i <= (randomX + 1); i++){
                for (int j = (randomY - 1); j <= (randomY + 1); j++){
                    if (xIndexInbounds(i) && yIndexInbounds(j) && getBoard()[i][j] != MINE_FIELD)
                        getBoard()[i][j]++;
                }
            }
        }
    }

    public int discoverField(int column, int row){
        return board[column][row];
    }

    public int[][] getBoard() {
        return board;
    }

    public void setBoard(int[][] board) {
        this.board = board;
    }

    public int getWidth() {
        return width;
    }
    public int getHeight() {
        return height;
    }
    public void setHeight(int height) {
        this.height = height;
    }
    public void setWidth(int width) {
        this.width = width;
    }

    public boolean[][] getFlagPlacement() {
        return flagPlacement;
    }

    public void setFlagPlacement(boolean[][] flagPlacement) {
        this.flagPlacement = flagPlacement;
    }

    public void changeFlagState(int column, int row) {
        this.flagPlacement[column][row] = !this.flagPlacement[column][row];
        if (this.flagPlacement[column][row]) {
            flagsLeft--;
        } else {
            flagsLeft++;
        }
    }

    public int getFlagsLeft() {
        return flagsLeft;
    }

    public void setFlagsLeft(int flagsLeft) {
        this.flagsLeft = flagsLeft;
    }
}
