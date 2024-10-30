package gameFrame;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import game.brickBreaker.BrickBreakerGame;
import game.flappyBird.FlappyBirdGame;
import game.pong.PongGame;
import game.snake.SnakeGame;
import game.tikTakToe.TikTakToeGame;
import game.trex.TrexGame;
import leaderboard.LeaderBoard;
import login.Login;
import myAccount.Account;

import java.sql.Connection;

public class Frame extends JFrame implements ActionListener{

    Image FlappyBirdLogo = new ImageIcon("GAME HUB/src/GameFrame/Images/FlappyBird.png").getImage();
    Image TrexLogo = new ImageIcon("GAME HUB/src/GameFrame/Images/Trex.png").getImage();
    Image BrickBreakerLogo = new ImageIcon("GAME HUB/src/GameFrame/Images/Brick Breaker.png").getImage();
    Image SnakeLogo = new ImageIcon("GAME HUB/src/GameFrame/Images/snake.png").getImage();
    Image TikTakToeLogo = new ImageIcon("GAME HUB/src/GameFrame/Images/tik tak toe.png").getImage();
    Image PongLogo = new ImageIcon("GAME HUB/src/GameFrame/Images/pong.png").getImage();
    Image userLogo = new ImageIcon("GAME HUB/src/GameFrame/Images/user.png").getImage();
    Image leaderBoardImage = new ImageIcon("GAME HUB/src/GameFrame/Images/leaderboard.png").getImage();

    Image SinglePlayer = new ImageIcon("GAME HUB/src/GameFrame/Images/1player.png").getImage();
    Image multiPlayer = new ImageIcon("GAME HUB/src/GameFrame/Images/2player.png").getImage();

    JButton flappyButton , TrexButton , BrickBreakerButton , SnakeButton , TikTakToeButton , PongButton ;
    JButton userButton , logoutButton;
    JButton LeaderBoard;
    JLabel player1 , player2;

    Connection conn;
    String user;

    public Frame(Connection con,String username){
        conn = con;
        user = username;
        setSize(1300,850);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setTitle("GAME HUB");
        setIconImage(new ImageIcon("GAME HUB/src/GameFrame/Images/logo.png").getImage());


        GamePanel panel = new GamePanel();
        panel.setLayout(null);


        // User

        userButton = new JButton();
        userButton.setSize(100,100);
        userButton.setIcon(new ImageIcon(userLogo));
        userButton.setLocation(20,10);
        userButton.setBackground(Color.GRAY);
        userButton.addActionListener(this);
        panel.add(userButton);

        // Logout

        logoutButton = new JButton("Logout");
        logoutButton.setSize(100,50);
        logoutButton.setLocation(1150,10);
        logoutButton.setBackground(Color.RED);
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFont(new Font("Arial",Font.BOLD,20));
        logoutButton.addActionListener(this);
        panel.add(logoutButton);

        
        // LeaderBoard

        LeaderBoard = new JButton();
        LeaderBoard.setBounds(20,150,60,60);
        LeaderBoard.setIcon(new ImageIcon(leaderBoardImage));
        LeaderBoard.addActionListener(this);
        panel.add(LeaderBoard);

        
        // Label

        player1 = new JLabel();
        player1.setIcon(new ImageIcon(SinglePlayer));
        player1.setBounds(25,250,150,150);
        panel.add(player1);
        
        JLabel label1 = new JLabel("Single Player");
        label1.setBounds(25,400,150,50);
        label1.setFont(new Font("Arial",Font.BOLD,22));
        label1.setForeground(Color.GREEN);
        panel.add(label1);
        
        // Game Play Button
        
        flappyButton = new JButton();
        flappyButton.setSize(280, 200);
        flappyButton.setIcon(new ImageIcon(FlappyBirdLogo));
        flappyButton.setLocation(210, 250);
        flappyButton.addActionListener(this);
        panel.add(flappyButton);
        
        BrickBreakerButton = new JButton();
        BrickBreakerButton.setSize(200, 200);
        BrickBreakerButton.setBackground(Color.BLACK);
        BrickBreakerButton.setIcon(new ImageIcon(BrickBreakerLogo));
        BrickBreakerButton.setLocation(510, 250);
        BrickBreakerButton.addActionListener(this);
        panel.add(BrickBreakerButton);
        
        TrexButton = new JButton();
        TrexButton.setSize(300, 200);
        TrexButton.setIcon(new ImageIcon(TrexLogo));
        TrexButton.setLocation(730, 250);
        TrexButton.addActionListener(this);
        panel.add(TrexButton);
        
        SnakeButton = new JButton();
        SnakeButton.setSize(200, 200);
        SnakeButton.setIcon(new ImageIcon(SnakeLogo));
        SnakeButton.setLocation(1050, 250);
        SnakeButton.addActionListener(this);
        panel.add(SnakeButton);
                


        // Label

        player2 = new JLabel();
        player2.setIcon(new ImageIcon(multiPlayer));
        player2.setBounds(25,530,150,150);
        panel.add(player2);

        JLabel label2 = new JLabel("Two Player");
        label2.setBounds(25,680,150,50);
        label2.setFont(new Font("Arial",Font.BOLD,22));
        label2.setForeground(Color.GREEN);
        panel.add(label2);

        // Game play Button
        
        TikTakToeButton = new JButton();
        TikTakToeButton.setSize(200, 200);
        TikTakToeButton.setIcon(new ImageIcon(TikTakToeLogo));
        TikTakToeButton.setLocation(350, 500);
        TikTakToeButton.addActionListener(this);
        panel.add(TikTakToeButton);

        
        PongButton = new JButton();
        PongButton.setSize(362, 200);
        PongButton.setIcon(new ImageIcon(PongLogo));
        PongButton.setLocation(750, 500);
        PongButton.addActionListener(this);
        panel.add(PongButton);



        panel.revalidate();
        panel.repaint();
        setContentPane(panel);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent e){
        if(e.getSource() == flappyButton){
            new FlappyBirdGame(conn,user);
            dispose();
        }
        if(e.getSource() == BrickBreakerButton){
            new BrickBreakerGame(conn,user);
            dispose();
        }
        if(e.getSource() == TrexButton){
            new TrexGame(conn,user);
            dispose();
        }
        if(e.getSource() == SnakeButton){
            new SnakeGame(conn,user);
            dispose();
        }
        if(e.getSource() == TikTakToeButton){
            new TikTakToeGame(conn,user);
            dispose();
        }
        if(e.getSource() == PongButton){
            new PongGame(conn,user);
            dispose();
        }
        if(e.getSource() == logoutButton){
            new Login(conn);
            dispose();
        }
        if(e.getSource()==userButton){
            new Account(conn,user);
            dispose();
        }
        if(e.getSource() == LeaderBoard){
            new LeaderBoard(conn, user);
            dispose();
        }

    }
}
class GamePanel extends JPanel{
    Image background = new ImageIcon("GAME HUB/src/GameFrame/Images/Background.png").getImage();
    Image gameHub = new ImageIcon("GAME HUB/src/GameFrame/Images/logo.png").getImage();

    protected void paintComponent(Graphics g){

        super.paintComponent(g);

        g.drawImage(background, 0, 0, getWidth(), getHeight(), this); 
        g.drawImage(gameHub,300,0,90,70,this);
        g.drawImage(gameHub,850,0,90,70,this);

        g.setColor(Color.CYAN);
        g.setFont(new  Font("Arial",Font.BOLD,60));
        g.drawString("GAME   HUB",450,60);

    }
}