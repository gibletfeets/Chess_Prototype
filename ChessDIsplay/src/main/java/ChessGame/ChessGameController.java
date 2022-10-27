package ChessGame;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class ChessGameController {
    private static int userID = -1;
    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
    private static String getHash(String S){
        String str = "";
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            str = bytesToHex(digest.digest(S.getBytes(StandardCharsets.UTF_8)));

        } catch (Exception E){
            System.out.println("Failed to hash");
            E.printStackTrace();
            E.getCause();
        }
        return str;
    }
    @FXML
    private Button registerButton;
    @FXML
    private Button registerUserButton;
    @FXML
    private Button attemptLogin;
    @FXML
    private TextField userBox;
    @FXML
    private TextField passwordBox;
    @FXML
    private TextField confirmpasswordBox;
    @FXML
    private Label registerLabel;
    @FXML
    private Label loginLabel;
    @FXML
    private void registerUser(){
        String userName = userBox.getText();
        System.out.println(gameBoard.currGame.currentPosition.getFEN());
        String pw1 = passwordBox.getText();
        String pw2 = confirmpasswordBox.getText();
        if(userName.contains("\n") | userName.contains("\t") | userName.contains(" ")){
            registerLabel.setText("Invalid username.");
            return;
        }
        if((!pw1.equals(pw2))){
            registerLabel.setText("Passwords do not match.");
            return;
        }
        ChessDBConnection connect = new ChessDBConnection();
        Connection connectDB = connect.getConnection();
        String hash = getHash(pw1);
        String queryIn = "INSERT INTO users (userName, userHash) VALUES ('" + userName + "', '" + hash + "')";
        String queryUser = "SELECT count(1) FROM users WHERE userName = '" + userBox.getText() + "'";
        try {

            Statement statement = connectDB.createStatement();
            ResultSet queryResult = statement.executeQuery(queryUser);
            while(queryResult.next()){
                if(queryResult.getInt(1)==1){
                    registerLabel.setText("Username already taken.");
                } else {
                    statement.executeUpdate(queryIn);
                    Stage stage = (Stage) registerLabel.getScene().getWindow();
                    stage.close();
                    return;
                }
            }
        } catch (Exception E){
            E.printStackTrace();
            E.getCause();
        }
    }
    @FXML
    private void validateLogin(){
        ChessDBConnection connectNow = new ChessDBConnection();
        Connection connectDB = connectNow.getConnection();
        String query = "SELECT count(1) FROM users WHERE userName = '" + userBox.getText() + "' AND userHash = '"+getHash(passwordBox.getText())+"'";
        try {
            Statement statement = connectDB.createStatement();
            ResultSet queryResult = statement.executeQuery(query);
            while(queryResult.next()){
                if(queryResult.getInt(1)==1){
                    query = "SELECT userID FROM users WHERE userName = '" + userBox.getText() + "'";
                    queryResult = statement.executeQuery(query);
                    while(queryResult.next()) {
                        userID = queryResult.getInt(1);
                        LoadGameController.loadDB(userID);
                        SaveGameController.loadDB(userID);
                        Stage stage = (Stage) userBox.getScene().getWindow();
                        stage.close();
                        return;
                    }
                } else {
                    loginLabel.setText("Invalid login.");
                }
            }
        } catch (Exception E){
            E.printStackTrace();
            E.getCause();
        }
    }
    private Stage newWindowStage;
    @FXML
    private static ChessBoard gameBoard;

    public ChessGameController() throws NoSuchAlgorithmException {
    }

    @FXML
    private void flipBoard(){
        gameBoard.flipBoard();
    }
    @FXML
    private void undoMove(){
        gameBoard.currGame.unmakeMove();
        gameBoard.resetBoardGfx();
    }
    @FXML
    private void saveGame(){
        try {
            if(newWindowStage == null || !newWindowStage.isShowing()) {
                Parent root = FXMLLoader.load(getClass().getResource("saveGame.fxml"));
                Stage newGameStage = new Stage();
                newWindowStage = newGameStage;
                newGameStage.setScene(new Scene(root, 400, 235));
                newGameStage.setTitle("Save Game");
                newGameStage.show();
            }
        } catch (Exception E) {
            E.printStackTrace();
            E.getCause();

        }
    }
    @FXML
    private void newGame(){
            try {
                if(newWindowStage == null || !newWindowStage.isShowing()) {
                    Parent root = FXMLLoader.load(getClass().getResource("newGame.fxml"));
                    Stage newGameStage = new Stage();
                    newWindowStage = newGameStage;
                    newGameStage.setScene(new Scene(root, 400, 235));
                    newGameStage.setTitle("New Game");
                    newGameStage.show();
                }
            } catch (Exception E) {
                E.printStackTrace();
                E.getCause();
            }
    }
    @FXML
    private void loadGame(){
        try {
            if(newWindowStage == null || !newWindowStage.isShowing()) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("loadGame.fxml"));
                Parent root = loader.load();
                Stage newGameStage = new Stage();
                LoadGameController L = loader.getController();
                L.setLoadItems();
                newWindowStage = newGameStage;
                newGameStage.setScene(new Scene(root, 310, 150));
                newGameStage.setTitle("Load Game");
                newGameStage.show();
            }
        } catch (Exception E) {
            E.printStackTrace();
            E.getCause();
        }
    }
    @FXML
    private Button loginMenuButton;
    @FXML
    private void login(){
        try {
            if(newWindowStage == null || !newWindowStage.isShowing()) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("userLogin.fxml"));
                Parent root = loader.load();
                Stage newGameStage = new Stage();
                newWindowStage = newGameStage;
                newGameStage.setScene(new Scene(root, 400, 312));
                newGameStage.setTitle("Login");
                newGameStage.show();
            }
        } catch (Exception E) {
            E.printStackTrace();
            E.getCause();
        }
    }
    @FXML
    private void register(){
        try {
            if(newWindowStage == null || !newWindowStage.isShowing()) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("userRegister.fxml"));
                Parent root = loader.load();
                Stage newGameStage = new Stage();
                newWindowStage = newGameStage;
                newGameStage.setScene(new Scene(root, 400, 340));
                newGameStage.setTitle("Register");
                newGameStage.show();
            }
        } catch (Exception E) {
            E.printStackTrace();
            E.getCause();
        }
    }
    public static void initData(ChessBoard in){
        gameBoard = in;
    }
    public static int getUserID(){
        return userID;
    }
}
