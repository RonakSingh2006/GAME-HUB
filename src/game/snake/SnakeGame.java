package game.snake;

import java.util.Random;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.ImageIcon;
import javax.swing.Timer;

import gameFrame.Frame;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.time.LocalTime;
import java.time.Duration;

public class SnakeGame extends JFrame{
    
    public SnakeGame(Connection con,String username){
        LocalTime start = LocalTime.now();
        setSize(600,600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setTitle("Snak Game");
        Image icon = new ImageIcon("GAME HUB/src/Game/Snake/icon.png").getImage();
        setIconImage(icon);
        setResizable(false);
        setVisible(true);
        SnakeGameplay play = new SnakeGameplay(con,username);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e){
                new Frame(con,username);
                LocalTime end = LocalTime.now();

                Duration time = Duration.between(start,end);
                int seconds = (int)time.toSeconds();
                int prev = getTime(con,username);
                updateTime(con, username, seconds+prev);
            }
        });
        add(play);
        pack();
    }
    public void updateTime(Connection con , String username ,int seconds){
        String query = "update gamedata set snake_time = ? where username = ?";
        try{
            PreparedStatement ps  = con.prepareStatement(query);
            ps.setInt(1,seconds);
            ps.setString(2,username);
            ps.executeUpdate();
        }catch(SQLException e){};
    }

    public int getTime(Connection con , String username){
        String query = "select snake_time from gamedata where username = ?";
        try{
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getInt("snake_time");
        }
        catch(SQLException e){}
        return 0;
    }
}
class SnakeGameplay extends JPanel implements ActionListener,KeyListener{
    Timer timer;
    Random random;
    int speedX = 0;
    int speedY = 0;
    Image head;

    private class Tile{
        int x,y;

        Tile(int x , int y){
            this.x = x;
            this.y = y;
        }
    }

    int tileSize = 25;

    // Snake
    Tile snakeHead;
    ArrayList<Tile> body;

    //Enemy
    Tile enemy;

    boolean gameOver = false;
    int score = 0;
    int highscore;

    Connection conn;
    String user;

    SnakeGameplay(Connection con,String username){

        conn = con;
        user = username;

        highscore = getHighscore(con, username);


        timer = new Timer(100,this); 
        timer.start();
        random = new Random();

        setBackground(Color.BLACK);
        setPreferredSize(new Dimension(600,600));

        head = new ImageIcon("GAME HUB/src/Game/Snake/head.png").getImage();


        body = new ArrayList<>();

        snakeHead = new Tile(random.nextInt(5),random.nextInt(5));
        enemy = new Tile(0,0);
        enemyGenerator();


        setFocusable(true);
        addKeyListener(this);
        setFocusTraversalKeysEnabled(false);

    }

