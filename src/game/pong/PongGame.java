package game.pong;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

import gameFrame.Frame;

import javax.swing.ImageIcon;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.Image;
import java.awt.Rectangle;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalTime;
import java.time.Duration;

public class PongGame extends JFrame{

    public PongGame(Connection con,String username){
        LocalTime start = LocalTime.now();
        setSize(1000,600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        Image icon = new ImageIcon("GAME HUB/src/Game/Pong/ping-pong.png").getImage();
        setIconImage(icon);
        setTitle("Ping Pong");

        PongGameplay play = new PongGameplay();
        add(play);

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

        pack();
        setVisible(true);
    }

    public void updateTime(Connection con , String username ,int seconds){
        String query = "update gamedata set pong_time = ? where username = ?";
        try{
            PreparedStatement ps  = con.prepareStatement(query);
            ps.setInt(1,seconds);
            ps.setString(2,username);
            ps.executeUpdate();
        }catch(SQLException e){};
    }

    public int getTime(Connection con , String username){
        String query = "select pong_time from gamedata where username = ?";
        try{
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getInt("pong_time");
        }
        catch(SQLException e){}
        return 0;
    }
}

class PongGameplay extends JPanel implements ActionListener,KeyListener{

    Timer gameloop;

    int speedX = 8;
    int speedY = 8;

    // ball
    int posX = 300;
    int posY = 250;
    int ballWidth = 25;
    int ballHeigth = 25;

    //paddle

    int paddleWidth = 10;
    int paddleHeight = 150;
    int paddle1X = 10;
    int paddle1Y = 250;
    int paddle2X = 1000-paddle1X-paddleWidth;
    int paddle2Y = 250;


    // score

    int scoreX = 0;
    int scoreY = 0;

    boolean gameOver = false;

    PongGameplay(){
        setPreferredSize(new Dimension(1000,600));
        setBackground(Color.BLACK);

        gameloop = new Timer(1000/60, this);
        gameloop.start();

        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        addKeyListener(this);

    }

    public void paint(Graphics g){
        super.paint(g);

        //ball
        g.setColor(Color.WHITE);
        g.fillOval(posX,posY,ballWidth,ballHeigth);

        //paddle
        g.setColor(Color.BLUE);
        g.fillRect(paddle1X,paddle1Y,paddleWidth,paddleHeight);
        g.setColor(Color.RED);
        g.fillRect(paddle2X,paddle2Y,paddleWidth,paddleHeight);

        // partion
        g.setColor(Color.WHITE);
        g.drawLine(500,0,500,600);

        //Score

        g.setColor(Color.CYAN);
        g.setFont(new Font("Arial",Font.PLAIN,24));

        g.drawString("Score : "+scoreX,200,20);

        g.drawString("Score : "+scoreY,700,20);

        // Game Over
        if(gameOver){
            g.setFont(new Font("Arial",Font.BOLD,40));
            g.setColor(Color.ORANGE);
            if(scoreX == 10)g.drawString("Player 1 Won!",400,300);
            else g.drawString("Player 2 Won!",400,300);
        }


    }

    public void actionPerformed(ActionEvent e) {
        if(gameOver) gameloop.stop();

        move();

        repaint();
    }

    public void move(){

        // Ball
        posX += speedX;
        posY += speedY;

        if(posX > 1000 - ballWidth || posX < 0){

            if(posX < 0) scoreY++;
            else scoreX++;

            speedX = -speedX;
        } 
        if(posY > 600 - ballHeigth || posY < 0) speedY = -speedY;

        if(scoreX == 10 || scoreY == 10){
            gameOver = true;
        }

        // collison

        Rectangle paddle1 = new Rectangle(paddle1X,paddle1Y,paddleWidth,paddleHeight);
        Rectangle paddle2 = new Rectangle(paddle2X,paddle2Y,paddleWidth,paddleHeight);
        Rectangle ball = new Rectangle(posX,posY,ballWidth,ballHeigth);

        if(paddle1.intersects(ball) || paddle2.intersects(ball)){
            speedX = -speedX;
        }

    }

    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_UP){
            if(paddle2Y > 0) paddle2Y -= 25;
        }
        if(e.getKeyCode() == KeyEvent.VK_DOWN){
            if(paddle2Y < 600 - paddleHeight) paddle2Y += 25;
        }
        if(e.getKeyCode() == KeyEvent.VK_W){
            if(paddle1Y > 0) paddle1Y -= 25;
        }
        if(e.getKeyCode() == KeyEvent.VK_S){
            if(paddle1Y < 600 - paddleHeight) paddle1Y += 25;
        }

        if(e.getKeyCode() == KeyEvent.VK_ENTER){
            restart();
        }
    }
    public void restart(){
        gameOver = false;
        scoreX = 0;
        scoreY = 0;
        posX = 500;
        posY = 300;
        paddle1Y = 250;
        paddle2Y = 250;
        gameloop.start();
    }
    public void keyTyped(KeyEvent e) {}
    public void keyReleased(KeyEvent e) {}
}