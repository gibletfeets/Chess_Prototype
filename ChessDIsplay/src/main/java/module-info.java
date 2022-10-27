module com.example.chessdisplay {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens ChessGame to javafx.fxml;
    exports ChessGame;
}