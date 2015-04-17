/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package snake2;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.LinkedList;
import java.util.Random;
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
    private int x; 
    private int y; 
    private int direction;
    private boolean right; 
    private boolean left; 
    private boolean down; 
    private boolean up;
    private int foodX;
    private int foodY;
    
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
        
        foodX = 10; 
        foodY = 10;
        direction = 4;
        down = true;
        up = false;
        right = false;
        left = false;
        
        x = 5; 
        y = 5;
      
        gameThread.start();
      }
    
    public void update(){
             
            //add a new snake part   
            snake.addFirst(new Point(x,y));
            
            // deals with the arrows keys for the snake directions
            switch(direction){
                case 1:
                     y--;
                    break;
                case 2: 
                    x++;
                    break;
                case 3: 
                    x--;
                    break;
                case 4:
                    y++;
                    break;
            }
            
            // checks for to see if the snake colide with its self
            for(int i = 1; i<snake.size(); ++i){
                if(snake.getFirst().x == snake.get(i).x && snake.getFirst().y == snake.get(i).y)
                    running = false;
            }
            
            // food colison
            if(snake.getFirst().x == foodX && snake.getFirst().y == foodY){
                 Random n = new Random();
                 foodX = n.nextInt((WIDTH / 20));
                 foodY = n.nextInt(HEIGHT / 20);
                 snake_size++;
             }
            
            //dealing with wall colisions
            if( snake.getFirst().getY()*20 >= HEIGHT-19.5)
                running = false;
            
            else if(snake.getFirst().getY() *20 <= -1)
                running = false;
            
            else if(snake.getFirst().getX() * 20 >= WIDTH -19.5)
                running = false;
            
            else if(snake.getFirst().getX() * 20 <= -1)
                running = false;
           
            // keeps the snake the same length
            if(snake.size() > snake_size)
                snake.removeLast();
    }   
        

    @Override
    public void paint(Graphics g) {
       // clear the screen to repaint 
       g.clearRect(0, 0,WIDTH,HEIGHT);
       
        // paint the food to the screen
       g.setColor(Color.red);
       g.fillRect(foodX*20,foodY*20,20, 20);
       
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
      
    }

    @Override
    public void run(){
        // game loop runs forever unless the player hits a wall or another snake part
        while(running){
            update();
            repaint();
            
            try {
                Thread.sleep(150);
            } catch (InterruptedException ex) {
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
