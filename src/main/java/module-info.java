module com.example.kishoreskomrades {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.example.kishoreskomrades to javafx.fxml;
    exports com.example.kishoreskomrades;
}