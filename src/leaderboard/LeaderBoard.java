package leaderboard;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;

import javax.swing.JFrame;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import gameFrame.Frame;

import javax.swing.JScrollPane;

import java.awt.Color;
import java.awt.Image;
import java.awt.Font;
import java.awt.Container;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;



public class LeaderBoard extends JFrame{
    Image icon = new ImageIcon("GAME HUB/src/leaderboard/podium.png").getImage();

    public LeaderBoard(Connection con,String user){

        addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent w){
                new Frame(con,user);
            }
        });

        setSize(1050,350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setTitle("LeaderBoard");
        setResizable(false);
        setIconImage(icon);

        Container c = this.getContentPane();
        c.setBackground(Color.LIGHT_GRAY);
        c.setLayout(null);

        JLabel flappyBirdLabel = new JLabel("Flappy Bird");
        flappyBirdLabel.setBounds(90,10,150,30);
        flappyBirdLabel.setFont(new Font("Arial",Font.PLAIN,24));
        c.add(flappyBirdLabel);

        JLabel trexLabel = new JLabel("Trex Game");
        trexLabel.setBounds(340,10,150,30);
        trexLabel.setFont(new Font("Arial",Font.PLAIN,24));
        c.add(trexLabel);

        JLabel snakeLabel = new JLabel("Snake Game");
        snakeLabel.setBounds(590,10,150,30);
        snakeLabel.setFont(new Font("Arial",Font.PLAIN,24));
        c.add(snakeLabel);

        JLabel brickBreakerabel = new JLabel("Brick Breaker");
        brickBreakerabel.setBounds(840,10,150,30);
        brickBreakerabel.setFont(new Font("Arial",Font.PLAIN,24));
        c.add(brickBreakerabel);

        String columns[] = {"Username","High Score"};

        DefaultTableModel flappyBirdModel = new DefaultTableModel(columns,0);
        JScrollPane flappyBirdTable = new JScrollPane(new JTable(flappyBirdModel));
        flappyBirdTable.setBounds(50,50,200,250);
        c.add(flappyBirdTable);

        DefaultTableModel trexModel = new DefaultTableModel(columns,0);
        JScrollPane trexTable = new JScrollPane(new JTable(trexModel));
        trexTable.setBounds(300,50,200,250);
        c.add(trexTable);

        DefaultTableModel snakeModel = new DefaultTableModel(columns,0);
        JScrollPane snakeTable = new JScrollPane(new JTable(snakeModel));
        snakeTable.setBounds(550,50,200,250);
        c.add(snakeTable);

        DefaultTableModel brickBreakerModel = new DefaultTableModel(columns,0);
        JScrollPane brickBreakerTable = new JScrollPane(new JTable(brickBreakerModel));
        brickBreakerTable.setBounds(800,50,200,250);
        c.add(brickBreakerTable);


        // getting data

        // Flappy Bird
        String query1 = "select username,flappyBird_highscore from gamedata order by flappyBird_highscore desc";
        try{
            PreparedStatement ps = con.prepareStatement(query1);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                String username = rs.getString("username");
                int highscore = rs.getInt("flappyBird_highscore");
                flappyBirdModel.addRow(new Object[]{username,highscore});
            }

        }catch(SQLException e){}


        // Trex Game
        String query2 = "select username,trex_highscore from gamedata order by trex_highscore desc";
        try{
            PreparedStatement ps = con.prepareStatement(query2);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                String username = rs.getString("username");
                int highscore = rs.getInt("trex_highscore");
                trexModel.addRow(new Object[]{username,highscore});
            }

        }catch(SQLException e){}

        // Snake Game
        String query3 = "select username,snake_highscore from gamedata order by snake_highscore desc";
        try{
            PreparedStatement ps = con.prepareStatement(query3);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                String username = rs.getString("username");
                int highscore = rs.getInt("snake_highscore");
                snakeModel.addRow(new Object[]{username,highscore});
            }

        }catch(SQLException e){}


        // Brick Breaker
        String query = "select username,brickBreaker_highscore from gamedata order by brickBreaker_highscore desc";
        try{
            PreparedStatement ps = con.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                String username = rs.getString("username");
                int highscore = rs.getInt("brickBreaker_highscore");
                brickBreakerModel.addRow(new Object[]{username,highscore});
            }

        }catch(SQLException e){}


        setVisible(true);
    }
}