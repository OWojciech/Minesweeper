import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainMenu extends JMenuBar {
    final static int BEGINNER = 0;
    final static int INTERMEDIATE = 1;
    final static int EXPERT = 2;
    JMenu gameMenu;
    JMenuItem newGameMenuItem;
    JMenuItem beginnerLevelMenuItem;
    JMenuItem intermediateLevelMenuItem;
    JMenuItem expertLevelMenuItem;
    JMenuItem exitGameMenuItem;
    //                              beginner, intermediate, expert
    static int[][] difficulties = {{9,9,10}, {16,16,40}, {24,24,99}};
    PlayerBoard playerBoard;
    private int curretnDifficulty;
    public MainMenu(PlayerBoard playerBoard){
        super();
        this.playerBoard = playerBoard;
        gameMenu = new JMenu("Gra");
        addNewGameMenuItem();
        gameMenu.addSeparator();
        addBeginnerLevelMenuItem();
        addIntermediateLevelMenuItem();
        addExpertLevelMenuItem();
        gameMenu.addSeparator();
        addExitGameMenuItem();
        add(gameMenu);
    }

    public void switchDifficultyFor(int difficultyLevel){
        curretnDifficulty = difficultyLevel;
        switch(difficultyLevel){
            case MainMenu.BEGINNER:
                beginnerLevelMenuItem.setSelected(true);
                intermediateLevelMenuItem.setSelected(false);
                expertLevelMenuItem.setSelected(false);
                playerBoard.getContentPane().removeAll();
                playerBoard.setupProperties(MainMenu.difficulties[MainMenu.BEGINNER][0],
                        MainMenu.difficulties[MainMenu.BEGINNER][1],
                        MainMenu.difficulties[MainMenu.BEGINNER][2]);
                playerBoard.setupFrame();
                break;
            case MainMenu.INTERMEDIATE:
                beginnerLevelMenuItem.setSelected(false);
                intermediateLevelMenuItem.setSelected(true);
                expertLevelMenuItem.setSelected(false);
                playerBoard.getContentPane().removeAll();
                playerBoard.setupProperties(MainMenu.difficulties[MainMenu.INTERMEDIATE][0],
                        MainMenu.difficulties[MainMenu.INTERMEDIATE][1],
                        MainMenu.difficulties[MainMenu.INTERMEDIATE][2]);
                playerBoard.setupFrame();
                break;
            case MainMenu.EXPERT:
                beginnerLevelMenuItem.setSelected(false);
                intermediateLevelMenuItem.setSelected(false);
                expertLevelMenuItem.setSelected(true);
                playerBoard.getContentPane().removeAll();
                playerBoard.setupProperties(MainMenu.difficulties[MainMenu.EXPERT][0],
                        MainMenu.difficulties[MainMenu.EXPERT][1],
                        MainMenu.difficulties[MainMenu.EXPERT][2]);
                playerBoard.setupFrame();
                break;
        }
    }

    private void addNewGameMenuItem(){
        newGameMenuItem = new JMenuItem("Nowa gra");
        newGameMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switchDifficultyFor(curretnDifficulty);
            }
        });
        gameMenu.add(newGameMenuItem);
    }

    private void addBeginnerLevelMenuItem(){
        beginnerLevelMenuItem = new JRadioButtonMenuItem("Początkujący");
        beginnerLevelMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switchDifficultyFor(MainMenu.BEGINNER);
            }
        });
        gameMenu.add(beginnerLevelMenuItem);
    }

    private void addIntermediateLevelMenuItem(){
        intermediateLevelMenuItem = new JRadioButtonMenuItem("Zaawansowany");
        intermediateLevelMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switchDifficultyFor(MainMenu.INTERMEDIATE);
            }
        });
        gameMenu.add(intermediateLevelMenuItem);
    }

    private void addExpertLevelMenuItem(){
        expertLevelMenuItem = new JRadioButtonMenuItem("Ekspert");
        expertLevelMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switchDifficultyFor(MainMenu.EXPERT);
            }
        });
        gameMenu.add(expertLevelMenuItem);
    }

    private void addExitGameMenuItem(){
        exitGameMenuItem = new JMenuItem("Wyjdź z gry");
        exitGameMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        gameMenu.add(exitGameMenuItem);
    }
}
