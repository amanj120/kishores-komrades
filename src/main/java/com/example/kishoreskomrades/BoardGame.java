package com.example.kishoreskomrades;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;

public class BoardGame extends javafx.application.Application {

    Button plusOne;
    Button plusThree;
    int currentRow;
    int currentCol;
    GridPane gp;
    Stage stage;
    VBox vBox;

    @Override
    public void start(Stage stage) throws IOException {
        //VBox composed of GridPane and Buttons. GridPane is composed of Stackpanes. Stackpanes composed of rectangle and text
        stage.setTitle("Digital Board Game");
        stage.setMinHeight(600);
        stage.setMinWidth(520);
        vBox = new VBox();
        gp = new GridPane();
        gp.setMinWidth(500);
        gp.setMinHeight(500);
        gp.setPadding(new Insets(16));
        gp.setAlignment(Pos.CENTER);

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                StackPane sp = new StackPane();
                Rectangle rect = new Rectangle();
                rect.setWidth(100);
                rect.setHeight(100);
                Text t = new Text("");
                rect.setStyle("-fx-fill: white; -fx-stroke: black; -fx-stroke-width: 3;");
                sp.getChildren().addAll(rect, t);
                gp.add(sp, i, j);
            }
        }
        StackPane sp = new StackPane();
        Rectangle rect = new Rectangle();
        rect.setWidth(100);
        rect.setHeight(100);
        rect.setStyle("-fx-fill: white; -fx-stroke: black; -fx-stroke-width: 3;");
        Text t = new Text("Player");
        sp.getChildren().addAll(rect, t);
        gp.add(sp, 0, 4);
        vBox.getChildren().add(gp);
        currentCol = 0;
        currentRow = 4;

        plusOne = new Button("Plus One");
        plusOne.setMinHeight(50);
        plusOne.setMinWidth(100);
        plusOne.setOnAction(e -> moveForward(e, 1));
        plusThree = new Button("Plus Three");
        plusThree.setMinHeight(50);
        plusThree.setMinWidth(100);
        plusThree.setOnAction(e -> moveForward(e, 3));
        vBox.getChildren().add(plusOne);
        vBox.getChildren().add(plusThree);
        Scene scene = new Scene(vBox);
        stage.setScene(scene);
        stage.show();
        this.stage = stage;
    }

    private void moveForward(ActionEvent e, int numForward) {
        if (currentRow - numForward < 0) {
            Alert a = new Alert(Alert.AlertType.WARNING);
            a.setContentText("Player cannot move forward " + numForward + " tile(s)");
            a.show();
            return;
        }
        StackPane sp = new StackPane();
        Rectangle rect = new Rectangle();
        rect.setWidth(100);
        rect.setHeight(100);
        rect.setStyle("-fx-fill: white; -fx-stroke: black; -fx-stroke-width: 3;");
        Text t = new Text("");
        sp.getChildren().addAll(rect, t);
        gp.add(sp, currentCol, currentRow);
        currentRow-= numForward;
        StackPane newSP = new StackPane();
        Rectangle newRect = new Rectangle();
        newRect.setWidth(100);
        newRect.setHeight(100);
        newRect.setStyle("-fx-fill: white; -fx-stroke: black; -fx-stroke-width: 3;");
        Text newT = new Text("Player");
        newSP.getChildren().addAll(newRect, newT);
        gp.add(newSP, currentCol, currentRow);
    }

    public static void main(String[] args) {
        launch();
    }
}