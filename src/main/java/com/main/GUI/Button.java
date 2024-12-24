package com.main.GUI;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JButton;

public class Button extends JButton {
    // Constructor to customize the button directly
    public Button(String text) {
        super(text); // Call JButton constructor with the button text

        // Apply space-themed styles
        this.setFont(new Font("Arial", Font.BOLD, 20));
        this.setForeground(Color.WHITE);
        this.setBackground(new Color(30, 30, 30)); // Dark space background
        this.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(57, 255, 20), 2), // Neon green border
                BorderFactory.createEmptyBorder(10, 20, 10, 20))); // Padding
        this.setFocusPainted(false); // Remove focus highlight

        // Set uniform size for all buttons
        Dimension buttonSize = new Dimension(200, 50); // Width = 200, Height = 50
        this.setPreferredSize(buttonSize);
        this.setMaximumSize(buttonSize); // Prevent stretching beyond this size
        this.setMinimumSize(buttonSize); // Prevent shrinking below this size

        // Add spacing above the button
        this.setAlignmentX(Component.CENTER_ALIGNMENT); // Center Alignment
    }
}
