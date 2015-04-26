/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor. 
 * ToDo: create a game menu and help menu.
 */
package snake_with_a_twist;

import java.awt.Color;
import java.awt.Dimension;
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
public class Game extends JPanel implements Runnable, KeyListener{
    // screen sizes and block sizes
    private final static int WIDTH = 500; 
    private final static int HEIGHT = 500;
    private final static int BOX_WIDTH = 25;
    private final static int BOX_HEIGHT = 25;
    
    public static boolean running; // boolean to tell if the game is running or not
    private Thread gameThread; // game thread
    // the snakes parts 
    private LinkedList<Point> snake;
    private Point snakeParts;  
    private int snakeLength;
    // the snakes food
    private Point food; 
    private boolean normalFood;
    private boolean poision;
    // the directions
    private boolean right;
    private boolean left; 
    private boolean up; 
    private boolean down; 
    // score variables
    private int score; 
    private int highScore; 
    
    //boolean to detect collisions
    private boolean collision;
    
    //timer for the random generation of foods 
    private long timer;
    private double timeInSec;
    // random for food
    private Random n;
    
    //the game constructor
    public Game(){
        setPreferredSize(new Dimension(WIDTH,HEIGHT));
        setFocusable(true);
        addKeyListener(this); 
        
        start_game();
    }
    
    private void init(){
        
        snakeParts.setLocation(0,0);
        snake.addFirst(snakeParts);
        snakeLength = 3; 
        
        normalFood = true; 
        poision = false; 
        food.setLocation(5,5);
        
        down = false;
        up = false; 
        right = false; 
        left = false; 
        score = 0;    
        
       // colision variable 
        collision = false; 
        // open the score txt file and set the high score variable
        fileReader("highscore.txt");
    }
    
    
    public synchronized void start_game(){
            snake = new LinkedList<>();     
            snakeParts = new Point();
            food = new Point();
            n = new Random();
            init();
            running = true;
            gameThread = new Thread(this,"game thread");
            gameThread.start();
    }
    
    public void update(){
          
          
          long elaspedTime = System.currentTimeMillis();
          timeInSec += (elaspedTime - timer) / 1000.0; 
          
          if(timeInSec > 5.0){
                timer = elaspedTime;
                timeInSec = 0.0;
                switch(n.nextInt(2)){
                    case 0:
                        normalFood = true;
                        poision = false;
                        break;
                    case 1:
                        normalFood = false;
                        poision = true;
                        break;
                }  
            }
        
          if(!collision){
                   
            //add a new point to the head of the snake
            snake.addFirst(new Point(snakeParts.x,snakeParts.y)); 
            
             // delete the last elements if its longer than the length
            while(snake.size() > snakeLength){
                snake.removeLast();
            }
           
             // collision dections 
            for(int s = 3; s < snake.size(); s++)
                if(snake.getFirst().x == snake.get(s).x && snake.getFirst().y == snake.get(s).y){
                            collision = true;
                            fileWriter("highscore.txt");
                }
            // wall collision detections 
            if(snake.getFirst().getY() >  (HEIGHT / BOX_HEIGHT)-1 || snake.getFirst().y * BOX_HEIGHT < 0
                    || snake.getFirst().x * BOX_WIDTH > WIDTH - BOX_WIDTH || snake.getFirst().x < 0){
                collision = true;
                
                if(score > highScore)
                fileWriter("highscore.txt");
            }
            
            if(snake.getFirst().y == food.y && snake.getFirst().x == food.x){
                if(normalFood){
                   snakeLength++;
                   score++;
                }
                else if(poision){
                   snakeLength--;
                   if(snakeLength < 3)
                       collision = true;
                   
                   if(score != 0)
                       score--;
                }
                food.setLocation(new Random().nextInt(WIDTH / BOX_WIDTH),new Random().nextInt(HEIGHT / BOX_HEIGHT));
            }
            
            
            
            if(down)
                snakeParts.y++;
            else if(up)
                snakeParts.y--;
            else if(right)
                snakeParts.x++;
            else if (left)
                snakeParts.x--;
            
          }
          
          
    }
    
