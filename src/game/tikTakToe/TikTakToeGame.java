package game.tikTakToe;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import gameFrame.Frame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalTime;
import java.time.Duration;

public class TikTakToeGame extends JFrame implements ActionListener{

    JLabel title,scoreXLabel,scoreOLabel;
    JPanel headPanel,boardPanel,bottomPanel;

    String playerX = "X";
    String playerO = "O";
    String current = playerX;

    boolean gameOver = false;
    int turns = 0;

    JButton tile[][] = new JButton[3][3];
    JButton resetButton;

    int scoreX = 0;
    int scoreO = 0;

    public TikTakToeGame(Connection con,String username){  
        LocalTime start = LocalTime.now();  

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

        

        // Frame
        setSize(600,700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        setTitle("Tick-Tak-Toe");
        ImageIcon icon = new ImageIcon("GAME HUB/src/Game/TikTakToe/icon.jpeg");
        setIconImage(icon.getImage());

        setResizable(false);
        
        // Top Pannel
        title = new JLabel("X's Turn");
        title.setFont(new Font("Arial",Font.BOLD,50));
        title.setForeground(Color.BLACK);
        title.setHorizontalAlignment(SwingConstants.CENTER);

        headPanel = new JPanel();
        headPanel.setLayout(new BorderLayout());
        headPanel.add(title);
        headPanel.setBackground(Color.CYAN);
        add(headPanel,BorderLayout.NORTH);

        // Game Board
        boardPanel = new JPanel();
        boardPanel.setLayout(new GridLayout(3,3));
        
        for(int i=0 ; i<3 ; i++){
            for(int j = 0 ; j<3 ; j++){
                tile[i][j] = new JButton();
                tile[i][j].setBackground(Color.DARK_GRAY);
                tile[i][j].setForeground(Color.WHITE);
                tile[i][j].setFont(new Font("Arial",Font.BOLD,120));
                tile[i][j].addActionListener(this);
                boardPanel.add(tile[i][j]);
            }
        }

        add(boardPanel,BorderLayout.CENTER);


        // Bottom Panel
        resetButton = new JButton("Reset");
        resetButton.setBackground(Color.GREEN);
        resetButton.setForeground(Color.WHITE);
        resetButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                reset();
            }
        });

        resetButton.setFont(new Font("Arial",Font.BOLD,50));

        scoreXLabel = new JLabel("X : "+scoreX);
        scoreXLabel.setFont(new Font("Arial",Font.BOLD,50));
        scoreXLabel.setHorizontalAlignment(SwingConstants.CENTER);

        scoreOLabel = new JLabel("O : "+scoreO);
        scoreOLabel.setFont(new Font("Arial",Font.BOLD,50));
        scoreOLabel.setHorizontalAlignment(SwingConstants.CENTER);

        bottomPanel = new JPanel();
        bottomPanel.setBackground(Color.CYAN);
        bottomPanel.setLayout(new GridLayout(1,3));

        bottomPanel.add(scoreXLabel);
        bottomPanel.add(resetButton);
        bottomPanel.add(scoreOLabel);

        add(bottomPanel,BorderLayout.SOUTH);

        setVisible(true);
    }

    public void updateTime(Connection con , String username ,int seconds){
        String query = "update gamedata set tiktaktoe_time = ? where username = ?";
        try{
            PreparedStatement ps  = con.prepareStatement(query);
            ps.setInt(1,seconds);
            ps.setString(2,username);
            ps.executeUpdate();
        }catch(SQLException e){};
    }

    public int getTime(Connection con , String username){
        String query = "select tiktaktoe_time from gamedata where username = ?";
        try{
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getInt("tiktaktoe_time");
        }
        catch(SQLException e){}
        return 0;
    }

    public void actionPerformed(ActionEvent e){
        if(gameOver) return;

        // Game Play
        for(int i=0 ; i<3 ; i++){
            for(int j=0 ; j<3 ; j++){
                if(e.getSource()==tile[i][j] && tile[i][j].getText().equals("")){
                    tile[i][j].setText(current);
                    turns++;
                    checkWinner();
                    if(gameOver) {
                        title.setText(current+" Wins");
                        if(current.equals("X")){
                            scoreX++;
                            scoreXLabel.setText("X : "+scoreX);
                        }
                        else{
                            scoreO++;
                            scoreOLabel.setText("O : "+scoreO);
                        } 
                        return;
                    }
                    if(turns == 9) {
                        Tie();
                        gameOver = true;
                        return;
                    }
                    current = (current == playerX) ? playerO : playerX;
                    title.setText(current+"'s Turn");
                }
                
            }
        }
        
    }
    
    public void checkWinner(){
        
        // Game Over

        // Horizontal
        for(int i=0 ; i<3 ; i++){
            if(tile[i][0].getText().equals("")) continue;
            if(tile[i][0].getText().equals(tile[i][1].getText()) && tile[i][0].getText().equals(tile[i][2].getText())){
                for(int j=0 ; j<3 ; j++){
                    tile[i][j].setForeground(Color.GREEN);
                    tile[i][j].setBackground(Color.LIGHT_GRAY);
                }
                gameOver = true;
            }
        }

        //Vertical
        for(int i=0 ; i<3 ; i++){
            if(tile[0][i].getText().equals("")) continue;
            if(tile[0][i].getText().equals(tile[1][i].getText()) && tile[0][i].getText().equals(tile[2][i].getText())){
                for(int j=0 ; j<3 ; j++){
                    tile[j][i].setBackground(Color.LIGHT_GRAY);
                    tile[j][i].setForeground(Color.GREEN);
                }
                gameOver = true;
            }
        }

        //Diagonal
        if( !tile[1][1].getText().equals("") && (tile[0][0].getText().equals(tile[1][1].getText()) && tile[0][0].getText().equals(tile[2][2].getText()))){
            for(int i=0 ; i<3 ; i++){
                tile[i][i].setForeground(Color.GREEN);
                tile[i][i].setBackground(Color.LIGHT_GRAY);
            }
            gameOver = true;
        }
        if( !tile[1][1].getText().equals("") &&  (tile[0][2].getText().equals(tile[1][1].getText()) && tile[0][2].getText().equals(tile[2][0].getText()))){
                for(int i=0 ; i<3 ; i++){
                    tile[i][2-i].setForeground(Color.GREEN);
                    tile[i][2-i].setBackground(Color.LIGHT_GRAY);
                }
                gameOver = true;
        }
        
    }

    // Tie
    public void Tie(){
        for(int i=0 ; i<3 ; i++){
            for(int j=0 ; j<3 ; j++){
                tile[i][j].setForeground(Color.ORANGE);
                tile[i][j].setBackground(Color.LIGHT_GRAY);
                title.setText("Tie!");
            }
        }
    }

    // reset
    public void reset(){
        gameOver = false;
        turns = 0;
        current = playerX;
        for(int i=0 ; i<3 ; i++){
            for(int j=0 ; j<3 ; j++){
                tile[i][j].setText("");
                tile[i][j].setForeground(Color.WHITE);
                tile[i][j].setBackground(Color.DARK_GRAY);
            }
        }
    }
}