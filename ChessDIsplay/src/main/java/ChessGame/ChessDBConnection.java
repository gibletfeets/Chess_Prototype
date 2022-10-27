package ChessGame;
import java.sql.Connection;
import java.sql.DriverManager;

public class ChessDBConnection {
    public Connection DBLink;

    public Connection getConnection(){
        String dbName = "ChessDB";
        String dbUser = "ChessApp";
        String dbPW = "Rxa5#";
        String url = "jdbc:mysql://localhost/" + dbName;
        //System.out.println("Connecting to DB");
        try {
            //Class.forName("com.sql.cj.jdbc.Driver");
            //DBLink = DriverManager.getConnection(url,dbUser,dbPW);
        } catch (Exception E){
            E.printStackTrace();
            E.getCause();
        }
        return DBLink;
    }
}
