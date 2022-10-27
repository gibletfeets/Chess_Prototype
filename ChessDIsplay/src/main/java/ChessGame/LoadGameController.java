package ChessGame;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

public class LoadGameController {
    private static HashMap<String,String> name2FEN;
    private static ArrayList<String> names;
    private static ChessBoard gameBoard;
    @FXML
    ComboBox comboBox;
    @FXML
    Button loadButton;
    public static void initData(ChessBoard in){
        gameBoard = in;
    }
    public static void loadDB(int userID) {
        ChessDBConnection connect = new ChessDBConnection();
        Connection connectDB = connect.getConnection();
        String query = "SELECT gameName, FEN FROM chessgames WHERE userID = '" + userID + "'";
        try {
            Statement statement = connectDB.createStatement();
            ResultSet queryResult = statement.executeQuery(query);
            name2FEN = new HashMap<>();
            names = new ArrayList<>();
            while(queryResult.next()) {
                String name = queryResult.getString(1);
                String FEN = queryResult.getString(2);
                name2FEN.put(name, FEN);
                names.add(name);
            }
        } catch (Exception E){
            E.printStackTrace();
            E.getCause();
        }
    }
    public void setLoadItems(){
        if(names.size() != 0) {
            comboBox.getItems().addAll(names);
        }
    }
    public void clearItems(){
        comboBox.getItems().clear();
    }
    @FXML
    private void loadGame(){
        String FEN = name2FEN.get(comboBox.getValue());
        System.out.println(FEN);
        gameBoard.currGame = new ChessEngine.Game(FEN);
        gameBoard.currGame.generateMoves();
        gameBoard.resetBoardGfx();
    }
}
