package game.brickBreaker;

import javax.swing.Timer;

import gameFrame.Frame;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.time.LocalTime;
import java.time.Duration;

public class BrickBreakerGame extends JFrame{
    public BrickBreakerGame( Connection con,String username) {
        
        LocalTime start = LocalTime.now();
        setSize(700, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        
        setTitle("Brick Breaker");
        ImageIcon icon = new ImageIcon("GAME HUB/src/Game/BrickBreaker/BrickBreakerIcon.png");
        setIconImage(icon.getImage());
        
        Gameplay play = new Gameplay(con,username);
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
        
        setVisible(true);
        play.requestFocusInWindow();
        revalidate();
    }

    public void updateTime(Connection con , String username ,int seconds){
        String query = "update gamedata set brickBreaker_time = ? where username = ?";
        try{
            PreparedStatement ps  = con.prepareStatement(query);
            ps.setInt(1,seconds);
            ps.setString(2,username);
            ps.executeUpdate();
        }catch(SQLException e){};
    }

    public int getTime(Connection con , String username){
        String query = "select brickBreaker_time from gamedata where username = ?";
        try{
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getInt("brickBreaker_time");
        }
        catch(SQLException e){}
        return 0;
    }
    
}

class Gameplay extends JPanel implements KeyListener,ActionListener{

    boolean gameOver = false;
    int totalBricks = 21;
    Timer timer;
    int score = 0;
    int highscore;
    int delay = 8;
    int ballposX = 120;
    int ballposY = 350;
    int speedX = -2;
    int speedY = -2;
    int playerX = 350;
    BrickGenerator bg;

    Connection conn;
    String user;
    public Gameplay( Connection con , String username) {
        conn = con;
        user = username;
        highscore = getHighscore(con, username);

        addKeyListener(this);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        timer = new Timer(delay,this);
        timer.start();

        bg = new BrickGenerator(3,7);

    }
    public int getHighscore(Connection con,String username){
        int x = 0;
        String query = "SELECT brickBreaker_highscore FROM gamedata WHERE username = ?";
        try{
            PreparedStatement stat = con.prepareStatement(query);
            stat.setString(1,username);
            ResultSet rs = stat.executeQuery();
            if(rs.next()){
                x = rs.getInt("brickBreaker_highscore");
            }
        }
        catch(SQLException e){}
        return x;
    }
    public void updateHighscore(Connection con , String username, int x){
        String query = "UPDATE gamedata SET brickBreaker_highscore = ? WHERE username = ?";
        try{
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1,x);
            ps.setString(2, username);
            ps.executeUpdate();
        }
        catch(SQLException e){};
    }

    public void paint(Graphics g) {
        super.paint(g);

        // Background
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, 700, 600);

        // Borders
        g.setColor(Color.YELLOW);
        g.fillRect(0, 0, 700, 10);  // Top border
        g.fillRect(0, 555, 700, 10);  // Bottom border
        g.fillRect(0, 0, 10, 600);  // Left border
        g.fillRect(677, 0, 10, 600);  // Right border

        //Paddle
        g.setColor(Color.CYAN);
        g.fillRect(playerX,540,85,8);

        // Ball
        g.setColor(Color.RED);
        g.fillOval(ballposX,ballposY,20,20);

        // Bicks
        bg.draw((Graphics2D)g);

        // Score
        g.setColor(Color.GREEN);
        g.setFont(new Font("Arial",Font.BOLD,24));
        g.drawString("Score : "+score,30,35);

        //Highscore
        g.setColor(Color.RED);
        g.drawString("High Score : "+highscore,450,35);

        // Game Over
        if(gameOver){
            g.setColor(Color.RED);
            g.setFont(new Font("Arial",Font.BOLD,50));
            g.drawString("Game Over",200,280);
        } 
        
        
    }
    @Override 
    public void actionPerformed(ActionEvent e){

        if(gameOver) {
            timer.stop();
            if(score > highscore) updateHighscore(conn, user, score);
        }
        move();
        repaint();
    }

    public void move(){
        if(ballposX <= 10 || ballposX >=655) speedX = -speedX;
        if(ballposY <= 10 || ballposY >=538 ) speedY = -speedY;

        ballposX+=speedX;
        ballposY+=speedY;

        Rectangle ballRect = new Rectangle(ballposX,ballposY,20,20);
        Rectangle paddleRect = new Rectangle(playerX,540,85,8);
    
        if(ballRect.intersects(paddleRect)) speedY = -speedY;

        for(int i=0 ; i<bg.map.length ; i++){
            for(int j=0; j<bg.map[0].length ; j++){
                if(bg.map[i][j] > 0){
                    int width = bg.brickWidth;
                    int height = bg.brickHeight;

                    int bX = j*width+80;
                    int bY = i*height+50;

                    Rectangle brickRect = new Rectangle(bX,bY,width,height);
                    if(brickRect.intersects(ballRect)){
                        bg.map[i][j]  = 0;
                        totalBricks--;
                        score+=5;

                        if (ballposX + 19 <= bX || ballposX + 1 >= bX + width) speedX = -speedX;
                        else speedY = -speedY;
                    }

                }
            }
            if(ballposY>=538 || totalBricks == 0){
                gameOver = true;
            }
        }
    }
    public void restart(){
        ballposX = 120;
        ballposY = 350;
        speedX = -2;
        speedY = -2;
        playerX = 350;
        totalBricks = 21;
        bg = new BrickGenerator(3, 7);  // Regenerate bricks
        score = 0;
        gameOver = false;
        timer.start();
        highscore = getHighscore(conn, user);
        repaint();
    }
    
    public void keyPressed(KeyEvent e) {

        if(e.getKeyCode()== KeyEvent.VK_LEFT){
            if(playerX > 10) playerX -=20;
        }
        if(e.getKeyCode()== KeyEvent.VK_RIGHT){
            if(playerX < 577) playerX +=20;
        }
        if(e.getKeyCode() == KeyEvent.VK_ENTER){
            restart();
        }
    }
    
   
    public void keyTyped(KeyEvent e) {}
    public void keyReleased(KeyEvent e) {}

    
}

class BrickGenerator{
    int map[][];
    int brickWidth;
    int brickHeight;

    BrickGenerator(int row , int column){
        map = new int[row][column];
        for(int i=0 ; i<row ; i++){
            for(int j=0 ; j<column ; j++){
                map[i][j] = 1;
            }
        }
        brickWidth = 540/column;
        brickHeight = 150/row;
    }
    
    public void draw(Graphics2D g){
        for(int i=0 ; i<map.length ; i++){
            for(int j=0 ; j<map[0].length ; j++){
                if(map[i][j] > 0){
                    // Bricks
                    g.setColor(Color.WHITE);
                    g.fillRect(j*brickWidth+80,i*brickHeight+50,brickWidth,brickHeight);
                    //Border
                    g.setColor(Color.BLACK);
                    g.setStroke(new BasicStroke(3));
                    g.drawRect(j*brickWidth+80,i*brickHeight+50,brickWidth,brickHeight);

                }
            }
        }
    }
}