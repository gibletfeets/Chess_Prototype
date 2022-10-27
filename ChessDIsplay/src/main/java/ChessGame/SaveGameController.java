package ChessGame;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class SaveGameController {
    private static ArrayList<String> names;
    private static ChessBoard gameBoard;
    @FXML
    private TextField NameBox;
    @FXML
    private Label saveGameLabel;
    @FXML
    private void saveGame(){
        if(ChessGameController.getUserID() == -1){
            saveGameLabel.setText("Not logged in.");
            return;
        }
        String name = NameBox.getText();
        if(names.contains(name)){
            saveGameLabel.setText("Name already used.");
            return;
        }
        if((name.trim().length() == 0)){
            saveGameLabel.setText("Invalid game name.");
            return;
        }
        try {
            ChessDBConnection connect = new ChessDBConnection();
            Connection connectDB = connect.getConnection();
            String FEN = gameBoard.currGame.currentPosition.getFEN();
            String query = "INSERT INTO chessGames (gameName, FEN) VALUES (" + name + ", " + FEN + ")";
            Statement statement = connectDB.createStatement();
            statement.executeUpdate(query);
        } catch (Exception E){
            E.printStackTrace();
            E.getCause();
        }
    }
    public static void initData(ChessBoard in){
        gameBoard = in;
    }
    public static void loadDB(int userID) {
        ChessDBConnection connect = new ChessDBConnection();
        Connection connectDB = connect.getConnection();
        String query = "SELECT gameName FROM chessGames WHERE userID = " + userID;
        try {
            Statement statement = connectDB.createStatement();
            ResultSet queryResult = statement.executeQuery(query);
            names = new ArrayList<>();
            while(queryResult.next()) {
                String name = queryResult.getString(1);
                names.add(name);
            }
        } catch (Exception E){
            E.printStackTrace();
            E.getCause();
        }
    }
}
