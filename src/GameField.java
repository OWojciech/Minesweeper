import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class GameField extends JButton {
    private boolean flagPlaced;
    private boolean revealed;

    public GameField(){
        super();
    }

    public boolean isFlagPlaced() {
        return flagPlaced;
    }

    public void setFlagPlaced(boolean flagPlaced){
        try {
            if (!isRevealed()) {
                if (!flagPlaced)
                    setIcon(new ImageIcon(ImageIO.read(new File(PlayerBoard.getImagePath(RealBoard.SAFETY_FLAG_FIELD))).getScaledInstance(getWidth(), getHeight(), Image.SCALE_SMOOTH)));
                else
                    setIcon(new ImageIcon(ImageIO.read(new File(PlayerBoard.getImagePath(RealBoard.UNCOVERED_FIELD))).getScaledInstance(getWidth(), getHeight(), Image.SCALE_SMOOTH)));
                this.flagPlaced = !flagPlaced;
            }
        }catch(IOException ioe){
            ioe.printStackTrace();
        }

    }

    public boolean isRevealed() {
        return revealed;
    }

    public void setRevealed(boolean revealed) {
        this.revealed = revealed;
    }
}
