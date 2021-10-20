package com.example.kishoreskomrades;

import javafx.event.ActionEvent;
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
import models.GameLogic;
import models.Player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.stream.Collectors;

public class BoardGame extends javafx.application.Application {

    /* JavaFX Variables we need */
    Label gameInfo;
    GridPane gp;
    Stage stage;

    /* Game Parameters */
    static final int COLS = 11;
    static final int ROWS = 5;
    static final int MAX_MONEY = 25;
    boolean isTextRed;
    int currPlayer = 0;

    /* Game Logic Stuff */
    GameLogic.Tile[][] tiles;
    ArrayList<Player> players;
    Random game_rng;

    @Override
    public void start(Stage stage) throws IOException {
        this.game_rng = new Random(System.currentTimeMillis());
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
        startButton.setOnAction(e -> showInitialConfigScreen(e));
        startBox.getChildren().add(startButton);
        Scene scene = new Scene(startBox);
        stage.setScene(scene);
        stage.show();
    }

    private void showInitialConfigScreen(ActionEvent e) {
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

        ArrayList<TextField> pnames = new ArrayList<>();

        for (int i = 1; i <= 4; i ++) {
            Label pNameLabel = new Label(String.format("Player %d Name:", i));
            initialInputGrid.add(pNameLabel, 0, i);
            TextField pNameText = new TextField();
            initialInputGrid.add(pNameText, 1, i);
            pnames.add(pNameText);
        }

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

        Button btn = new Button("Start Game");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(btn);
        initialInputGrid.add(hbBtn, 1, 8);

        final Text actiontarget = new Text();
        initialInputGrid.add(actiontarget, 1, 10);

        btn.setOnAction((eventHandler) -> {
            players = new ArrayList<>();
            RadioButton startMoneyToggle = (RadioButton) startMoneyTG.getSelectedToggle();
            int startingMoney = Integer.parseInt(startMoneyToggle.getText());

            for (TextField tf : pnames) {
                String name = tf.getText().trim();
                if (name.length() > 0 && name.length() <= 8 && name.matches("^[a-zA-Z0-9]*$")) {
                    players.add(new Player(name, startingMoney, 0, 0));
                }
            }

            if (players.size() < 2) {
                actiontarget.setFill(Color.FIREBRICK);
                actiontarget.setText("Please input at least two valid names\n(alphanumeric strings between 1 and 8 characters)");
            } else {
                RadioButton colorToggle = (RadioButton) playersColorTG.getSelectedToggle();
                isTextRed = colorToggle.getText().equals("Red") ? true : false;
                Collections.shuffle(players);
                showMainGameScreen(null);
            }
        });

        initialConfigBox.getChildren().add(initialInputGrid);

        stage.setScene(new Scene(initialConfigBox));
    }

    private void showMainGameScreen(ActionEvent ae) {
        //VBox composed of GridPane and Buttons. GridPane is composed of Stackpanes. Stackpanes composed of rectangle and text
        setupBoard();
        refreshBoard();

        Button dice = new Button("Roll Dice");
        dice.setMinHeight(50);
        dice.setMinWidth(100);
        dice.setOnAction(e -> moveDiceRoll(e));

        Button endTurn = new Button("End Turn");
        endTurn.setMinHeight(50);
        endTurn.setMinWidth(100);
        endTurn.setOnAction(e -> endTurn(e));

        HBox buttonBox = new HBox();
        buttonBox.getChildren().add(gameInfo);
        buttonBox.getChildren().add(dice);
        buttonBox.getChildren().add(endTurn);
        buttonBox.setSpacing(16);
        buttonBox.setPadding(new Insets(16));
        buttonBox.setAlignment(Pos.CENTER_LEFT);

        VBox vBox = new VBox();
        vBox.getChildren().add(buttonBox);
        vBox.getChildren().add(gp);

        Scene scene = new Scene(vBox);
        this.stage.setScene(scene);
        this.stage.show();
    }

    private void showFinishScreen(ActionEvent ae) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(GameLogic.getGameOverString(players));
        alert.show();

        Button endTurn = new Button("Play Again");
        endTurn.setMinHeight(50);
        endTurn.setMinWidth(100);
        endTurn.setOnAction(e -> showInitialConfigScreen(e));

        VBox vBox = new VBox();
        vBox.getChildren().add(gp);
        vBox.getChildren().add(endTurn);
        vBox.setAlignment(Pos.CENTER);
        vBox.setPadding(new Insets(16));

        Scene scene = new Scene(vBox);
        this.stage.setScene(scene);
        this.stage.show();
    }

    private void setupBoard() {
        this.gp = new GridPane();
        this.gameInfo = new Label();

        gp.setMinWidth(500);
        gp.setMinHeight(500);
        gp.setPadding(new Insets(16));
        gp.setAlignment(Pos.CENTER);

        tiles = GameLogic.setupTiles(ROWS, COLS, MAX_MONEY, game_rng);
    }

    private void refreshBoard() {
        gp.getChildren().clear();

        for (int col = 0; col < COLS; col++) {
            for (int row = 0; row < ROWS; row++) {
                String player_string = get_players_at_string(row, col);
                StackPane sp = new StackPane();
                Rectangle rect = new Rectangle();
                rect.setWidth(100);
                rect.setHeight(100);
                Text t;
                if (col == COLS - 1 && row == ROWS - 1) {
                    t = new Text("Finish\n" + player_string);
                } else{
                    t = new Text(player_string);
                    if (isTextRed) {
                        t.setFill(Color.RED);
                    } else {
                        t.setFill(Color.BLUE);
                    }
                }

                rect.setStyle("-fx-stroke: white; -fx-stroke-width: 1;");

                if (tiles[row][col].chance != GameLogic.ChanceCard.NONE) {
                    rect.setFill(Color.LIGHTSKYBLUE);
                } else if (tiles[row][col].isRedTile) {
                    rect.setFill(Color.LIGHTPINK);
                } else {
                    rect.setFill(Color.LIGHTGREEN);
                }

                sp.getChildren().addAll(rect, t);
                gp.add(sp, col, row);
            }
        }

        gameInfo.setText(getGameInfoString());
    }

    private String getGameInfoString() {
        String ret = "Current Player: " + players.get(currPlayer).getName();
        for (Player p: players) {
            ret += "\nplayer: " + p.getName() + " money: " + p.getMoney();
            if (p.isDone()) {
                ret += " Finished!";
            }
        }
        return ret;
    }

    private String get_players_at_string(int row, int col) {
        return String.join("\n", GameLogic.getPlayersAt(players, row, col).stream().map(Player::getName).collect(Collectors.toList()));
    }

    private void endTurn(ActionEvent e) {
        currPlayer = (currPlayer + 1) % players.size();
        refreshBoard();
        if (GameLogic.isGameOver(players)) {
            showFinishScreen(null);
        }
    }

    private void moveDiceRoll (ActionEvent e) {
        int roll = game_rng.nextInt(6) + 1;

        String move_message = GameLogic.movePlayer(players, this.currPlayer, roll, tiles);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(move_message);
        alert.show();

        refreshBoard();
        if (GameLogic.isGameOver(players)) {
            showFinishScreen(null);
        }
    }

    public static void main(String[] args) {
        launch();
    }
}