    @Override
    public void paint(Graphics g){
        
        g.clearRect(0,0,WIDTH,HEIGHT);
        g.setColor(Color.BLACK);
        g.fillRect(0,0,WIDTH,HEIGHT);
        
        
       
        // draw the snake       
        for(int s = 0; s < snake.size(); s++){
            // set the snakes head to the color of orange.
            if(s == 0){
                g.setColor(Color.orange);
            }
            g.fillRect(snake.get(s).x * BOX_WIDTH, snake.get(s).y * BOX_HEIGHT, BOX_WIDTH,BOX_HEIGHT);
            // set the rest of the snakes body to green.
            g.setColor(Color.green);
        }
        // draw food for the snake
        if(normalFood)
            g.setColor(Color.red);
        else if(poision)
            g.setColor(Color.yellow);
        
        g.fillRect(food.x * BOX_WIDTH,food.y * BOX_HEIGHT,BOX_WIDTH,BOX_HEIGHT);
        
         // draw the verticle lines
        g.setColor(Color.gray);
        
        for(int x = 0; x < WIDTH / BOX_WIDTH; x++)
            g.drawLine(x*BOX_WIDTH,0,x * BOX_WIDTH, HEIGHT);
        
        // draw the horizontal lines 
        for(int y = 0; y < (HEIGHT / BOX_HEIGHT); y++)
            g.drawLine(0,y * BOX_HEIGHT,WIDTH, y * BOX_HEIGHT);
        
       //paint scoring system
       g.setColor(Color.white);
       g.drawString("high score: "+highScore,20,20);
       g.drawString("your Score: "+score,20,HEIGHT -20);
       
       // change colors for the dead snake
       if(collision){
           g.setColor(Color.gray);
           for(int i = 0 ; i < snake.size(); i++){
                g.fillRect(snake.get(i).x * BOX_WIDTH, snake.get(i).y * BOX_HEIGHT, BOX_WIDTH,BOX_HEIGHT);
           }
       }
        
    }
    
    public synchronized void stop_game(){
       
        try {
            gameThread.join();
        } catch (InterruptedException ex) {
            System.out.println("having trouble closing the thread");
        }
    }
    
    @Override
    public void run() {
        // start the timer a 5 updates per second
        long startTime = System.nanoTime();
        double optTime = (1000000000.0 / 5.0);
        double delta = 0;
        int updates = 0;
        int frames = 0; 
        long timer = System.currentTimeMillis();
        //other timer 
        timer = System.currentTimeMillis();
        // the game loop 
        while(running){
            long elaspedTime = System.nanoTime();
            delta += (elaspedTime - startTime) / optTime;
            startTime = elaspedTime; 
            
            
             while(delta >= 1.0 ){
                 update();
                 updates++;
                delta--;
            }
             
              repaint();
              frames++;
             
             if(System.currentTimeMillis() - timer > 1000){
                 timer += 1000;
                 System.out.println("updates "+updates + " frames per sec "+frames);
                 updates = 0;
                 frames = 0;
            } 
             
            
        }
        
        stop_game();
           
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
    
     public void fileReader(String fileName){
        Scanner file_in = null;
        try {
            
            file_in = new Scanner(new File(fileName));
            
            while(file_in.hasNext()){
                highScore = file_in.nextInt();
            }
            
        } catch (FileNotFoundException ex) {
           System.out.println("cant find file");
        }finally{
            try{
            file_in.close();
            }catch(Exception e){
                System.out.println("null pointer");
            }
        }
    }
    
    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        
        switch(e.getKeyCode()){
            case KeyEvent.VK_UP:
                if(!down)
                    up = true;
                right = false; 
                left = false; 
                break;
            case KeyEvent.VK_DOWN:
                if(!up)
                    down = true;
                right = false; 
                left = false;
                break;
            case KeyEvent.VK_RIGHT:
                if(!left)
                    right = true;
                up = false;
                down = false; 
                break;
            case KeyEvent.VK_LEFT:
                if(!right)
                    left = true;
                up = false; 
                down = false; 
                break; 
            case KeyEvent.VK_SPACE:
                snake.clear();
                init();
                break; 
            case KeyEvent.VK_ESCAPE:
                running = false;
                System.exit(0);
                break;
            }
            
    }

    @Override
    public void keyReleased(KeyEvent e) {}
    
    
}
