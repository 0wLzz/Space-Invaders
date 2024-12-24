package com.main.GUI;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

import com.main.App.App;
import com.main.App.SpaceInvaders;
import com.main.Database.Dbh;


public class Menu extends App{
    public static JFrame frame;
    public Menu() {
        frame = super.makeFrame();

        // Create a StarfieldPanel
        StarfieldPanel starfieldPanel = new StarfieldPanel();
        starfieldPanel.setLayout(new BoxLayout(starfieldPanel, BoxLayout.Y_AXIS));

        // Title Image
        JLabel menuGame = new JLabel(new ImageIcon(getClass().getResource("/obj/Headline.png")), SwingConstants.CENTER);
        menuGame.setAlignmentX(Component.CENTER_ALIGNMENT);
        menuGame.setBorder(BorderFactory.createEmptyBorder(50, 0, 20, 0)); // Spacing around the image

        // Buttons Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setOpaque(false); 
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Load image icon for the buttons
        ImageIcon startIcon = new ImageIcon(getClass().getResource("/obj/startBtn.png"));
        ImageIcon scoreboardIcon = new ImageIcon(getClass().getResource("/obj/rankBtn.png"));
        ImageIcon instructionsIcon = new ImageIcon(getClass().getResource("/obj/helpBtn.png"));
        ImageIcon exitIcon = new ImageIcon(getClass().getResource("/obj/exitBtn.png"));

        // Create buttons with image icons
        JButton startButton = new JButton(startIcon);
        JButton scoreboardButton = new JButton(scoreboardIcon);
        JButton instructionsButton = new JButton(instructionsIcon);
        JButton exitButton = new JButton(exitIcon);

        // Customize the buttons (make them transparent)
        customizeButton(startButton);
        customizeButton(scoreboardButton);
        customizeButton(instructionsButton);
        customizeButton(exitButton);

        // Set button size to match the image size
        startButton.setPreferredSize(new Dimension(startIcon.getIconWidth(), startIcon.getIconHeight()));
        scoreboardButton.setPreferredSize(new Dimension(scoreboardIcon.getIconWidth(), scoreboardIcon.getIconHeight()));
        instructionsButton.setPreferredSize(new Dimension(instructionsIcon.getIconWidth(), instructionsIcon.getIconHeight()));
        exitButton.setPreferredSize(new Dimension(exitIcon.getIconWidth(), exitIcon.getIconHeight()));

        // Add buttons with spacing
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        buttonPanel.add(startButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        buttonPanel.add(scoreboardButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        buttonPanel.add(instructionsButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        buttonPanel.add(exitButton);

        // Add MouseListeners for button actions
        startButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                startGame();
            }
        });

        scoreboardButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                showScoreBoard();
            }
        });

        instructionsButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                showInstructions();
            }
        });

        exitButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                System.exit(0);
            }
        });

        // Add the components to the starfield panel
        starfieldPanel.add(menuGame);
        starfieldPanel.add(buttonPanel);

        // Add starfieldPanel to frame
        frame.add(starfieldPanel);

        // Make the frame visible
        frame.setVisible(true);
    }

    private void customizeButton(JButton button) {
        button.setContentAreaFilled(false); // Transparent background
        button.setBorderPainted(false); // No border
        button.setFocusPainted(false); // No focus highlight
    }

    

    public void showMenu() {
        frame.setVisible(true);
    }

    private void startGame() {
        frame.setVisible(false);
    
        JFrame gameFrame = super.makeFrame();
        SpaceInvaders sp = new SpaceInvaders(); // Game panel

        gameFrame.add(sp); // Add the game panel
        sp.requestFocus(); // Ensure the game panel gets focus
        gameFrame.setVisible(true); // Show the game frame
    }

    private void showInstructions() {
        JOptionPane.showMessageDialog(frame,
                "Instructions:\n- Use arrow keys to move.\n- Press space to shoot.\n- Destroy all aliens!",
                "Instructions",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void showScoreBoard() {
        // Hide the main menu frame instead of disposing it
        frame.setVisible(false);
    
        // Create a new frame for the scoreboard
        JFrame scoreboardFrame = super.makeFrame();
    
        // Create a StarfieldPanel for the scoreboard
        StarfieldPanel scoreboardPanel = new StarfieldPanel();
        scoreboardPanel.setLayout(new BoxLayout(scoreboardPanel, BoxLayout.Y_AXIS));
    
        // Title Label
        JLabel scoreboardTitle = new JLabel("Scoreboard", SwingConstants.CENTER);
        scoreboardTitle.setFont(new Font("Arial", Font.BOLD, 40));
        scoreboardTitle.setForeground(new Color(57, 255, 20)); // Neon green
        scoreboardTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        scoreboardTitle.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(50, 0, 20, 0), // Spacing
                BorderFactory.createLineBorder(new Color(57, 255, 20), 2))); // Glowing effect
    
        // Create a custom JTable with space-themed design
        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel.addColumn("Rank");
        tableModel.addColumn("Player");
        tableModel.addColumn("Score");
    
        // Populate the table model with data from the database
        try (Connection connection = Dbh.getConnection();
             Statement statement = connection.createStatement()) {
            String query = "SELECT player_name, score FROM scoreboard ORDER BY score DESC LIMIT 10";
            ResultSet resultSet = statement.executeQuery(query);
    
            int rank = 1;
            while (resultSet.next()) {
                String playerName = resultSet.getString("player_name");
                int score = resultSet.getInt("score");
                tableModel.addRow(new Object[]{rank++, playerName, score});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(scoreboardFrame,
                    "Error retrieving scoreboard data: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    
        JTable table = new JTable(tableModel) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make cells non-editable
            }
        };

        table.setFont(new Font("Arial", Font.PLAIN, 18));
        table.setForeground(new Color(57, 255, 20)); // Neon green text
        table.setBackground(Color.BLACK); // Black background
        table.setGridColor(new Color(57, 255, 20)); // Neon green grid
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 20));
        table.getTableHeader().setForeground(new Color(57, 255, 20));
        table.getTableHeader().setBackground(Color.BLACK);
    
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setAlignmentX(Component.CENTER_ALIGNMENT);
    
        // Back Button
        Button backButton = new Button("Back to Menu");
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.addActionListener(e -> {
            scoreboardFrame.dispose(); // Close the scoreboard frame
            frame.setVisible(true); // Show the main menu
        });
    
        // Add components to the scoreboard panel
        scoreboardPanel.add(scoreboardTitle);
        scoreboardPanel.add(Box.createRigidArea(new Dimension(0, 20))); // Spacing
        scoreboardPanel.add(scrollPane);
        scoreboardPanel.add(Box.createRigidArea(new Dimension(0, 20))); // Spacing
        scoreboardPanel.add(backButton);
    
        // Add the panel to the frame
        scoreboardFrame.add(scoreboardPanel);
        scoreboardFrame.setVisible(true);
    }
    
    
}