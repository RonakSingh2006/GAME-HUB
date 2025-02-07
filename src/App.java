import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


import login.Login;

public class App{
    public static void main(String args[]){
        String url = "jdbc:mysql://127.0.0.1:3306/gamehub";
        String username = "root";
        String password = "Ronak@2006";
        Connection con;
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(url,username,password);

        }catch(ClassNotFoundException | SQLException e){
            System.out.println(e.getMessage());
            return;
        }
        new Login(con);
        
    }
}