    public int getHighscore(Connection con,String username){
        int x = 0;
        String query = "SELECT snake_highscore FROM gamedata WHERE username = ?";
        try{
            PreparedStatement stat = con.prepareStatement(query);
            stat.setString(1,username);
            ResultSet rs = stat.executeQuery();
            if(rs.next()){
                x = rs.getInt("snake_highscore");
            }
        }
        catch(SQLException e){}
        return x;
    }
    public void updateHighscore(Connection con , String username, int x){
        String query = "UPDATE gamedata SET snake_highscore = ? WHERE username = ?";
        try{
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1,x);
            ps.setString(2, username);
            ps.executeUpdate();
        }
        catch(SQLException e){};
    }

    public void actionPerformed(ActionEvent e){
        if(gameOver){
            timer.stop();
            if(score > highscore) updateHighscore(conn, user, score);
        }
        move();
        repaint();
    }

    public void paint(Graphics g){
        super.paint(g);
        
        //Snake Head
        g.drawImage(head,snakeHead.x*tileSize,snakeHead.y*tileSize,tileSize,tileSize,null);
        
        // Snake Body
        g.setColor(Color.GREEN);
        for(int i=0 ; i<body.size() ; i++){
            Tile snakePart = body.get(i);
            g.fill3DRect(snakePart.x*tileSize,snakePart.y*tileSize,tileSize,tileSize,true);
        }

        //Enemy
        g.setColor(Color.RED);
        g.fill3DRect(enemy.x*tileSize,enemy.y*tileSize,tileSize,tileSize,true);
        
        //score
        g.setColor(Color.ORANGE);
        g.setFont(new Font("Arial",Font.BOLD,24));
        g.drawString("Score : "+score,30,30);

        //Highscore
        g.setColor(Color.RED);
        g.setFont(new Font("Arial",Font.BOLD,24));
        g.drawString("High Score : "+highscore,400,30);
        
        // GameOver
        if(gameOver){
            g.setColor(Color.RED);
            g.setFont(new Font("Arial",Font.BOLD,50));
            g.drawString("Game Over : "+score, 120, 300);
            return;
        }


    }

    public void enemyGenerator(){
        enemy.x = random.nextInt(24); // 600/tilesize = 24;
        enemy.y = random.nextInt(24);
        if(!body.isEmpty()){
            for(Tile tile : body){
                if(collision(tile, enemy)){
                    enemyGenerator();
                    return;
                }
            }
        }
    }
    

    public void move(){

        if(collision(snakeHead, enemy)){
            body.add(new Tile(enemy.x , enemy.y));
            score++;
            enemyGenerator();
        }

        for(int i=body.size()-1; i>=0 ; i--){
            Tile bodyTile = body.get(i);
            if(i==0){
                bodyTile.x = snakeHead.x;
                bodyTile.y = snakeHead.y;
            }
            else{
                Tile prev = body.get(i-1);
                bodyTile.x = prev.x;
                bodyTile.y = prev.y;

            }
        }
        snakeHead.x += speedX;
        snakeHead.y += speedY;

        for(Tile bodyTile : body){

            if(body.isEmpty()) break;
            if(collision(bodyTile, snakeHead)) gameOver = true;
        }
        
        if(snakeHead.x > 24) snakeHead.x = 0;
        if(snakeHead.x < 0) snakeHead.x = 24;
        if(snakeHead.y > 24) snakeHead.y = 0;
        if(snakeHead.y < 0) snakeHead.y = 24;

    }
    public boolean collision(Tile t1 , Tile t2){
        if(t1.x == t2.x && t1.y == t2.y) return true;
        return false;
    }

    
   
    public void keyPressed(KeyEvent e) {

       if( (e.getKeyCode()==KeyEvent.VK_UP || e.getKeyCode()==KeyEvent.VK_W ) &&  speedY!=1){
            speedY = -1;
            speedX =  0;
       }
       if( (e.getKeyCode()==KeyEvent.VK_DOWN || e.getKeyCode()==KeyEvent.VK_S  )&& speedY!=-1){
            speedY = 1;
            speedX = 0;
       }
       if( (e.getKeyCode()==KeyEvent.VK_LEFT || e.getKeyCode()==KeyEvent.VK_A  )&& speedX!=1){
            speedX = -1;
            speedY = 0;
       }
       if( (e.getKeyCode()==KeyEvent.VK_RIGHT || e.getKeyCode()==KeyEvent.VK_D ) && speedX!=-1){
            speedX = 1;
            speedY = 0;
       }

       if(e.getKeyCode() == KeyEvent.VK_ENTER){

            // Restart
            gameOver = false;
            score = 0;
            body.clear();
            timer.start();
            speedX = 0;
            speedY = 0;
            snakeHead.x = random.nextInt(5);
            snakeHead.y = random.nextInt(5);
            highscore = getHighscore(conn, user);
            enemyGenerator();
       }

    }
    
    public void keyTyped(KeyEvent e) {}
    public void keyReleased(KeyEvent e) {}

}