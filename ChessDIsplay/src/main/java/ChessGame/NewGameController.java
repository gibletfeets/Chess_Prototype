package ChessGame;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NewGameController{
    private static ChessBoard gameBoard;
    public static void initData(ChessBoard in){
        gameBoard = in;
    }
    @FXML
    private TextField FENBox;
    @FXML
    private Label newGameLabel;
    @FXML
    private void initNewGame(){
        String FEN = FENBox.getText();
        if(FEN.trim().length() == 0) {
            gameBoard.currGame = new ChessEngine.Game();
            gameBoard.resetBoardGfx();
            Stage stage = (Stage) FENBox.getScene().getWindow();
            stage.close();
        } else {
            //found this horrible monster regex on regex101, had to fix it because it didn't try to capture castling rights flags more than once
            Pattern p = Pattern.compile("((([prnbqkPRNBQK12345678]*/){7})([prnbqkPRNBQK12345678]*)) (w|b) ((K?Q?k?q?)|\\-) (([abcdefgh][36])|\\-) ?(\\d*)? ?(\\d*)?");
            Matcher m = p.matcher(FEN);
            if (m.find()) {
                gameBoard.currGame = new ChessEngine.Game(FEN);
                gameBoard.currGame.generateMoves();
                gameBoard.resetBoardGfx();
                Stage stage = (Stage) FENBox.getScene().getWindow();
                stage.close();
            } else {
                newGameLabel.setText("Invalid FEN");
            }
        }
    }
}
