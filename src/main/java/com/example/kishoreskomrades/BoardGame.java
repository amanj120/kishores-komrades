package com.example.kishoreskomrades;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import models.Player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class BoardGame extends javafx.application.Application {

    Label currPlayerName;
    Label currPlayerMoney;
    Button plusOne;
    Button plusThree;
    Button endTurn;
    GridPane gp;
    Stage stage;
    VBox vBox;
    ArrayList<Player> players;
    boolean isRed;
    int currPlayer = 0;

    @Override
    public void start(Stage stage) throws IOException {

        this.stage = stage;
        stage.setTitle("Digital Board Game");
        stage.setMinHeight(600);
        stage.setMinWidth(520);
        VBox startBox = new VBox();
         startBox.setAlignment(Pos.CENTER);
        Text text = new Text("Welcome!\n\n");
        text.setTextAlignment(TextAlignment.CENTER);
        text.setFont(new Font(30));
        startBox.getChildren().add(text);
        Button startButton = new Button("Start!");
        startButton.setOnAction(e -> moveToInitialConfiguration(e));
        startBox.getChildren().add(startButton);
        Scene scene = new Scene(startBox);
        stage.setScene(scene);
        stage.show();

    }

    private void moveToInitialConfiguration(ActionEvent e) {
        VBox initialConfigBox = new VBox();
        GridPane initialInputGrid = new GridPane();
        initialInputGrid.setMinHeight(600);
        initialInputGrid.setMinWidth(520);
        initialInputGrid.setAlignment(Pos.CENTER);
        initialInputGrid.setHgap(10);
        initialInputGrid.setVgap(10);
        initialInputGrid.setPadding(new Insets(25, 25, 25, 25));
        Text scenetitle = new Text("Configuration");
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        initialInputGrid.add(scenetitle, 0, 0, 2, 1);

        Label p1NameLabel = new Label("Player 1 Name:");
        initialInputGrid.add(p1NameLabel, 0, 1);
        TextField p1NameText = new TextField();
        initialInputGrid.add(p1NameText, 1, 1);

        Label p2NameLabel = new Label("Player 2 Name:");
        initialInputGrid.add(p2NameLabel, 0, 2);
        TextField p2NameText = new TextField();
        initialInputGrid.add(p2NameText, 1, 2);

        Label p3NameLabel = new Label("Player 3 Name:");
        initialInputGrid.add(p3NameLabel, 0, 3);
        TextField p3NameText = new TextField();
        initialInputGrid.add(p3NameText, 1, 3);

        Label p4NameLabel = new Label("Player 4 Name:");
        initialInputGrid.add(p4NameLabel, 0, 4);
        TextField p4NameText = new TextField();
        initialInputGrid.add(p4NameText, 1, 4);

        Label startMoneyLabel = new Label("Starting Money:");
        initialInputGrid.add(startMoneyLabel, 0, 5);
        ToggleGroup startMoneyTG = new ToggleGroup();
        RadioButton oneHundredRB = new RadioButton("100");
        oneHundredRB.setToggleGroup(startMoneyTG);
        oneHundredRB.setSelected(true);
        RadioButton twoHundredRB = new RadioButton("200");
        twoHundredRB.setToggleGroup(startMoneyTG);
        RadioButton threeHundredRB = new RadioButton("300");
        threeHundredRB.setToggleGroup(startMoneyTG);
        startMoneyTG.setUserData(startMoneyTG.getSelectedToggle().toString());
        VBox startMoneyVBox = new VBox();
        startMoneyVBox.getChildren().add(oneHundredRB);
        startMoneyVBox.getChildren().add(twoHundredRB);
        startMoneyVBox.getChildren().add(threeHundredRB);


        initialInputGrid.add(startMoneyVBox, 1, 5);

        Label playersColor = new Label("Players color:");
        initialInputGrid.add(playersColor, 0, 6);
        ToggleGroup playersColorTG = new ToggleGroup();
        RadioButton redRB = new RadioButton("Red");
        redRB.setToggleGroup(playersColorTG);
        redRB.setSelected(true);
        RadioButton blueRB = new RadioButton("Blue");
        blueRB.setToggleGroup(playersColorTG);
        VBox playersColorVBox = new VBox();
        playersColorVBox.getChildren().add(redRB);
        playersColorVBox.getChildren().add(blueRB);
        initialInputGrid.add(playersColorVBox, 1, 6);

        Button btn = new Button("Sign in");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(btn);
        initialInputGrid.add(hbBtn, 1, 8);

        final Text actiontarget = new Text();
        initialInputGrid.add(actiontarget, 1, 10);

        btn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {

                players = new ArrayList<>();
                int numValidPlayers = 0;
                RadioButton startMoneyToggle = (RadioButton) startMoneyTG.getSelectedToggle();
                int startingMoney = Integer.parseInt(startMoneyToggle.getText());
                int startingRow = 4;
                int startingCol = 0;
                if (!p1NameText.getText().trim().isEmpty()) {
                    numValidPlayers++;
                    Player player1 = new Player(p1NameText.getText().trim(), startingMoney, startingRow, startingCol);
                    players.add(player1);
                }
                if (!p2NameText.getText().trim().isEmpty()) {
                    numValidPlayers++;
                    Player player2 = new Player(p2NameText.getText().trim(), startingMoney, startingRow, startingCol);
                    players.add(player2);
                }
                if (!p3NameText.getText().trim().isEmpty()) {
                    numValidPlayers++;
                    Player player3 = new Player(p3NameText.getText().trim(), startingMoney, startingRow, startingCol);
                    players.add(player3);
                }
                if (!p4NameText.getText().trim().isEmpty()) {
                    numValidPlayers++;
                    Player player4 = new Player(p4NameText.getText().trim(), startingMoney, startingRow, startingCol);
                    players.add(player4);
                }

                if (numValidPlayers < 2) {
                    actiontarget.setFill(Color.FIREBRICK);
                    actiontarget.setText("Please input at least two valid names");
                } else {
                    Collections.shuffle(players);
                    for (int i = 0; i < players.size(); i++) {
                        players.get(i).setCurrentCol(i);
                    }
                    RadioButton colorToggle = (RadioButton) playersColorTG.getSelectedToggle();
                    isRed = colorToggle.getText().equals("Red") ? true : false;
                    moveToStartGame(e);
                }
            }
        });

        initialConfigBox.getChildren().add(initialInputGrid);

        stage.setScene(new Scene(initialConfigBox));
    }

    private void moveToStartGame(ActionEvent ae) {
        //VBox composed of GridPane and Buttons. GridPane is composed of Stackpanes. Stackpanes composed of rectangle and text
        this.vBox = new VBox();
        this.currPlayerName = new Label("Player Name: " + players.get(0).getName());
        this.currPlayerMoney = new Label("Player Money: " + players.get(0).getMoney());
        vBox.getChildren().add(currPlayerName);
        vBox.getChildren().add(currPlayerMoney);

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

        for (int i = 0; i < players.size(); i++) {
            drawPlayer(players.get(i));
        }
        vBox.getChildren().add(gp);

        plusOne = new Button("Plus One");
        plusOne.setMinHeight(50);
        plusOne.setMinWidth(100);
        plusOne.setOnAction(e -> moveForward(e, 1));
        plusThree = new Button("Plus Three");
        plusThree.setMinHeight(50);
        plusThree.setMinWidth(100);
        plusThree.setOnAction(e -> moveForward(e, 3));
        endTurn = new Button("End Turn");
        endTurn.setMinHeight(50);
        endTurn.setMinWidth(100);
        endTurn.setOnAction(e -> endTurn(e));

        vBox.getChildren().add(plusOne);
        vBox.getChildren().add(plusThree);
        vBox.getChildren().add(endTurn);


        Scene scene = new Scene(vBox);
        stage.setScene(scene);
        stage.show();
    }

    private void endTurn(ActionEvent e) {
        currPlayer = (currPlayer + 1) % players.size();
        this.currPlayerName .setText("Player name: " + players.get(currPlayer).getName());
        this.currPlayerMoney.setText("Player money: " + players.get(currPlayer).getMoney());
    }

    private void moveForward(ActionEvent e, int numForward) {
        Player currPlayer = players.get(this.currPlayer);
        if (currPlayer.getCurrentRow() - numForward < 0) {
            Alert a = new Alert(Alert.AlertType.WARNING);
            a.setContentText("Player cannot move forward " + numForward + " tile(s)");
            a.show();
            return;
        }
        clearPlayer(currPlayer);
        currPlayer.setCurrentRow(currPlayer.getCurrentRow()- numForward);
        drawPlayer(currPlayer);
    }

    private void drawPlayerRectangle(Player player, boolean isClear) {
        StackPane sp = new StackPane();
        Rectangle rect = new Rectangle();
        rect.setWidth(100);
        rect.setHeight(100);
        rect.setStyle("-fx-text-fill: green; -fx-fill: white; -fx-stroke: black; -fx-stroke-width: 3;");
        Text t = new Text();
        if (!isClear) {
            t.setText(player.getName());
            if (isRed) {
                t.setFill(Color.RED);
            } else {
                t.setFill(Color.BLUE);
            }
        }

        sp.getChildren().addAll(rect, t);
        gp.add(sp, player.getCurrentCol(), player.getCurrentRow());
    }

    private void clearPlayer(Player player) {
        drawPlayerRectangle(player, true);
    }

    private void drawPlayer(Player player) {
        drawPlayerRectangle(player, false);
    }

    public static void main(String[] args) {
        launch();
    }
}