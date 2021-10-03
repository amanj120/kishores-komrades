module com.example.kishoreskomrades {
    requires javafx.controls;
    requires javafx.fxml;
    requires junit;

    opens com.example.kishoreskomrades to javafx.fxml;
    exports com.example.kishoreskomrades;
    exports models;
}