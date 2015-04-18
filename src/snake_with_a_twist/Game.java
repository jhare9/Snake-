/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package snake_with_a_twist;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;

/**
 *
 * @author jon
 */
public class Game extends JPanel implements Runnable,KeyListener{
    
    private final static int WIDTH = 400;
    private final static int HEIGHT = 400;
    private boolean running = false;
    private Thread gameThread; 
    private LinkedList<Point> snake; 
    int snake_size; 
    private int snakeX; 
    private int snakeY; 
    private int direction;
    private boolean right; 
    private boolean left; 
    private boolean down; 
    private boolean up;
    private boolean normalFood;
    private boolean poision;
    private double totalElaspedTime;
    private Point food; 
    private int highScore;
    private int score;
    
    public Game(){
        setPreferredSize(new Dimension(WIDTH,HEIGHT));
        setFocusable(true);
        this.addKeyListener(this);
        new_game();  
 }
    
    public void new_game(){
        running = true;
        gameThread = new Thread(this,"game thread");
        
        snake = new LinkedList<Point>();
        
        snake_size = 3;
        
        normalFood = true;
        poision = false;
        
        food = new Point();
        food.setLocation(10,10);
        
        direction = 4;
        down = true;
        up = false;
        right = false;
        left = false;
        score = 0;
        snakeX = 5; 
        snakeY = 5;
        fileReader("highscore.txt");
        System.out.println("high score "+highScore);
        gameThread.start();
      }
    
    public void update(){
             
            //add a new snake part   
            snake.addFirst(new Point(snakeX,snakeY));
            
            // keeps the snake the same length
            if(snake.size() > snake_size)
                snake.removeLast();
            
             // deals with the arrows keys for the snake directions 
            // and the movement of the snake
            switch(direction){
                case 1:
                     snakeY--;
                    break;
                case 2: 
                    snakeX++;
                    break;
                case 3: 
                    snakeX--;
                    break;
                case 4:
                    snakeY++;
                    break;
            }
            
            
            // checks for to see if the snake colide with its self
            for(int i = 1; i<snake.size(); ++i){
                if(snake.getFirst().x == snake.get(i).x && snake.getFirst().y == snake.get(i).y){
                    running = false;
                    fileWriter("highscore.txt");
                }
            }
            
            // food colison
            if(snake.getFirst().x == food.x && snake.getFirst().y == food.y){
                if(normalFood){   
                    snake_size++;
                    score++;
                    
                }else if(poision){
                    snake_size--;
                    score--;
                    snake.removeLast();
                    if(snake_size <=1){
                        running = false;
                        fileWriter("highscore.txt");
                    }
                }
                
                generateFood();
                
                Random n = new Random();
                food.setLocation(n.nextInt(WIDTH /20),n.nextInt(HEIGHT / 20));
            }
            
            if(totalElaspedTime >= 3.0){
                generateFood();
                totalElaspedTime = 0.0;
            }
      
            
            //dealing with wall colisions
            if( snake.getFirst().getY()*20 >= HEIGHT-19.5 || snake.getFirst().getY() *20 <= -1 ||
                    snake.getFirst().getX() * 20 >= WIDTH -19.5 || snake.getFirst().getX() * 20 <= -1){
                
                fileWriter("highscore.txt");
                running = false;
            }           
            
    }   
        

    @Override
    public void paint(Graphics g) {
       // clear the screen to repaint 
       g.clearRect(0, 0,WIDTH,HEIGHT);
       
        // paint the food to the screen
        if(normalFood){
            g.setColor(Color.red);
            
        }
        else if(poision){
            g.setColor(Color.green);
        }
        
        g.fillRect(food.x * 20,food.y*20,20,20);
       
       //paint the snake to the screen
       for(int i = 0; i < snake.size(); i++){
           g.setColor(Color.BLUE);
           g.fillRect((int)(snake.get(i).x*20),(int)(snake.get(i).y*20), 20, 20);
       }
       
       g.setColor(Color.BLACK);
        // paint the verticle lines 
       for(int x = 0; x < WIDTH; x++)
           g.drawLine(x*20,0,x*20,HEIGHT);
       
       //paint the horrizontal lines
       for(int y = 0; y <HEIGHT; y++)
            g.drawLine(0,y * 20,WIDTH, y*20);
       
       //paint scoring system
       g.setColor(Color.RED);
       g.drawString("high score: "+highScore,20,20);
       g.drawString("your Score: "+score,20,HEIGHT -20);
      
    }

    @Override
    public void run(){
        // game loop runs forever unless the player hits a wall or another snake part
        long startTime = System.currentTimeMillis();
        
        while(running){
            
            long currentTime = System.currentTimeMillis();
            double elaspedTime = currentTime - startTime;
            totalElaspedTime += elaspedTime / 1000;
            
            update();
            repaint();
            
            try {
                Thread.sleep(150);
            } catch (InterruptedException ex) {
                Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            startTime = currentTime;
        }
        
        try {
            Thread.currentThread().join();
        } catch (InterruptedException ex) {
            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
   
    public void generateFood(){
         Random n = new Random();
                
                switch(n.nextInt(2)){
                    case 0:
                           normalFood = true;
                           poision = false;
                        break;
                    case 1:
                        poision = true;
                        normalFood = false;
                        break;
                }
    }
    
    public void fileReader(String fileName){
        Scanner file_in = null;
        try {
            
            file_in = new Scanner(new File(fileName));
            
            while(file_in.hasNextLine()){
                highScore = file_in.nextInt();
            }
            
        } catch (Exception e) {
           System.out.println("cant find file");
        }finally{
            try{
            file_in.close();
            }catch(Exception e){
                System.out.println("null pointer");
            }
        }
    }
    
    public void fileWriter(String fileName){
        
        if(score < highScore)
            return; 
        
        BufferedWriter bw = null;
        try {
             bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(fileName))));
            bw.write(score+" ");
            bw.flush();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
        }finally{
            try {
                bw.close();
            } catch (IOException ex) {
                Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }
    
    // not used 
    @Override
    public void keyTyped(KeyEvent e) {}

    // handles the arrow keys 
    @Override
    public void keyPressed(KeyEvent e) {
       switch(e.getKeyCode()){
           case KeyEvent.VK_UP:
               if(!down){
               direction = 1;
               }
               up = true; 
               right = false; 
               left = false;
               break; 
           case KeyEvent.VK_RIGHT:
               if(!left){
               direction = 2;
               }
               right = true;
               up = false;
               down = false; 
               break;
           case KeyEvent.VK_LEFT:
               if(!right){
               direction = 3;
               }
               up = false; 
               down = false;
               left = true;
               break;
           case KeyEvent.VK_DOWN:
               if(!up){
               direction = 4;
               }
               down = true; 
               left = false; 
               right = false; 
               break;
       }
       
       if( e.getKeyCode() == KeyEvent.VK_SPACE)
            new_game();
           
    }
    // not used 
    @Override
    public void keyReleased(KeyEvent e) {}

    }
