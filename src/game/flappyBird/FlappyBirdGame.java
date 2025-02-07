package game.flappyBird;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Rectangle;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Random;


import javax.swing.Timer;

import gameFrame.Frame;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.time.LocalTime;
import java.time.Duration;

public class FlappyBirdGame extends JFrame{

    public FlappyBirdGame(Connection con,String username){
        LocalTime start = LocalTime.now();
        setSize(360,640);
        setLocationRelativeTo(null);
        setTitle("Flappy Bird");
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        ImageIcon icon = new ImageIcon("GAME HUB/src/Game/FlappyBird/Images/bird.png");
        setIconImage(icon.getImage());
        FlappyBirdGameplay flappyBird = new FlappyBirdGameplay(con,username);
        flappyBird.setBounds(360,640,0,0);
        flappyBird.requestFocus();
        add(flappyBird);

        addWindowListener(new WindowAdapter(){
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
        String query = "update gamedata set flappyBird_time = ? where username = ?";
        try{
            PreparedStatement ps  = con.prepareStatement(query);
            ps.setInt(1,seconds);
            ps.setString(2,username);
            ps.executeUpdate();
        }catch(SQLException e){};
    }

    public int getTime(Connection con , String username){
        String query = "select flappyBird_time from gamedata where username = ?";
        try{
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getInt("flappyBird_time");
        }
        catch(SQLException e){}
        return 0;
    }
}
class FlappyBirdGameplay extends JPanel implements KeyListener,ActionListener{
    Image background = new ImageIcon("GAME HUB/src/Game/FlappyBird/Images/flappybirdbg.png").getImage();
    Image bird = new ImageIcon("GAME HUB/src/Game/FlappyBird/Images/flappybird.png").getImage();
    Image toppipe = new ImageIcon("GAME HUB/src/Game/FlappyBird/Images/toppipe.png").getImage();
    Image bottompipe = new ImageIcon("GAME HUB/src/Game/FlappyBird/Images/bottompipe.png").getImage();

    boolean start = false;

    // Bird
    int birdX = 40;
    int birdY = 320;

    // Pipes
    int pipeX = 360;
    int pipeY = 0;
    int pipeH = 512;

    class Pipe{
        int  x = pipeX;
        int y = pipeY;
        int h = pipeH;

        boolean passed = false;
        Image img;
        Pipe(Image i){
            this.img =i;
        }
    }
    
    // Score

    int score = 0;
    int highscore;

    // Logic

    int speedX = -4;
    int speedY = 0;
    int gravity = 1;

    Timer timer;
    Timer placePipeTimer;

    ArrayList<Pipe> pipes;

    boolean gameOver = false;

    
    Random random = new Random();

    public void placePipe(){

        int open = 160;

        Pipe top = new Pipe(toppipe);
        top.h = random.nextInt(128,384);
        pipes.add(top);

        Pipe bottom = new Pipe(bottompipe);
        bottom.y = top.h + open;
        pipes.add(bottom);

        if(pipes.size() > 6){
            pipes.remove(0);
        }


    }
    private Connection conn;
    private String user;
    FlappyBirdGameplay(Connection con , String username){
        conn = con;
        user = username;
        highscore = getHighscore(con, username);

        setPreferredSize(new Dimension(360,640));
        timer = new Timer(1000/60,this);   // 60fbs

        placePipeTimer = new Timer(1500, new ActionListener() {
            public void actionPerformed(ActionEvent e){
                placePipe();
            }
        });

        pipes = new ArrayList<>();

        timer.start();
        placePipeTimer.start();

        addKeyListener(this);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
    }


    public void paint(Graphics g){
        super.paint(g);
        
        // Background
        g.drawImage(background,0,0,360,640,null);

        // Bird
        g.drawImage(bird,birdX,birdY+speedY,34,24,null);

        //pipe
        for(int i=0 ; i<pipes.size() ; i++){
            Pipe pipe = pipes.get(i);
            g.drawImage(pipe.img,pipe.x,pipe.y,64,pipe.h,null);
        }
        
        // Game Over
        if(gameOver){
            g.setColor(Color.RED);
            g.setFont(new Font("serif",Font.BOLD,32));
            g.drawString("Game Over : "+score,80,315);
        }

        // Score 
        g.setColor(Color.BLACK);
        g.setFont(new Font("serif",Font.BOLD,20));
        g.drawString("Score : "+score,10,20);

        // Highscore
        g.drawString("High Score : "+highscore,210,20);
        

    }

    public void actionPerformed(ActionEvent e){

        if(gameOver){
            timer.stop();
            placePipeTimer.stop();
            if(score > highscore) updateHighscore(conn, user, score);
        }

        speedY += gravity;
        birdY+=speedY;
        birdY = Math.max(birdY,0);

        // Game Over Condition
        if(birdY > 640-24) gameOver = true;

        Rectangle birdRect = new Rectangle(birdX,birdY,34,24);
        Rectangle pipeRect;

        for(int i=0 ; i<pipes.size() ; i++){
            Pipe pipe = pipes.get(i);

            // Game Over Condition
            pipeRect = new Rectangle(pipe.x,pipe.y,64,pipe.h);
            if(pipeRect.intersects(birdRect)) gameOver = true;

            // Score
            if(!pipe.passed && birdX > pipe.x + 64){
                pipe.passed = true;
                score+=5;
            }

            // pipe movement
            pipe.x += speedX;
        }

        repaint();
    }

    public int getHighscore(Connection con,String username){
        int x = 0;
        String query = "SELECT flappyBird_highscore FROM gamedata WHERE username = ?";
        try{
            PreparedStatement stat = con.prepareStatement(query);
            stat.setString(1,username);
            ResultSet rs = stat.executeQuery();
            if(rs.next()){
                x = rs.getInt("flappyBird_highscore");
            }
        }
        catch(SQLException e){}
        return x;
    }

    public void updateHighscore(Connection con , String username, int x){
        String query = "UPDATE gamedata SET flappyBird_highscore = ? WHERE username = ?";
        try{
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1,x);
            ps.setString(2, username);
            ps.executeUpdate();
        }
        catch(SQLException e){};
    }

    
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode()==KeyEvent.VK_UP || e.getKeyCode()==KeyEvent.VK_W || e.getKeyCode()==KeyEvent.VK_SPACE){
            speedY = -10;
        }
        if(e.getKeyCode() == KeyEvent.VK_ENTER){
            highscore = getHighscore(conn, user);
            gameOver = false;
            score = 0;
            pipes.clear();
            birdY = 320;
            speedY = 0;
            timer.start();
            placePipeTimer.start();
            repaint();
        }
    }

    public void keyTyped(KeyEvent e) {}
    public void keyReleased(KeyEvent e) {}
}