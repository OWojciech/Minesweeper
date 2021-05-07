//Source: https://www.geeksforgeeks.org/cpp-implementation-minesweeper-game/
//Layout: https://minesweeperonline.com/
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.MouseInputAdapter;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class PlayerBoard extends JFrame{
    private int gridWidth;
    private int gridHeight;
    private int[][] board;
    private RealBoard realBoard;
    private int mines;
    private final int buttonWidth = 27;
    private final int buttonHeight = 27;
    private JPanel gamePanel;
    private JLabel flagsLeftLabel;
    private Icon unrevealedIcon;
    private boolean firstClick;

    public PlayerBoard(int width, int height, int mines, int difficultyLevel){
        setupProperties(height, width, mines);
        setupMainMenu(difficultyLevel);
    }

    protected void setupMainMenu(int difficultyLevel){
        MainMenu mainMenu = new MainMenu(this);
        setJMenuBar(mainMenu);
        mainMenu.switchDifficultyFor(difficultyLevel);
    }

    protected void setupProperties(int height, int width, int mines){
        setGridHeight(height);
        setGridWidth(width);
        setMines(mines);
        setBoard(new int[getGridWidth()][getGridHeight()]);
        realBoard = new RealBoard(getGridWidth(), getGridHeight(), getMines());
        firstClick = false;
    }

    protected void setupFrame(){
        setLayout(new BoxLayout(getContentPane(), BoxLayout.PAGE_AXIS));
        setupInformationPanel();
        setupGamePanel();
        setTitle("Saper");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void setupInformationPanel() {
        JPanel informationPanel = new JPanel();
        informationPanel.setSize(getGridWidth()*buttonWidth, buttonHeight);
        informationPanel.setBackground(new Color(180,180,180));
        informationPanel.add(new JLabel("Pozostało flag:"));
        flagsLeftLabel = new JLabel();
        updateFlagsLeftLabel();
        informationPanel.add(flagsLeftLabel);
        add(informationPanel, BorderLayout.NORTH);
    }

    private void updateFlagsLeftLabel() {
        flagsLeftLabel.setText(Integer.toString(realBoard.getFlagsLeft()));
    }

    private void setupGamePanel() {
        gamePanel = new JPanel();
        gamePanel.setSize(getGridWidth()*buttonWidth, getGridHeight()*buttonHeight);
        gamePanel.setLayout(new GridLayout(getGridWidth(), getGridHeight()));
        try{
            fillLayoutWithButtons();
        }catch (IOException ioe){
            ioe.printStackTrace();
        }
        add(gamePanel, BorderLayout.CENTER);
    }

    public int getMines() {
        return mines;
    }

    public void setMines(int mines) {
        this.mines = mines;
    }

    private void fillLayoutWithButtons() throws IOException{
        unrevealedIcon = new ImageIcon(ImageIO.read(new File(Objects.requireNonNull(getImagePath(RealBoard.UNCOVERED_FIELD)))).getScaledInstance(buttonWidth, buttonHeight, Image.SCALE_SMOOTH));
        for (int i = 0; i < getGridWidth() * getGridHeight(); i++)
            createAndAddNewButton();
    }

    private void createAndAddNewButton(){
        GameField gameField = new GameField();
        gameField.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
        gameField.setIcon(unrevealedIcon);
        gameField.addMouseListener(new MouseInputAdapter() {
            boolean leftButtonPressed;
            boolean rightButtonPressed;

            @Override
            public void mousePressed(MouseEvent e){
                if (SwingUtilities.isLeftMouseButton(e))
                    leftButtonPressed = true;
                if (SwingUtilities.isRightMouseButton(e))
                    rightButtonPressed = true;
            }

            @Override
            public void mouseReleased(MouseEvent e){
                super.mouseReleased(e);
                new Thread(() -> {
                    if (!leftButtonPressed && !rightButtonPressed) return;
                    GameField source = (GameField) (e.getSource());
                    int x = source.getLocation().x / source.getBounds().width;
                    int y = source.getLocation().y / source.getBounds().height;
                    if (!firstClick){
                        realBoard.fillGameBoard(x, y);
                        firstClick = true;
                    }
                    try {
                        if (SwingUtilities.isLeftMouseButton(e) && !SwingUtilities.isRightMouseButton(e))
                            leftMouseButtonPressed(source, x, y);
                        if (SwingUtilities.isLeftMouseButton(e) && SwingUtilities.isRightMouseButton(e))
                            bothMouseButtonsPressed(x, y);
                        if (!SwingUtilities.isLeftMouseButton(e) && SwingUtilities.isRightMouseButton(e))
                            rightMouseButtonPressed(source, x, y);
                    }catch (IOException ioe){
                        ioe.printStackTrace();
                    }
                    leftButtonPressed =false;
                    rightButtonPressed =false;
                }).start();

            }
        });
        gamePanel.add(gameField);
    }

    private void leftMouseButtonPressed(GameField source, int x, int y) throws IOException{
        if (source.isFlagPlaced() || source.isRevealed()) return;
        if (realBoard.discoverField(x, y) == RealBoard.MINE_FIELD) {
            revealAllMines();
            return;
        }
        revealEmptyFields(x, y);
    }

    private void bothMouseButtonsPressed(int x, int y) throws IOException{
        revealSafeMineProximity(x, y);
    }

    private void rightMouseButtonPressed(GameField source, int x, int y) throws IOException{
        if (source.isRevealed()) return;
        source.setFlagPlaced(source.isFlagPlaced());
        realBoard.changeFlagState(x, y);
        updateFlagsLeftLabel();
    }

    private void revealSafeMineProximity(int x, int y) throws IOException{
        if (realBoard.getBoard()[x][y] <= RealBoard.EMPTY_FIELD && realBoard.getBoard()[x][y] > RealBoard.EIGHT_NEARBY_FIELD) return;

        int flagCounter = 0;
        for (int i = (x - 1); i <= (x + 1); i++){
            for (int j = (y - 1); j <= (y + 1); j++){
                if (xIndexInbounds(i) && yIndexInbounds(j))
                    if (realBoard.getFlagPlacement()[i][j])
                        flagCounter++;
            }
        }

        if (flagCounter == realBoard.getBoard()[x][y]){
            for (int i = (x - 1); i <= (x + 1); i++){
                for (int j = (y - 1); j <= (y + 1); j++){
                    if (xIndexInbounds(i) && yIndexInbounds(j) && !getButtonFromGrid(i, j).isFlagPlaced()) {
                        if (realBoard.discoverField(i, j) != RealBoard.MINE_FIELD) {
                            revealEmptyFields(i, j);
                            continue;
                        }
                        revealAllMines();
                    }
                }
            }
        }
    }

    protected void revealAllMines() throws IOException{
        ImageIcon mineFieldIcon = new ImageIcon(ImageIO.read(new File(Objects.requireNonNull(getImagePath(RealBoard.MINE_FIELD)))).getScaledInstance(buttonWidth, buttonHeight, Image.SCALE_SMOOTH));
        for (int i = 0; i < board.length; i++){
            for (int j = 0; j < board[i].length; j++){
                if (realBoard.getBoard()[j][i] != RealBoard.MINE_FIELD) continue;
                getButtonFromGrid(j, i).setIcon(mineFieldIcon);
            }
        }
    }

    protected void revealEmptyFields(int x, int y) throws IOException{
        if (xIndexInbounds(x) && yIndexInbounds(y) && !getButtonFromGrid(x, y).isRevealed()) {
            revealField(x, y);
            if (realBoard.getBoard()[x][y] == RealBoard.EMPTY_FIELD) {
                revealEmptyFields((x + 1), y);
                revealEmptyFields((x + 1), (y - 1));
                revealEmptyFields((x + 1), (y + 1));
                revealEmptyFields((x - 1), (y - 1));
                revealEmptyFields((x - 1), (y + 1));
                revealEmptyFields((x - 1), y);
                revealEmptyFields(x, (y + 1));
                revealEmptyFields(x, (y - 1));
            }
        }
    }

    private void revealField(int x, int y) throws IOException{
        GameField gameField = getButtonFromGrid(x, y);
        if (gameField == null ) return;
        realBoard.progress();
        gameField.setIcon(getCorrespondingIcon(x, y));
        gameField.setRevealed(true);
    }

    private Icon getCorrespondingIcon(int x, int y) throws IOException{
        return new ImageIcon(ImageIO.read(new File(Objects.requireNonNull(getImagePath(realBoard.discoverField(x, y))))).getScaledInstance(buttonWidth, buttonHeight, Image.SCALE_SMOOTH));
    }

    public static String getImagePath(int whichField){
        switch(whichField){
            case RealBoard.MINE_FIELD:
                return "src\\Images\\Mine Field.png";
            case RealBoard.EMPTY_FIELD:
                return "src\\Images\\Empty Field.png";
            case RealBoard.ONE_NEARBY_FIELD:
                return "src\\Images\\One Nearby Field.png";
            case RealBoard.TWO_NEARBY_FIELD:
                return "src\\Images\\Two Nearby Field.png";
            case RealBoard.THREE_NEARBY_FIELD:
                return "src\\Images\\Three Nearby Field.png";
            case RealBoard.FOUR_NEARBY_FIELD:
                return "src\\Images\\Four Nearby Field.png";
            case RealBoard.FIVE_NEARBY_FIELD:
                return "src\\Images\\Five Nearby Field.png";
            case RealBoard.SIX_NEARBY_FIELD:
                return "src\\Images\\Six Nearby Field.png";
            case RealBoard.SEVEN_NEARBY_FIELD:
                return "src\\Images\\Seven Nearby Field.png";
            case RealBoard.EIGHT_NEARBY_FIELD:
                return "src\\Images\\Eight Nearby Field.png";
            case RealBoard.SAFETY_FLAG_FIELD:
                return "src\\Images\\Safety Flag Field.png";
            case RealBoard.UNCOVERED_FIELD:
                return "src\\Images\\Uncovered Field.png";
            default:
                return null;
        }
    }

    public GameField getButtonFromGrid(int x, int y) {
        return (xIndexInbounds(x) && yIndexInbounds(y)) ? (GameField) gamePanel.getComponent(y * getGridHeight() + x) : null;
    }

    public void setBoard(int[][] board) {
        this.board = board;
    }

    public int getGridWidth() {
        return gridWidth;
    }
    public int getGridHeight() {
        return gridHeight;
    }
    public void setGridHeight(int girdHeight) {
        this.gridHeight = girdHeight;
    }
    public void setGridWidth(int gridWidth) {
        this.gridWidth = gridWidth;
    }    
    
    private boolean yIndexInbounds(int j) {
        return ((j >= 0) && (j < getGridHeight()));
    }

    private boolean xIndexInbounds(int i) {
        return ((i >= 0) && (i < getGridWidth()));
    }


    public static void main(String[] args){
        new PlayerBoard(MainMenu.difficulties[MainMenu.EXPERT][0],
                MainMenu.difficulties[MainMenu.EXPERT][1],
                MainMenu.difficulties[MainMenu.EXPERT][2],
                MainMenu.EXPERT);
    }
}