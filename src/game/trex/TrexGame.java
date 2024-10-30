package game.trex;
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
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;

import java.time.LocalTime;
import java.time.Duration;

public class TrexGame extends JFrame{

    public TrexGame(Connection con,String username){
        LocalTime start = LocalTime.now();
        setSize(750,250);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        Image icon = new ImageIcon("GAME HUB/src/Game/Trex/Image/icon.png").getImage();
        setTitle("T-rex");
        setIconImage(icon);
        setResizable(false);
        TrexGameplay play = new TrexGameplay(con,username);
        add(play);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e){
                new Frame(con,username);
                LocalTime end = LocalTime.now();
                Duration time = Duration.between(start,end);
                int seconds = (int)time.toSeconds();
                int prevSeconds = getTime(con, username);
                updateTime(con, username, prevSeconds+seconds);
            }
        });
        pack();
        setVisible(true);
        play.requestFocusInWindow();
    }

    public void updateTime(Connection con , String username ,int seconds){
        String query = "update gamedata set trex_time = ? where username = ?";
        try{
            PreparedStatement ps  = con.prepareStatement(query);
            ps.setInt(1,seconds);
            ps.setString(2,username);
            ps.executeUpdate();
        }catch(SQLException e){};
    }

    public int getTime(Connection con , String username){
        String query = "select trex_time from gamedata where username = ?";
        try{
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getInt("trex_time");
        }
        catch(SQLException e){}
        return 0;
    }
}

class TrexGameplay extends JPanel implements ActionListener,KeyListener{

    Image dinoDead = new ImageIcon("GAME HUB/src/Game/Trex/Image/dino-dead.png").getImage();
    Image dinoRun = new ImageIcon("GAME HUB/src/Game/Trex/Image/dino-run.gif").getImage();
    Image dinoJump = new ImageIcon("GAME HUB/src/Game/Trex/Image/dino-jump.png").getImage();
    Image cactus1 = new ImageIcon("GAME HUB/src/Game/Trex/Image/cactus1.png").getImage();
    Image cactus2 = new ImageIcon("GAME HUB/src/Game/Trex/Image/cactus2.png").getImage();
    Image cactus3 = new ImageIcon("GAME HUB/src/Game/Trex/Image/cactus3.png").getImage();
    Image track = new ImageIcon("GAME HUB/src/Game/Trex/Image/track.png").getImage();
    Image gameOverImage = new ImageIcon("GAME HUB/src/Game/Trex/Image/game-over.png").getImage();
    Image cloud = new ImageIcon("GAME HUB/src/Game/Trex/Image/cloud.png").getImage();
    
    // Motion
    Timer timer;

    Timer cactusTimer;

    class Block{
        int x,y;
        int width,height;
        Image image;

        Block(Image image , int  x , int y , int width , int height ){
            this.image = image;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }
    }
    // Dinasour
    int dinoWidth = 88;
    int dinoHeight = 94;

    int posX = 50;
    int posY = 250-dinoHeight;

    //cactus

    int cactus1Width = 34;
    int cactus2Width = 69;
    int cactus3Width = 102;

    int cactusHeight = 70;
    int cactusX = 750;
    int cactusY = 250 - cactusHeight;


    Block dinosaur;
    ArrayList<Block> cactusArray;

    //physics
    int gravity = 1;
    int speedY = 0;
    int speedX = 12;

    boolean gameOver = false;

    int score = 0;
    int highscore;

    private Connection conn;
    private String user;

    TrexGameplay(Connection con,String username){
        conn = con;
        user = username;
        highscore = getHighscore(con, username);

        setBackground(Color.LIGHT_GRAY);
        setPreferredSize(new Dimension(750,250));

        timer = new Timer(1000/60,this);
        timer.start();

        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        addKeyListener(this);

        dinosaur = new Block(dinoRun,posX,posY,dinoWidth,dinoHeight);

        cactusArray = new ArrayList<>();

        cactusTimer = new Timer(1500, new ActionListener() {
            public void actionPerformed(ActionEvent e){
                cactusGenerator();
                score += 10;
            }
        });
        cactusTimer.start();
        
    }

