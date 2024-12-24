package com.main.GUI;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JPanel;
import javax.swing.Timer;

public class StarfieldPanel extends JPanel {
    private final List<Star> stars = new ArrayList<>();
    private final Random random = new Random();

    public StarfieldPanel() {
        setBackground(Color.BLACK); // Set background color
        setPreferredSize(new Dimension(800, 600)); // Set preferred size for the panel

        // Initialize stars with random positions
        for (int i = 0; i < 100; i++) {
            stars.add(new Star(random.nextInt(800), random.nextInt(600), random.nextInt(5) + 1));
        }
        
        // Timer for animation (runs every 16ms, ~60 FPS)
        Timer timer = new Timer(16, e -> updateStars());
        timer.start();
    }

    private void updateStars() {
        for (Star star : stars) {
            star.move(); // Update star position
            if (star.y > getHeight()) { // If the star moves off-screen
                star.y = 0; // Reset it to the top
                
                int width = Math.max(getWidth(), 1);
                int height = Math.max(getHeight(), 1);

                star.x = random.nextInt(width); // Randomize horizontal position
                star.y = random.nextInt(height);
                star.size = random.nextInt(3) + 1; // Randomize size
            }
        }
        repaint(); // Redraw the panel
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // Paint the background

        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.WHITE); // Set star color

        // Draw each star
        for (Star star : stars) {
            g2d.fillOval(star.x, star.y, star.size, star.size);
        }
    }

    // Inner class to represent a star
    private static class Star {
        int x, y, size;

        public Star(int x, int y, int size) {
            this.x = x;
            this.y = y;
            this.size = size;
        }

        public void move() {
            y += size; // Move the star downward based on its size (larger = faster)
        }
    }
}