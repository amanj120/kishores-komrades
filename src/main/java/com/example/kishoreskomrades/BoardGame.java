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
import java.util.Random;
import java.util.stream.Collectors;

public class BoardGame extends javafx.application.Application {

    private class Pair {
        int row;
        int col;

        private Pair(int row, int col) {
            this.row = row;
            this.col = col;
        }
    }

    private class Tile {
        int row;
        int col;
        int money;
        boolean isChance;
        boolean isRedTile;

        private Tile(int row, int col, int money, boolean isChance, boolean isRedTile) {
            this.row = row;
            this.col = col;
            this.money = money;
            this.isChance = isChance;
            this.isRedTile = isRedTile;
        }
    }

    Label gameInfo;
    GridPane gp;
    Stage stage;
    ArrayList<Player> players;

    static final int COLS = 12;
    static final int ROWS = 6;
    static final int MAX_MONEY = 25;
    static final int num_chance = (int)(COLS * ROWS * 0.1);
    static final int num_reds = (int)(COLS * ROWS * 0.45);

    boolean isRed;
    int currPlayer = 0;

    Tile[][] tiles;
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

        btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
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
                    isRed = colorToggle.getText().equals("Red") ? true : false;
                    Collections.shuffle(players);
                    showMainGameScreen(e);
                }
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

    private void setupBoard() {
        this.gp = new GridPane();
        this.gameInfo = new Label();

        gp.setMinWidth(500);
        gp.setMinHeight(500);
        gp.setPadding(new Insets(16));
        gp.setAlignment(Pos.CENTER);

        int num_tiles = ROWS * COLS;

        tiles = new Tile[ROWS][COLS];

        ArrayList<Pair> shuffle = new ArrayList<>();
        for (int row = 0; row < ROWS; row ++) {
            for (int col = 0; col < COLS; col++) {
                shuffle.add(new Pair(row, col));
            }
        }
        Collections.shuffle(shuffle);

        for (int i = 0; i < num_chance; i++) { // chance tiles
            int money = (game_rng.nextInt(MAX_MONEY) + 1);
            if (game_rng.nextInt(2) == 0) {
                money = -1 * money;
            }
            Pair p = shuffle.get(i);
            tiles[p.row][p.col] = new Tile(p.row, p.col, money, true, false);
        }

        for (int i = num_chance; i < num_chance + num_reds; i++) { // red tiles
            int money = (game_rng.nextInt(MAX_MONEY) + 1);
            money = -1 * money;
            Pair p = shuffle.get(i);
            tiles[p.row][p.col] = new Tile(p.row, p.col, money, false, true);
        }

        for (int i = num_chance + num_reds; i < num_tiles; i++) { //green tiles
            int money = (game_rng.nextInt(MAX_MONEY) + 1);
            Pair p = shuffle.get(i);
            tiles[p.row][p.col] = new Tile(p.row, p.col, money, false, false);
        }

        tiles[ROWS - 1][COLS - 1] = new Tile(ROWS - 1, COLS - 1, 0, false, false);
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
                    if (isRed) {
                        t.setFill(Color.RED);
                    } else {
                        t.setFill(Color.BLUE);
                    }
                }

                rect.setStyle("-fx-stroke: white; -fx-stroke-width: 1;");

                if (tiles[row][col].isChance) {
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
        return String.join("\n", get_players_at(row, col).stream().map(Player::getName).collect(Collectors.toList()));
    }

    private ArrayList<Player> get_players_at(int row, int col) {
        ArrayList<Player> ret = new ArrayList<>();
        for (Player p: players) {
            if (p.getCurrentRow() == row && p.getCurrentCol() == col) {
                ret.add(p);
            }
        }
        return ret;
    }

    private void endTurn(ActionEvent e) {
        currPlayer = (currPlayer + 1) % players.size();
        refreshBoard();
    }

    private void moveDiceRoll (ActionEvent e) {
        Player currPlayerObj = players.get(this.currPlayer);
        int row = currPlayerObj.getCurrentRow();
        int col = currPlayerObj.getCurrentCol();
        int roll = game_rng.nextInt(6) + 1;

        col += roll;
        if (col >= COLS) {
            row += 1;
            col = col % COLS;
        }
        if (row >= ROWS) {
            row = ROWS - 1;
            col = COLS - 1;
            currPlayerObj.setDone();
            Alert win = new Alert(Alert.AlertType.INFORMATION);
            win.setContentText("Player " + players.get(this.currPlayer).getName()+ " has finished the game");
            win.show();
        }

        if (currPlayerObj.isDone() == false) {
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setContentText("You rolled: " + roll + "\nyour money changed by: " + tiles[row][col].money);
            a.show();
            currPlayerObj.setMoney(currPlayerObj.getMoney() + tiles[row][col].money);
        }

        currPlayerObj.setCurrentRow(row);
        currPlayerObj.setCurrentCol(col);
        refreshBoard();
    }

    public static void main(String[] args) {
        launch();
    }
}