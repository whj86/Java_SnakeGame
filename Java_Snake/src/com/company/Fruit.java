package com.company;

import javax.swing.*;
import java.awt.*;

public class Fruit {
    private int x;
    private int y;
    private ImageIcon img;

    public Fruit() {
        img = new ImageIcon(getClass().getResource("star.png"));
        // 20個column，Math.random()提供>= 0 ~ < 1 的double，
        // 因此Math.floor(Math.random() * Main.column) 的可能值為0, 1, 2, 3, 4, ..., 19
        // 再乘上CELL_SIZE可算出要經過多少格的寬度，
        // 最左邊格子(第1格)的x，前面要經過0個CELL_SIZE的寬度，
        // 而最右邊格子(第20格)的x，前面要經過19個CELL_SIZE的寬度。
        this.x = (int) (Math.floor(Math.random() * Main.column) * Main.CELL_SIZE);
        this.y = (int) (Math.floor(Math.random() * Main.row) * Main.CELL_SIZE);
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public void drawFruit(Graphics g) {
        img.paintIcon(null, g, this.x, this.y);
    }

    public void setNewLocation(Snake s) {
        int new_x;
        int new_y;
        do {
            new_x = (int) (Math.floor(Math.random() * Main.column) * Main.CELL_SIZE);
            new_y = (int) (Math.floor(Math.random() * Main.row) * Main.CELL_SIZE);
        } while(checkOverlapping(new_x, new_y, s));
        this.x = new_x;
        this.y = new_y;
    }

    public boolean checkOverlapping(int x, int y, Snake s) {
        for (int i = 0 ; i < s.getSnakeBody().size(); i++) {
            if ((x == s.getSnakeBody().get(i).x) && (y == s.getSnakeBody().get(i).y)) {
                return true;
            }
        }
        return false;
    }
}
