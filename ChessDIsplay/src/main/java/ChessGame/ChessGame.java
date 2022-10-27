package ChessGame;

import ChessEngine.Game;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import java.io.IOException;

public class ChessGame extends Application{


    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ChessBoard.class.getResource("chessGame.fxml"));
        Pane gameView = fxmlLoader.load();
        ChessBoard boardPane = new ChessBoard();
        boardPane.currGame = new Game();
        boardPane.resetBoardGfx();
        boardPane.currGame.generateMoves();
        ChessGameController.initData(boardPane);
        NewGameController.initData(boardPane);
        LoadGameController.initData(boardPane);
        SaveGameController.initData(boardPane);
        //LoadGameController.loadDB(-1);
        gameView.getChildren().add(boardPane);
        Scene scene = new Scene(gameView, 900, 600);
        stage.setTitle("Chess");
        stage.setScene(scene);
        stage.show();
    }
    public static void main(String[] args) {
        launch();
    }
}
