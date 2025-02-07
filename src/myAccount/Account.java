package myAccount;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.BasicStroke;
import java.awt.Image;
import java.time.Duration;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;

import gameFrame.Frame;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Account extends JFrame{
    Image gameIcon = new ImageIcon("GAME HUB/src/MyAccount/game.png").getImage();
    Image usericon = new ImageIcon("GAME HUB/src/MyAccount/user.png").getImage();
    Image brickbreakerImage = new ImageIcon("GAME HUB/src/MyAccount/BrickBreakerIcon.png").getImage();
    Image flappyBirdImage = new ImageIcon("GAME HUB/src/MyAccount/flappyBirdIcon.png").getImage();
    Image trexImage = new ImageIcon("GAME HUB/src/MyAccount/trexicon.png").getImage();
    Image snakeImage = new ImageIcon("GAME HUB/src/MyAccount/snakeIcon.png").getImage();

    int trex_highscore;
    int flappyBird_highscore;
    int snake_highscore;
    int brickBreaker_highscore;

    int trex_time;
    int flappyBird_time;
    int snake_time;
    int brickBreaker_time;
    int tiktaktoe_time;
    int pong_time;



    public Account(Connection con , String username){

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e){
                new Frame(con,username);
            }
        });

        getHighscore(con, username);
        getTime(con, username);

        setSize(850,750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        setTitle("User Data");
        setIconImage(gameIcon);

        Panel panel = new Panel();
        panel.setLayout(null);
        setContentPane(panel);


        // Usericon
        JLabel userLabel = new JLabel(new ImageIcon(usericon));
        userLabel.setBounds(350,20,150,150);
        panel.add(userLabel);

        // Username 

        JLabel usernameLabel = new JLabel(username);
        usernameLabel.setFont(new Font("Arial",Font.BOLD,28));
        usernameLabel.setForeground(Color.WHITE);
        usernameLabel.setBounds(350,170,150,50);
        panel.add(usernameLabel);

        // Highscore

        JLabel highscoreLabel = new JLabel("HIGH SCORE");
        highscoreLabel.setFont(new Font("Arial",Font.PLAIN,35));
        highscoreLabel.setBounds(50,270,250,50);
        highscoreLabel.setForeground(Color.RED);
        panel.add(highscoreLabel);

        // game iocn

        JLabel flappyBirdLabel = new JLabel(new ImageIcon(flappyBirdImage));
        flappyBirdLabel.setBounds(70,350,60,60);
        panel.add(flappyBirdLabel);

        JLabel trexLabel = new JLabel(new ImageIcon(trexImage));
        trexLabel.setBounds(70,440,60,60);
        panel.add(trexLabel);

        JLabel BrickBreakerLabel = new JLabel(new ImageIcon(brickbreakerImage));
        BrickBreakerLabel.setBounds(70,530,60,60);
        panel.add(BrickBreakerLabel);

        JLabel snakeLabel = new JLabel(new ImageIcon(snakeImage));
        snakeLabel.setBounds(70,620,60,60);
        panel.add(snakeLabel);


        // Highscoe

        JLabel flappyBirdHighscoreLabel = new JLabel(flappyBird_highscore+"");
        flappyBirdHighscoreLabel.setBounds(190,350,60,60);
        flappyBirdHighscoreLabel.setForeground(Color.GREEN);
        flappyBirdHighscoreLabel.setFont(new Font("Arial",Font.BOLD,22));
        panel.add(flappyBirdHighscoreLabel);

        JLabel trexHighscoreLabel = new JLabel(trex_highscore+"");
        trexHighscoreLabel.setBounds(190,440,60,60);
        trexHighscoreLabel.setForeground(Color.GREEN);
        trexHighscoreLabel.setFont(new Font("Arial",Font.BOLD,22));
        panel.add(trexHighscoreLabel);

        JLabel BrickBreakerHighscoreLabel = new JLabel(brickBreaker_highscore+"");
        BrickBreakerHighscoreLabel.setBounds(190,530,60,60);
        BrickBreakerHighscoreLabel.setForeground(Color.GREEN);
        BrickBreakerHighscoreLabel.setFont(new Font("Arial",Font.BOLD,22));
        panel.add(BrickBreakerHighscoreLabel);

        JLabel snakeHighscoreLabel = new JLabel(snake_highscore+"");
        snakeHighscoreLabel.setBounds(190,620,60,60);
        snakeHighscoreLabel.setForeground(Color.GREEN);
        snakeHighscoreLabel.setFont(new Font("Arial",Font.BOLD,22));
        panel.add(snakeHighscoreLabel);

        // Time Spent

        JLabel TimeSpentLabel = new JLabel("TIME SPENT");
        TimeSpentLabel.setFont(new Font("Arial",Font.PLAIN,35));
        TimeSpentLabel.setBounds(530,270,250,50);
        TimeSpentLabel.setForeground(Color.RED);
        panel.add(TimeSpentLabel);


    // Time Spent table    

        JLabel gameLabel = new JLabel("Game");
        gameLabel.setBounds(520,340,150,50);
        gameLabel.setFont(new Font("Arial",Font.PLAIN,24));
        gameLabel.setForeground(Color.ORANGE);
        panel.add(gameLabel);


        JLabel timespentLabel = new JLabel("Time Spent");
        timespentLabel.setBounds(660,340,150,50);
        timespentLabel.setFont(new Font("Arial",Font.PLAIN,24));
        timespentLabel.setForeground(Color.ORANGE);
        panel.add(timespentLabel);

        // Game label

        JLabel flappyLabel = new JLabel("Flappy Bird");
        flappyLabel.setBounds(520,390,150,30);
        flappyLabel.setFont(new Font("Arial",Font.PLAIN,20));
        flappyLabel.setForeground(Color.CYAN);
        panel.add(flappyLabel);


        JLabel TrexLabel = new JLabel("Trex Game");
        TrexLabel.setBounds(520,440,150,30);
        TrexLabel.setFont(new Font("Arial",Font.PLAIN,20));
        TrexLabel.setForeground(Color.CYAN);
        panel.add(TrexLabel);

        JLabel brickBreakerLabel = new JLabel("Brick Breaker");
        brickBreakerLabel.setBounds(520,490,150,30);
        brickBreakerLabel.setFont(new Font("Arial",Font.PLAIN,20));
        brickBreakerLabel.setForeground(Color.CYAN);
        panel.add(brickBreakerLabel);
        
        JLabel snakeGameLabel = new JLabel("Snake Game");
        snakeGameLabel.setBounds(520,540,150,30);
        snakeGameLabel.setFont(new Font("Arial",Font.PLAIN,20));
        snakeGameLabel.setForeground(Color.CYAN);
        panel.add(snakeGameLabel);
        
        JLabel tiktaktoeLabel = new JLabel("Tik-Tak-Toe");
        tiktaktoeLabel.setBounds(520,590,150,30);
        tiktaktoeLabel.setFont(new Font("Arial",Font.PLAIN,20));
        tiktaktoeLabel.setForeground(Color.CYAN);
        panel.add(tiktaktoeLabel);
        
        JLabel pongLabel = new JLabel("Ping-Pong");
        pongLabel.setBounds(520,640,150,30);
        pongLabel.setFont(new Font("Arial",Font.PLAIN,20));
        pongLabel.setForeground(Color.CYAN);
        panel.add(pongLabel);

        // Time label

        JLabel flappytimespentLabel = new JLabel(timeConvertor(flappyBird_time));
        flappytimespentLabel.setBounds(680,390,150,30);
        flappytimespentLabel.setFont(new Font("Arial",Font.PLAIN,20));
        flappytimespentLabel.setForeground(Color.GREEN);
        panel.add(flappytimespentLabel);

        JLabel TrextimespentLabel = new JLabel(timeConvertor(trex_time));
        TrextimespentLabel.setBounds(680,440,150,30);
        TrextimespentLabel.setFont(new Font("Arial",Font.PLAIN,20));
        TrextimespentLabel.setForeground(Color.GREEN);
        panel.add(TrextimespentLabel);
        
        JLabel brickBreakertimespentLabel = new JLabel(timeConvertor(brickBreaker_time));
        brickBreakertimespentLabel.setBounds(680,490,150,30);
        brickBreakertimespentLabel.setFont(new Font("Arial",Font.PLAIN,20));
        brickBreakertimespentLabel.setForeground(Color.GREEN);
        panel.add(brickBreakertimespentLabel);

        JLabel snakeGametimespentLabel = new JLabel(timeConvertor(snake_time));
        snakeGametimespentLabel.setBounds(680,540,150,30);
        snakeGametimespentLabel.setFont(new Font("Arial",Font.PLAIN,20));
        snakeGametimespentLabel.setForeground(Color.GREEN);
        panel.add(snakeGametimespentLabel);
        
        JLabel tiktaktoetimespentLabel = new JLabel(timeConvertor(tiktaktoe_time));
        tiktaktoetimespentLabel.setBounds(680,590,150,30);
        tiktaktoetimespentLabel.setFont(new Font("Arial",Font.PLAIN,20));
        tiktaktoetimespentLabel.setForeground(Color.GREEN);
        panel.add(tiktaktoetimespentLabel);


        JLabel pongtimeLabel = new JLabel(timeConvertor(pong_time));
        pongtimeLabel.setBounds(680,640,150,30);
        pongtimeLabel.setFont(new Font("Arial",Font.PLAIN,20));
        pongtimeLabel.setForeground(Color.GREEN);
        panel.add(pongtimeLabel);



        repaint();
        setVisible(true);
    }

    public void getHighscore(Connection con,String username){
        String query = "SELECT trex_highscore,flappyBird_highscore,brickBreaker_highscore,snake_highscore FROM gamedata WHERE username = ?";
        try{
            PreparedStatement stat = con.prepareStatement(query);
            stat.setString(1,username);
            ResultSet rs = stat.executeQuery();
            if(rs.next()){
                trex_highscore = rs.getInt("trex_highscore");
                flappyBird_highscore = rs.getInt("flappyBird_highscore");
                brickBreaker_highscore = rs.getInt("brickBreaker_highscore");
                snake_highscore = rs.getInt("snake_highscore");
            }
        }
        catch(SQLException e){}
    }

    public void getTime(Connection con,String username){
        String query = "SELECT trex_time,flappyBird_time,brickBreaker_time,snake_time,tiktaktoe_time,pong_time FROM gamedata WHERE username = ?";
        try{
            PreparedStatement stat = con.prepareStatement(query);
            stat.setString(1,username);
            ResultSet rs = stat.executeQuery();
            if(rs.next()){
                trex_time = rs.getInt("trex_time");
                flappyBird_time = rs.getInt("flappyBird_time");
                brickBreaker_time = rs.getInt("brickBreaker_time");
                snake_time = rs.getInt("snake_time");
                tiktaktoe_time = rs.getInt("tiktaktoe_time");
                pong_time = rs.getInt("pong_time");
            }
        }
        catch(SQLException e){}
    }

    public String timeConvertor(int seconds){
        Duration time = Duration.ofSeconds(seconds);
        String formatedTime = String.format("%02d : %02d : %02d",time.toHours(),time.toMinutes()%60,time.toSeconds()%60);
        return formatedTime;
    }

}
class Panel extends JPanel{
    Image  backgroundImage = new ImageIcon("GAME HUB/src/MyAccount/Userbackground.png").getImage();
    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        g.drawImage(backgroundImage,0,0,getWidth(),getHeight(),this);

        Graphics2D g2D = (Graphics2D)g;
        g2D.setStroke(new BasicStroke(5));
        g2D.setColor(Color.WHITE);
        g2D.drawRect(50,330,220,370);
        g2D.drawRect(500,330,300,370);
    }
}