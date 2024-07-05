package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

public class Main extends JPanel implements KeyListener {
    public static final int CELL_SIZE = 20;
    public static int width = 400;
    public static int height = 400;
    public static int row = height / CELL_SIZE;
    public static int column = width / CELL_SIZE;
    private Snake snake;
    private Fruit fruit;
    private Timer t;
    private int speed = 80;
    private String direction;
    private boolean allowKeyPress; // 讀到方向鍵，設定direction後，改成False，直到repaint()完才改成True。
    private int score;
    private int highest_score;
    String desktop = System.getProperty("user.home") + "/Desktop/";
    String myfile = desktop + "filename.txt";

    public Main() {
        read_highest_score();
        reset();
        addKeyListener(this);
    }

    private void setTimer() {
        t = new Timer();
        t.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                repaint();
            }
        }, 0, speed);
    }

    private void reset() {
        score = 0;
        if (snake != null) {
            snake.getSnakeBody().clear();
        }
        allowKeyPress = true;
        direction = "right";
        snake = new Snake();
        fruit = new Fruit();
        setTimer();
    }

    @Override
    public void paintComponent(Graphics g) {
        // 判斷是否咬到自己(end game)
        for (int i = 1; i < snake.getSnakeBody().size(); i++) {
            if ((snake.getSnakeBody().get(i).x == snake.getSnakeBody().get(0).x) && (snake.getSnakeBody().get(i).y == snake.getSnakeBody().get(0).y)) {
                allowKeyPress = false;
                t.cancel(); // 我:清除timer已排程的task
                t.purge(); // 我:清除timer的task queue
                int response = JOptionPane.showOptionDialog(this, "Game Over! Your score is " + score + ".\nThe highest score was " + highest_score + ".\nWould you like to start over?", "Game Over", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, JOptionPane.YES_NO_OPTION);
                write_a_file(score);
                switch (response) {
                    case JOptionPane.CLOSED_OPTION:
                    case JOptionPane.NO_OPTION:
                        System.exit(0);
                        break;
                    case JOptionPane.YES_OPTION:
                        reset();
                        return;
                }
            }
        }

        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);
        fruit.drawFruit(g);
        snake.drawSnake(g);

        int snakeX = snake.getSnakeBody().get(0).x;
        int snakeY = snake.getSnakeBody().get(0).y;
        switch (direction) {
            case "right":
                snakeX += CELL_SIZE;
                break;
            case "left":
                snakeX -= CELL_SIZE;
                break;
            case "up":
                snakeY -= CELL_SIZE;
                break;
            case "down":
                snakeY += CELL_SIZE;
                break;
        }
        Node newHead = new Node(snakeX, snakeY);
        // 如何"前進"?
        // 在沒吃到fruit:去掉最後一個Node，並在原行進方向上的第0個位置(蛇頭)，插入一個新的Node。
        // 有吃到fruit:不須去掉最後一個Node，在原行進方向上的第0個位置(蛇頭)，插入一個新的Node。
        if ((snake.getSnakeBody().get(0).x == fruit.getX()) && (snake.getSnakeBody().get(0).y == fruit.getY())) {
            fruit.setNewLocation(snake);
            score++;
        } else {
            snake.getSnakeBody().remove(snake.getSnakeBody().size() - 1);
        }
        snake.getSnakeBody().add(0, newHead);

        allowKeyPress = true; // repaint後才能再key press
        requestFocusInWindow();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(width, height);
    }

    public static void main(String[] args) {
        JFrame window = new JFrame("Snake Game");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setContentPane(new Main());
        window.pack();
        window.setLocationRelativeTo(null);
        window.setVisible(true);
        window.setResizable(false);
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (allowKeyPress) {
            if ((e.getKeyCode() == 37) && !direction.equals("right")) {
                direction = "left";
            } else if ((e.getKeyCode() == 38) && !direction.equals("down")) {
                direction = "up";
            } else if ((e.getKeyCode() == 39) && !direction.equals("left")) {
                direction = "right";
            } else if ((e.getKeyCode() == 40) && !direction.equals("up")) {
                direction = "down";
            }
            allowKeyPress = false;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    public void read_highest_score() {
        try { // 嘗試讀取已有檔案
            File myObj = new File(myfile);
            Scanner myReader = new Scanner(myObj);
            // 問題:若遊戲是第一次執行，會產生filename.txt，filename.txt還未存有最高分資料，
            // 若直接用右上角打叉關閉遊戲，而不是等到game over正常關閉，則filename.txt還是沒有資料，
            // 之後開啟JAR檔，會遇到無反應的狀況，用IntelliJ執行也會發生錯誤，
            // 因為程式執行到 highest_score = myReader.nextInt(); 會出錯，根本沒有int。
            // 已修正:用hasNext()確認是否有值，有就用nextInt()讀，沒有就不用nextInt()，直接把highest_score設為0。
            if (myReader.hasNext()) {
                highest_score = myReader.nextInt();
            } else {
                highest_score = 0;
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            highest_score = 0;
            // 嘗試新增檔案
            try {
                File myObj = new File(myfile);
                if (myObj.createNewFile()) {
                    System.out.println("File created: " + myObj.getName());
                }
                FileWriter myWriter = new FileWriter(myfile);
                myWriter.write("" + 0);
                myWriter.close();
            } catch (IOException err) {
                System.out.println("An error occurred.");
                err.printStackTrace();
            }
        }
    }
    // 更新最高分
    public void write_a_file(int score) {
        try {
            FileWriter myWriter = new FileWriter(myfile);
            if (score > highest_score) {
                myWriter.write("" + score);
                highest_score = score;
            } else {
                myWriter.write("" + highest_score);
            }
            myWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}