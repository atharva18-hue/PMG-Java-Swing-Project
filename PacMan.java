import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.Random;
import java.util.ArrayList;

public class PacMan extends JPanel implements ActionListener, KeyListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        repaint();
        move();

        // Update power-up timer
        if (powerUpActive) {
            powerUpTimer--;
            if (powerUpTimer <= 0) {
                powerUpActive = false;
                restoreGhostImages();
            }
        }

        // Animate pacman mouth
        animationCounter++;
        if (animationCounter % 10 == 0) {
            pacmanMouthOpen = !pacmanMouthOpen;
        }

        if (gameOver) {
            gameLoop.stop();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        // Pause game
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            if (!gameOver) {
                paused = !paused;
                if (paused) {
                    gameLoop.stop();
                } else {
                    gameLoop.start();
                }
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (gameOver) {
            loadMap();
            resetPositions();
            lives = 3;
            score = 0;
            level = 1;
            gameOver = false;
            gameSpeed = 50;
            gameLoop.setDelay(gameSpeed);
            gameLoop.start();
        }

        if (paused) return;

        if (e.getKeyCode() == KeyEvent.VK_UP) {
            pacman.updateDirection('U');
        } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            pacman.updateDirection('D');
        } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            pacman.updateDirection('L');
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            pacman.updateDirection('R');
        }

        if (pacman.direction == 'U') {
            pacman.image = pacmanUpImage;
        } else if (pacman.direction == 'D') {
            pacman.image = pacmanDownImage;
        } else if (pacman.direction == 'R') {
            pacman.image = pacmanRightImage;
        } else if (pacman.direction == 'L') {
            pacman.image = pacmanLeftImage;
        }
    }

    class Block {
        int x;
        int y;
        int width;
        int height;
        Image image;
        Image originalImage;

        int startX;
        int startY;

        char direction = 'U';
        int velocityX = 0;
        int velocityY = 0;

        Block(Image image, int x, int y, int width, int height) {
            this.image = image;
            this.originalImage = image;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.startX = x;
            this.startY = y;
        }

        void updateDirection(char direction) {
            char prevDirection = this.direction;
            this.direction = direction;
            updateVelocity();
            this.x += this.velocityX;
            this.y += this.velocityY;
            for (Block wall : walls) {
                if (collision(this, wall)) {
                    this.x -= this.velocityX;
                    this.y -= this.velocityY;
                    this.direction = prevDirection;
                    updateVelocity();
                }
            }
        }

        void updateVelocity() {
            int speed = tilesSize / 4;
            if (this.direction == 'U') {
                velocityX = 0;
                velocityY = -speed;
            } else if (this.direction == 'D') {
                velocityX = 0;
                velocityY = speed;
            } else if (this.direction == 'R') {
                velocityX = speed;
                velocityY = 0;
            } else if (this.direction == 'L') {
                velocityX = -speed;
                velocityY = 0;
            }
        }

        void reset() {
            this.x = this.startX;
            this.y = this.startY;
        }
    }

    private int rowCount = 21;
    private int colCount = 19;
    private int tilesSize = 32;
    private int boardWidth = colCount * tilesSize;
    private int boardHeight = rowCount * tilesSize;

    private Image wallImage;
    private Image blueGhostImage;
    private Image orangeGhostImage;
    private Image pinkGhostImage;
    private Image redGhostImage;
    private Image scaredGhostImage;

    private Image pacmanUpImage;
    private Image pacmanDownImage;
    private Image pacmanRightImage;
    private Image pacmanLeftImage;

    private String[] tileMap = {
            "XXXXXXXXXXXXXXXXXXX",
            "X        X        X",
            "X XX XXX X XXX XX X",
            "X                 X",
            "X XX X XXXXX X XX X",
            "X    X       X    X",
            "XXXX XXXX XXXX XXXX",
            "OOOX X       X XOOO",
            "XXXX X XXrXX X XXXX",
            "O       bpo       O",
            "XXXX X XXXXX X XXXX",
            "OOOX X       X XOOO",
            "XXXX X XXXXX X XXXX",
            "X        X        X",
            "X XX XXX X XXX XX X",
            "X  X     P     X  X",
            "XX X X XXXXX X X XX",
            "X    X   X   X    X",
            "X XXXXXX X XXXXXX X",
            "X                 X",
            "XXXXXXXXXXXXXXXXXXX"
    };

    HashSet<Block> walls;
    HashSet<Block> foods;
    HashSet<Block> powerUps;
    HashSet<Block> ghosts;
    Block pacman;
    Timer gameLoop;
    char[] directions = {'U', 'D', 'L', 'R'};
    Random random = new Random();

    int score = 0;
    int lives = 3;
    int level = 1;
    int highScore = 0;
    boolean gameOver = false;
    boolean paused = false;

    // Power-up system
    boolean powerUpActive = false;
    int powerUpTimer = 0;
    int powerUpDuration = 150; // frames

    // Animation
    boolean pacmanMouthOpen = true;
    int animationCounter = 0;

    // Game speed
    int gameSpeed = 50;

    // Combo system
    int ghostCombo = 0;
    int comboTimer = 0;

    PacMan() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setBackground(Color.BLACK);
        addKeyListener(this);
        setFocusable(true);

        // Load images
        wallImage = new ImageIcon(getClass().getResource("./wall.png")).getImage();
        blueGhostImage = new ImageIcon(getClass().getResource("./blueGhost.png")).getImage();
        orangeGhostImage = new ImageIcon(getClass().getResource("./orangeGhost.png")).getImage();
        pinkGhostImage = new ImageIcon(getClass().getResource("./pinkGhost.png")).getImage();
        redGhostImage = new ImageIcon(getClass().getResource("./redGhost.png")).getImage();

        pacmanUpImage = new ImageIcon(getClass().getResource("./pacmanUp.png")).getImage();
        pacmanDownImage = new ImageIcon(getClass().getResource("./pacmanDown.png")).getImage();
        pacmanLeftImage = new ImageIcon(getClass().getResource("./pacmanLeft.png")).getImage();
        pacmanRightImage = new ImageIcon(getClass().getResource("./pacmanRight.png")).getImage();

        // Create scared ghost image (could be a blue tinted version)
        scaredGhostImage = blueGhostImage; // Placeholder

        loadMap();
        for (Block ghost : ghosts) {
            char newDirection = directions[random.nextInt(4)];
            ghost.updateDirection(newDirection);
        }

        gameLoop = new Timer(gameSpeed, this);
        gameLoop.start();
    }

    public void loadMap() {
        walls = new HashSet<Block>();
        foods = new HashSet<Block>();
        powerUps = new HashSet<Block>();
        ghosts = new HashSet<Block>();

        for (int r = 0; r < rowCount; r++) {
            for (int c = 0; c < colCount; c++) {
                String row = tileMap[r];
                char tileMapChar = row.charAt(c);

                int x = c * tilesSize;
                int y = r * tilesSize;

                if (tileMapChar == 'X') {
                    Block wall = new Block(wallImage, x, y, tilesSize, tilesSize);
                    walls.add(wall);
                } else if (tileMapChar == 'b') {
                    Block ghost = new Block(blueGhostImage, x, y, tilesSize, tilesSize);
                    ghosts.add(ghost);
                } else if (tileMapChar == 'o') {
                    Block ghost = new Block(orangeGhostImage, x, y, tilesSize, tilesSize);
                    ghosts.add(ghost);
                } else if (tileMapChar == 'r') {
                    Block ghost = new Block(redGhostImage, x, y, tilesSize, tilesSize);
                    ghosts.add(ghost);
                } else if (tileMapChar == 'p') {
                    Block ghost = new Block(pinkGhostImage, x, y, tilesSize, tilesSize);
                    ghosts.add(ghost);
                } else if (tileMapChar == 'P') {
                    pacman = new Block(pacmanRightImage, x, y, tilesSize, tilesSize);
                } else if (tileMapChar == ' ') {
                    Block food = new Block(null, x + 14, y + 14, 4, 4);
                    foods.add(food);
                }
            }
        }

        // Add power-ups at corners
        powerUps.add(new Block(null, tilesSize + 14, tilesSize + 14, 8, 8));
        powerUps.add(new Block(null, (colCount - 2) * tilesSize + 14, tilesSize + 14, 8, 8));
        powerUps.add(new Block(null, tilesSize + 14, (rowCount - 2) * tilesSize + 14, 8, 8));
        powerUps.add(new Block(null, (colCount - 2) * tilesSize + 14, (rowCount - 2) * tilesSize + 14, 8, 8));
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        // Draw pacman
        g.drawImage(pacman.image, pacman.x, pacman.y, pacman.width, pacman.height, null);

        // Draw ghosts
        for (Block ghost : ghosts) {
            g.drawImage(ghost.image, ghost.x, ghost.y, ghost.width, ghost.height, null);
        }

        // Draw walls
        for (Block wall : walls) {
            g.drawImage(wall.image, wall.x, wall.y, wall.width, wall.height, null);
        }

        // Draw food
        g.setColor(Color.WHITE);
        for (Block food : foods) {
            g.fillRect(food.x, food.y, food.width, food.height);
        }

        // Draw power-ups (larger, pulsing dots)
        g.setColor(new Color(255, 184, 255));
        for (Block powerUp : powerUps) {
            int size = powerUp.width + (animationCounter % 10 < 5 ? 2 : 0);
            g.fillOval(powerUp.x - 1, powerUp.y - 1, size, size);
        }

        // Draw HUD
        g.setFont(new Font("Arial", Font.BOLD, 18));
        g.setColor(Color.WHITE);

        if (gameOver) {
            g.setFont(new Font("Arial", Font.BOLD, 36));
            g.setColor(Color.RED);
            g.drawString("GAME OVER", boardWidth / 2 - 120, boardHeight / 2 - 20);
            g.setFont(new Font("Arial", Font.PLAIN, 20));
            g.setColor(Color.WHITE);
            g.drawString("Score: " + score, boardWidth / 2 - 50, boardHeight / 2 + 20);
            g.drawString("High Score: " + highScore, boardWidth / 2 - 80, boardHeight / 2 + 50);
            g.setFont(new Font("Arial", Font.PLAIN, 16));
            g.drawString("Press any key to restart", boardWidth / 2 - 100, boardHeight / 2 + 80);
        } else if (paused) {
            g.setFont(new Font("Arial", Font.BOLD, 36));
            g.setColor(Color.YELLOW);
            g.drawString("PAUSED", boardWidth / 2 - 80, boardHeight / 2);
            g.setFont(new Font("Arial", Font.PLAIN, 16));
            g.drawString("Press SPACE to continue", boardWidth / 2 - 110, boardHeight / 2 + 40);
        } else {
            // Lives
            for (int i = 0; i < lives; i++) {
                g.drawImage(pacmanRightImage, tilesSize / 2 + i * 35, tilesSize / 2, 30, 30, null);
            }

            // Score and Level
            g.drawString("Score: " + score, boardWidth / 2 - 50, tilesSize / 2 + 15);
            g.drawString("Level: " + level, boardWidth - 100, tilesSize / 2 + 15);

            // Power-up indicator
            if (powerUpActive) {
                g.setColor(Color.CYAN);
                g.drawString("POWER UP!", tilesSize / 2, tilesSize / 2 + 40);
                int barWidth = (powerUpTimer * 100) / powerUpDuration;
                g.fillRect(tilesSize / 2, tilesSize / 2 + 45, barWidth, 5);
            }

            // Combo indicator
            if (ghostCombo > 0 && comboTimer > 0) {
                g.setColor(Color.YELLOW);
                g.setFont(new Font("Arial", Font.BOLD, 24));
                g.drawString("x" + ghostCombo + " COMBO!", boardWidth / 2 - 70, boardHeight / 2);
            }
        }
    }

    public void move() {
        if (paused) return;

        pacman.x += pacman.velocityX;
        pacman.y += pacman.velocityY;

        // Wrap around screen edges
        if (pacman.x < 0) pacman.x = boardWidth - tilesSize;
        if (pacman.x >= boardWidth) pacman.x = 0;

        // Check wall collision
        for (Block wall : walls) {
            if (collision(pacman, wall)) {
                pacman.x -= pacman.velocityX;
                pacman.y -= pacman.velocityY;
                break;
            }
        }

        // Ghost collision and movement
        ArrayList<Block> ghostsToRemove = new ArrayList<>();
        for (Block ghost : ghosts) {
            if (collision(pacman, ghost)) {
                if (powerUpActive) {
                    // Eat ghost
                    ghostsToRemove.add(ghost);
                    ghostCombo++;
                    comboTimer = 60;
                    score += 200 * ghostCombo;
                } else {
                    // Lose life
                    lives -= 1;
                    if (lives == 0) {
                        gameOver = true;
                        if (score > highScore) {
                            highScore = score;
                        }
                        return;
                    }
                    resetPositions();
                    return;
                }
            }

            // Ghost AI
            if (ghost.y == tilesSize * 9 && ghost.direction != 'U' && ghost.direction != 'D') {
                ghost.updateDirection('U');
            }

            ghost.x += ghost.velocityX;
            ghost.y += ghost.velocityY;

            // Wrap ghosts around edges
            if (ghost.x < 0) ghost.x = boardWidth - tilesSize;
            if (ghost.x >= boardWidth) ghost.x = 0;

            for (Block wall : walls) {
                if (collision(ghost, wall)) {
                    ghost.x -= ghost.velocityX;
                    ghost.y -= ghost.velocityY;
                    char newDirection = directions[random.nextInt(4)];
                    ghost.updateDirection(newDirection);
                }
            }
        }

        // Remove eaten ghosts
        for (Block ghost : ghostsToRemove) {
            ghosts.remove(ghost);
        }

        // Respawn ghosts if all eaten
        if (ghosts.isEmpty() && powerUpActive) {
            respawnGhosts();
        }

        // Check food collision
        Block foodEaten = null;
        for (Block food : foods) {
            if (collision(food, pacman)) {
                foodEaten = food;
                score += 10;
            }
        }
        foods.remove(foodEaten);

        // Check power-up collision
        Block powerUpEaten = null;
        for (Block powerUp : powerUps) {
            if (collision(powerUp, pacman)) {
                powerUpEaten = powerUp;
                score += 50;
                activatePowerUp();
            }
        }
        powerUps.remove(powerUpEaten);

        // Update combo timer
        if (comboTimer > 0) {
            comboTimer--;
            if (comboTimer == 0) {
                ghostCombo = 0;
            }
        }

        // Level complete
        if (foods.isEmpty() && powerUps.isEmpty()) {
            level++;
            score += 1000;
            gameSpeed = Math.max(30, gameSpeed - 5); // Increase speed
            gameLoop.setDelay(gameSpeed);
            loadMap();
            resetPositions();
        }
    }

    public void activatePowerUp() {
        powerUpActive = true;
        powerUpTimer = powerUpDuration;
        ghostCombo = 0;

        // Change ghost appearance
        for (Block ghost : ghosts) {
            ghost.image = scaredGhostImage;
        }
    }

    public void restoreGhostImages() {
        for (Block ghost : ghosts) {
            ghost.image = ghost.originalImage;
        }
    }

    public void respawnGhosts() {
        // Reload ghosts from map
        for (int r = 0; r < rowCount; r++) {
            for (int c = 0; c < colCount; c++) {
                String row = tileMap[r];
                char tileMapChar = row.charAt(c);
                int x = c * tilesSize;
                int y = r * tilesSize;

                if (tileMapChar == 'b') {
                    Block ghost = new Block(blueGhostImage, x, y, tilesSize, tilesSize);
                    ghost.updateDirection(directions[random.nextInt(4)]);
                    ghosts.add(ghost);
                } else if (tileMapChar == 'o') {
                    Block ghost = new Block(orangeGhostImage, x, y, tilesSize, tilesSize);
                    ghost.updateDirection(directions[random.nextInt(4)]);
                    ghosts.add(ghost);
                } else if (tileMapChar == 'r') {
                    Block ghost = new Block(redGhostImage, x, y, tilesSize, tilesSize);
                    ghost.updateDirection(directions[random.nextInt(4)]);
                    ghosts.add(ghost);
                } else if (tileMapChar == 'p') {
                    Block ghost = new Block(pinkGhostImage, x, y, tilesSize, tilesSize);
                    ghost.updateDirection(directions[random.nextInt(4)]);
                    ghosts.add(ghost);
                }
            }
        }
    }

    public boolean collision(Block a, Block b) {
        return a.x < b.x + b.width &&
                a.x + a.width > b.x &&
                a.y < b.y + b.height &&
                a.y + a.height > b.y;
    }

    public void resetPositions() {
        pacman.reset();
        pacman.velocityX = 0;
        pacman.velocityY = 0;
        powerUpActive = false;
        ghostCombo = 0;

        for (Block ghost : ghosts) {
            ghost.reset();
            ghost.image = ghost.originalImage;
            char newDirection = directions[random.nextInt(4)];
            ghost.updateDirection(newDirection);
        }
    }
}