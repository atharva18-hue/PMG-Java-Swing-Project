import javax.swing.*;
import java.awt.*;

public class App {
    public static void main(String[] args) {
        int rowCount = 21;
        int colCount = 19;
        int tilesSize = 32;
        int boardWidth = colCount * tilesSize;
        int boardHeight = rowCount * tilesSize;

        JFrame frame = new JFrame("Pac Man - Enhanced Edition");
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null); // Center the window
        frame.setResizable(false); // Fixed size window
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Exit on close

        PacMan pacmanGame = new PacMan();
        frame.add(pacmanGame);
        frame.pack();
        pacmanGame.requestFocus();
        frame.setVisible(true);

    }
}