    public int getHighscore(Connection con,String username){
        int x = 0;
        String query = "SELECT trex_highscore FROM gamedata WHERE username = ?";
        try{
            PreparedStatement stat = con.prepareStatement(query);
            stat.setString(1,username);
            ResultSet rs = stat.executeQuery();
            if(rs.next()){
                x = rs.getInt("trex_highscore");
            }
        }
        catch(SQLException e){}
        return x;
    }
    public void updateHighscore(Connection con , String username, int x){
        String query = "UPDATE gamedata SET trex_highscore = ? WHERE username = ?";
        try{
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1,x);
            ps.setString(2, username);
            ps.executeUpdate();
        }
        catch(SQLException e){};
    }

    public void paint(Graphics g){
        super.paint(g);

        // Track
        g.drawImage(track,0,230,750,20,null);

        // Cloud
        g.drawImage(cloud,100,10,100,100,null);
        g.drawImage(cloud,320,50,150,120,null);
        g.drawImage(cloud,580,20,120,100,null);

        // Dinosour
        g.drawImage(dinosaur.image,dinosaur.x,dinosaur.y,dinosaur.width,dinosaur.height,null);

        // Cactus
        for(Block cactus : cactusArray){
            g.drawImage(cactus.image,cactus.x, cactus.y , cactus.width , cactus.height,null);
        }

        // GameOver
        if(gameOver == true){
            g.drawImage(gameOverImage, 50, 100, 650, 50, null);
        }

        //Score
        g.setColor(Color.RED);
        g.setFont(new Font("Arial",Font.BOLD,22));
        g.drawString("Score : "+score,10,20);

        // Highscore
        g.setFont(new Font("Arial",Font.BOLD,22));
        g.drawString("High Score : "+highscore,550,20);

        

    }

    public void actionPerformed(ActionEvent e) {
        if(gameOver){
            timer.stop();
            cactusTimer.stop();
            if(score > highscore){
                updateHighscore(conn,user, score);
            }
           
            repaint();
        }

        move();

        repaint();
    }

    public void move(){

        // Dinasour
        speedY += gravity;
        dinosaur.y += speedY;

        if(dinosaur.y > 250-dinoHeight){
            dinosaur.y = 250-dinoHeight;
            speedY = 0;
            dinosaur.image = dinoRun;
        }

        //Cactus
        for(Block cactus : cactusArray){
            cactus.x -= speedX;
        }

        for(Block cactus : cactusArray){
            if(collision(cactus, dinosaur)){
                gameOver = true;
                dinosaur.image = dinoDead;
            } 
        }

    }

    public boolean collision(Block b1 , Block b2){

        Rectangle r1 = new Rectangle(b1.x , b1.y , b1.width , b1.height);
        Rectangle r2 = new Rectangle(b2.x , b2.y , b2.width , b2.height);

        if(r1.intersects(r2)) return true;
        return false;
    }

    public void cactusGenerator(){
        double random = Math.random();
        Block cactusNew;
        if(random > 0.9){
            cactusNew = new Block(cactus3, cactusX, cactusY, cactus3Width, cactusHeight);      
        }
        else if(random > 0.5 && random <= 0.9){
            cactusNew = new Block(cactus2, cactusX, cactusY, cactus2Width, cactusHeight);    
        }
        else{
            cactusNew = new Block(cactus1, cactusX, cactusY, cactus1Width, cactusHeight);   
        }
        cactusArray.add(cactusNew);

        // Memory Managment
        if(cactusArray.size() > 5){
            cactusArray.remove(0);
        }
    }

    public void keyPressed(KeyEvent e) {

        if(e.getKeyCode() == KeyEvent.VK_SPACE){
            
            if(dinosaur.y == 250-dinoHeight){
                speedY = -17;
                dinosaur.image = dinoJump;
            }
        }
        if(e.getKeyCode() == KeyEvent.VK_ENTER){
            reset();
        }
       
    }

    public void reset(){
        timer.start();
        cactusTimer.start();
        gameOver = false;
        cactusArray.clear();
        score = 0;
        highscore = getHighscore(conn, user);
    }
    
    
    public void keyTyped(KeyEvent e) {}
    public void keyReleased(KeyEvent e) {}

}