/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package snake2;

import javax.swing.JFrame;

/**
 *
 * @author jon
 */
public class Snake2 extends JFrame{
    
    public Snake2(){
       
        setTitle("Snake"); 
        setResizable(false);
        add(new Game());
        pack();
        
        setLocationRelativeTo(null);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        new Snake2();
        
    }
    
}
