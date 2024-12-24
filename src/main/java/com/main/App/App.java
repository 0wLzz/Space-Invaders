package com.main.App;

import javax.swing.JFrame;

import com.main.Audio.Sound;
import com.main.GUI.Menu;

public class App{
    Sound sound = new Sound();
    public static void main(String[] args) {
        Menu menu = new Menu();
        menu.showMenu();
    }

    public void playMusic(int i ){
        sound.setFile(i);
        sound.play();
        sound.loop();
    }

    public void stopMusic(){
        sound.stop();
    }

    public void playSfx(int i){
        sound.setFile(i);
        sound.play();
    }

    public JFrame makeFrame() {
        int tileSize = 32;
        int row = 16;
        int column = 16;
        int mapWidth = tileSize * column;
        int mapHeight = tileSize * row;
    
        JFrame frame = new JFrame("Space Invaders");
    
        frame.setVisible(true); 
        frame.setSize(mapWidth, mapHeight); 
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 

        return frame;
    }
}
