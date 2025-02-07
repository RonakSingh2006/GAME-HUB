package login;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import gameFrame.Frame;



public class Login extends JFrame implements ActionListener {

    JTextField userfield;
    JPasswordField passwordfield;
    Connection conn;

    public Login(Connection con) {
        conn = con;

        setTitle("Game Hub");
        setIconImage(new ImageIcon("GAME HUB\\src\\login\\key.png").getImage());
        setSize(500, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

       
        BackgroundPanel backgroundPanel = new BackgroundPanel();
        backgroundPanel.setLayout(null);  

        // Username
        JLabel usernameLabel = new JLabel("Username");
        usernameLabel.setForeground(Color.WHITE);
        usernameLabel.setBounds(50, 200, 100, 30);
        usernameLabel.setFont(new Font("Arial",Font.PLAIN,18));
        backgroundPanel.add(usernameLabel);

        userfield = new JTextField();
        userfield.setBounds(150, 200, 200, 30);
        backgroundPanel.add(userfield);

        // Password
        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setForeground(Color.WHITE);
        passwordLabel.setBounds(50, 300, 100, 30);
        passwordLabel.setFont(new Font("Arial",Font.PLAIN,18));
        backgroundPanel.add(passwordLabel);

        passwordfield = new JPasswordField();
        passwordfield.setBounds(150, 300, 200, 30);
        backgroundPanel.add(passwordfield);

        // Login
        JButton login = new JButton("Login");
        login.setBounds(100, 400, 100, 30);
        login.addActionListener(this);
        backgroundPanel.add(login);

       // Register
        JButton register = new JButton("Register");
        register.setBounds(300, 400, 100, 30);
        register.addActionListener(this);
        backgroundPanel.add(register);

       
        setContentPane(backgroundPanel);
        setVisible(true);
    }

    class BackgroundPanel extends JPanel {
        Image background = new ImageIcon("GAME HUB\\src\\login\\background.png").getImage();
        Image gamehub = new ImageIcon("GAME HUB\\src\\login\\gameOver.png").getImage();

       
        protected void paintComponent(Graphics g) {

            super.paintComponent(g);
            g.drawImage(background, 0, 0, getWidth(), getHeight(), this);  
            g.drawImage(gamehub, 100, 50, this);

        }
    }


    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("Login")) {
            String query = "SELECT password FROM userdata WHERE username = ?";
            try {
                PreparedStatement ps = conn.prepareStatement(query);
                ps.setString(1, userfield.getText());
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {  // Check if the username exists
                    String pass = rs.getString("password");
                    if (pass.equals(new String(passwordfield.getPassword()))) {

                        new Frame(conn , userfield.getText());
                            
                        dispose();  // Close the window after successful login
                    } else {
                        JOptionPane.showMessageDialog(this, "Incorrect Password", "Login", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Username not found", "Login", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        } else if (e.getActionCommand().equals("Register")) {
            if (userfield.getText().isEmpty() || new String(passwordfield.getPassword()).isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter all fields", "Registration Failed", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String query = "INSERT INTO userdata (username, password) VALUES (?, ?)";
            String query2 = "INSERT INTO gamedata (username) values(?)";
            try {
                PreparedStatement ps = conn.prepareStatement(query);
                PreparedStatement ps2 = conn.prepareStatement(query2);

                ps.setString(1, userfield.getText());
                ps.setString(2, new String(passwordfield.getPassword()));
                ps2.setString(1,userfield.getText());

                int rowsAffected = ps.executeUpdate();
                int rowsAffected2 = ps2.executeUpdate();

                if (rowsAffected > 0 && rowsAffected2 > 0) {
                    JOptionPane.showMessageDialog(this, "Registration Successful!", "Registration", JOptionPane.INFORMATION_MESSAGE);
                    userfield.setText(null);
                    passwordfield.setText(null);
                } else {
                    JOptionPane.showMessageDialog(this, "Registration Failed", "Registration", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Registration Failed", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}