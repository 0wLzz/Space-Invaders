package com.main.App;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import com.main.Database.Dbh;
import com.main.GUI.Menu;

public class SpaceInvaders extends JPanel implements ActionListener, KeyListener{
    class Block {
        int x;
        int y;
        int width;
        int height;
        int hitCount;
        Image img;
        boolean alive = true; // buat alien
        boolean used = false; // buat peluru
        boolean exploding = false; // Untuk menandai apakah alien sedang meledak
        int explosionTimer = 0; // Durasi ledakan dalam frame

        Block(int x, int y, int width, int height, Image img) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.img = img;
            this.hitCount = 0;
        } 
    }

    int tileSize = 32;
    int row = 16;
    int column = 16;
    int mapWidth = tileSize * column;
    int mapHeight = tileSize * row;

    Image shipImg;
    Image alien1;
    Image alien2;
    Image alien3;
    Image alien4;
    ArrayList<Image> alienImgArray;

    // buat ship
    int shipWidth = tileSize * 2;
    int shipHeight = tileSize * 2;
    int shipX = tileSize * column/2 - tileSize;
    int shipY = mapHeight - tileSize * 3;
    int shipVelocityX = tileSize;
    int shipVelocityY = tileSize;

    // buat alien
    ArrayList<Block> alienArray;
    int alienWidth = tileSize;
    int alienHeight = tileSize;
    int alienX = tileSize;
    int alienY = tileSize;

    int alienRow = 2;
    int alienCol = 3;
    int alienCount = 0; // 
    int alienVelocityX = 1;

    //buat peluru
    private final Image bulletImg;
    ArrayList<Block> bulletArray;
    private int bulletsNeededPerAlien;
    int bulletWidth = tileSize / 2;
    int bulletHeight = tileSize;
    int bulletVelocityY = -15;

    //explosion
    Image explodeImg;
    int explodeWidth = tileSize;
    int explodeHeight = tileSize;
    int explodeX = tileSize;
    int explodeY = tileSize;

    Block ship;
    Timer gameLoop;
    int score = 0;
    int currentLevel = 1;
    boolean gameOver = false;
    boolean exploding = false;
    
    private final App app; // Reference to App to play sounds
    private boolean musicPlaying = false;

    public SpaceInvaders() {
        setPreferredSize(new Dimension(mapWidth, mapHeight));
        setBackground(Color.black);
        setFocusable(true);
        addKeyListener(this);
        
        app = new App();

        shipImg = new ImageIcon(getClass().getResource("/character/ship.png")).getImage();
        alien1 = new ImageIcon(getClass().getResource("/character/alien1.png")).getImage();
        alien2 = new ImageIcon(getClass().getResource("/character/alien2.png")).getImage();
        alien3 = new ImageIcon(getClass().getResource("/character/alien3.png")).getImage();
        alien4  = new ImageIcon(getClass().getResource("/character/alien4.png")).getImage();
        bulletImg = new ImageIcon(getClass().getResource("/character/bullet.png")).getImage();
        explodeImg = new ImageIcon(getClass().getResource("/character/explosion.png")).getImage();

        alienImgArray = new ArrayList<>();
        alienImgArray.add(alien1);
        alienImgArray.add(alien2);
        alienImgArray.add(alien3);
        alienImgArray.add(alien4);

        ship = new Block(shipX, shipY, shipWidth, shipHeight, shipImg);
        alienArray = new ArrayList<>();
        bulletArray = new ArrayList<>();
        
        app.playMusic(0); // Play main music (index 0)
        musicPlaying = true;

        gameLoop = new Timer(1000/60, this);
        createAliens();
        gameLoop.start();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        g.drawImage(ship.img, ship.x, ship.y, ship.width, ship.height, null);
       
        for (int i = 0; i < alienArray.size(); i++) {
            Block alien = alienArray.get(i);
            if (alien.exploding) {
                // Gambar ledakan
                g.drawImage(explodeImg, alien.x, alien.y, alien.width, alien.height, null);
            } else if (alien.alive) {
                // Gambar alien jika masih hidup
                g.drawImage(alien.img, alien.x, alien.y, alien.width, alien.height, null);
            }
        }
        

        g.setColor(Color.white);
        for (int i = 0; i < bulletArray.size(); i++) {
            Block bullet = bulletArray.get(i);
            if (!bullet.used) {
                // Gambar peluru menggunakan ikon baru
                g.drawImage(bulletImg, bullet.x, bullet.y, bullet.width, bullet.height, null);
            }
        }

        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.PLAIN, 32));
        if(gameOver) {
            ImageIcon imageIcon = new ImageIcon(getClass().getResource("/obj/saveScore.png"));
            g.drawImage(imageIcon.getImage(), mapWidth / 4, mapHeight / 4, mapWidth /2, mapHeight / 6, null);

            ImageIcon saveIcon = new ImageIcon(getClass().getResource("/obj/gameOver.png"));
            g.drawImage(saveIcon.getImage(), mapWidth / 4, mapHeight / 2, mapWidth / 2, mapHeight /10, null);


            ImageIcon yesIcon = new ImageIcon(getClass().getResource("/obj/yesBtn.png"));
            ImageIcon noIcon = new ImageIcon(getClass().getResource("/obj/noBtn.png"));

            // Add Yes and No buttons
            JButton yesButton = new JButton(yesIcon);
            JButton noButton = new JButton(noIcon);

            customizeButton(yesButton);
            customizeButton(noButton);
            
            // Set button bounds (adjust as needed)
            yesButton.setBounds(150, 320, 100, 30);
            noButton.setBounds(260, 320, 100, 30);
            
            // Add buttons to the panel
            this.setLayout(null);
            this.add(yesButton);
            this.add(noButton);

            // Handle "Yes" button click
            yesButton.addActionListener(e -> {
                String playerName = JOptionPane.showInputDialog(null, 
                                    "Enter your name:", 
                                    "Save Score", 
                                    JOptionPane.PLAIN_MESSAGE);

                if (playerName != null && !playerName.trim().isEmpty()) {
                    saveScoreToDatabase(playerName, score);
                } 
                
                else {
                    JOptionPane.showMessageDialog(null, 
                        "Name cannot be empty!", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                }

                goToMainMenu();
            });

            // Handle "No" button click
            noButton.addActionListener(e -> {
                goToMainMenu(); 
            });
            
        }else {
            g.drawString(String.valueOf(score), 10, 35);
        }
    }

    private void customizeButton(JButton button) {
        button.setContentAreaFilled(false); // Transparent background
        button.setBorderPainted(false); // No border
        button.setFocusPainted(false); // No focus highlight
    }

    // Method to save score to database
    private void saveScoreToDatabase(String player, int score) {
        String query = "INSERT INTO scoreboard (player_name, score, game_date) VALUES (?, ?, ?)";
        try (Connection connection = Dbh.getConnection();
            PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, player);
            statement.setInt(2, score);

            LocalDate date = LocalDate.now();
            statement.setDate(3, Date.valueOf(date));
            
            statement.executeUpdate();
            JOptionPane.showMessageDialog(this, "Score saved successfully!");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error saving score: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        
        goToMainMenu();
    }

    // Method to go back to the main menu
    private void goToMainMenu() {
        SwingUtilities.getWindowAncestor(this).dispose(); // Close the current game window
        Menu menu = new Menu();
        menu.showMenu();
    }

    public void move() {
        for(int i = 0; i < alienArray.size(); i++) {
            Block alien = alienArray.get(i);
            if (alien.exploding) {
                alien.explosionTimer--;
                if (alien.explosionTimer <= 0) {
                    // Hapus alien setelah ledakan selesai
                    alienArray.remove(i);
                    i--; // Pastikan indeks tidak melewatkan elemen berikutnya
                }
            } else if(alien.alive) {
                alien.x += alienVelocityX;

                //kalo udah nyentuh dinding
                if(alien.x + alien.width >= mapWidth || alien.x <= 0) {
                    alienVelocityX *= -1;
                    alien.x += alienVelocityX * 2;

                    //pindah ke bawah 1 tile
                    for(int j = 0; j < alienArray.size(); j++) {
                        alienArray.get(j).y += alienHeight;
                    } 
                }
                if(alien.y >= ship.y) {
                    gameOver = true;
                }
            }
        }

        // peluru
        for(int i = 0; i < bulletArray.size(); i++) {
            Block bullet = bulletArray.get(i);
            bullet.y += bulletVelocityY;

            for(int j = 0; j < alienArray.size(); j++) {
                Block alien = alienArray.get(j);
                if(!bullet.used && alien.alive && detectCollision(bullet, alien)) {
                    bullet.used = true; // Tandai peluru sebagai digunakan
                    alien.hitCount++; // Tambahkan hit count untuk alien

                    if (alien.hitCount >= bulletsNeededPerAlien) {
                        alien.alive = false; // Jika sudah cukup peluru, matikan alien
                        alien.exploding = true; // Tandai alien sebagai meledak
                        alien.explosionTimer = 5; // Ledakan berlangsung selama 30 frame (setara 0.5 detik jika 60 FPS)
                        alienCount--;
                        score += 5;
            
                        // Putar efek suara saat alien dihancurkan
                        app.playSfx(4); // Menu in sound (index 2)
                    }
                }
            }
        }

        //clear peluru
        while(bulletArray.size() > 0 && (bulletArray.get(0).used || bulletArray.get(0).y < 0)) {
            bulletArray.remove(0); // hapus elemen di index 0 atau pertama di array
        }

        //next level
        if(alienCount == 0) {
            currentLevel++; // Naikkan level
            //nambah jumlah alien sebanyak 1 row dan 1 column
            alienCol = Math.min(alienCol + 1, column / 2); // max column 16/2 - 2 = 6
            alienRow = Math.min(alienRow + 1, row - 6); // max row 16-6 = 10
            bulletsNeededPerAlien = Math.min(bulletsNeededPerAlien + 1, 5);
            alienArray.clear();
            bulletArray.clear();
            alienVelocityX = 1;
            initializeLevel(); // Inisialisasi level baru
            createAliens();
        }
    }

    private void initializeLevel() {
        bulletsNeededPerAlien = Math.min(1 + (currentLevel - 1), 5); // Maksimal 5 peluru
    }

    public void createAliens() {
        Random random = new Random();
        for(int r = 0; r < alienRow; r++) {
            for(int c = 0; c < alienCol; c++) {
                int randomImgIndex = random.nextInt(alienImgArray.size());
                Block alien = new Block(
                    alienX + c*alienWidth,
                    alienY + r*alienHeight,
                    alienWidth,
                    alienHeight,
                    alienImgArray.get(randomImgIndex)
                );
                alienArray.add(alien);
            }
        }
        alienCount = alienArray.size();
    }

    public boolean detectCollision(Block a, Block b) {
        return a.x < b.x + b.width && // a's top left corner doesnt reach b's top right corner
               a.x + a.width > b.x && // a's top right corner passes b's top left corner
               a.y < b.y + b.height && // a's left corner doesnt reach b's bottom left corner
               a.y + a.height > b.y; // a's bottom left corner passes b's top left corner
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if(gameOver) {
            gameLoop.stop();
            
            // Stop music
            if (musicPlaying) {
                app.stopMusic();
                musicPlaying = false;
            }
            
            // Optional: Play a game over sound
            app.playSfx(3); // Menu out sound (index 3)
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {
        if(gameOver) {
            if (musicPlaying) {
                app.stopMusic();
                musicPlaying = false;
            }
            
            ship.x = shipX;
            alienArray.clear();
            bulletArray.clear();
            score = 0;
            alienVelocityX = 1;
            alienCol = 3;
            alienRow = 2;
            gameOver = false;
            createAliens();
            gameLoop.start();
            
            // Restart music
            app.playMusic(0);
            musicPlaying = true;

        }else if(e.getKeyCode() == KeyEvent.VK_LEFT && ship.x - shipVelocityX >= 0) {
            ship.x -= shipVelocityX; // pindah 1 tile ke kiri

        }else if(e.getKeyCode() == KeyEvent.VK_RIGHT && ship.x + ship.width + shipVelocityX <= mapWidth) {
            ship.x += shipVelocityX; // pindah 1 tile ke kanan

        }else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            // Buat peluru baru menggunakan ikon baru
            Block bullet = new Block(ship.x + shipWidth * 12 / 32, ship.y, bulletWidth, bulletHeight, bulletImg);
            bulletArray.add(bullet);
            app.playSfx(1);
        }
    }
}
