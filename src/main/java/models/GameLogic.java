package models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.stream.Collectors;

public class GameLogic {

    private int numChanceTypes = 6; // Not including None

    private enum ChanceCard {
        NONE, GAIN_MONEY, LOSE_MONEY, MOVE_FORWARD, MOVE_BACK, SWAP_LOSER, SWAP_WINNER
    }

    private static class Pair {
        int row;
        int col;

        private Pair(int row, int col) {
            this.row = row;
            this.col = col;
        }
    }

    public static class Tile {
        public int row;
        public int col;
        public int money;
        public int move; // used only for chance cards
        public ChanceCard chance;
        public boolean isRedTile;

        public Tile(int row, int col, int money, ChanceCard isChance, boolean isRedTile) {
            this.row = row;
            this.col = col;
            this.money = money;
            this.move = 0;
            this.isChance = isChance;
            this.isRedTile = isRedTile;
        }
    }

    public static Tile getRandomChance(int row, int col, int maxMoney, Random random) {
        int chanceType = random.nextInt(numChanceTypes) + 1;
        ChanceCard card = (ChanceCard) chanceType;

        switch (card) {
            case GAIN_MONEY:
                int money = (random.nextInt(maxMoney) + 1);
                return new Tile(row, col, money, GAIN_MONEY, false);
            case LOSE_MONEY:
                int money = -1 * (random.nextInt(maxMoney) + 1);
                return new Tile(row, col, money, LOSE_MONEY, false);
            case MOVE_FORWARD:
                int move = (random.nextInt(6) + 1);
                Tile t = Tile(row, col, money, MOVE_FORWARD, false);
                t.move = move;
                return t;
            case MOVE_BACK:
                int move = -1 * (random.nextInt(6) + 1);
                Tile t = Tile(row, col, money, MOVE_BACK, false);
                t.move = move;
                return t;
            case SWAP_LOSER:
                return new Tile(row, col, 0, SWAP_LOSER, false);
            case SWAP_WINNER:
                return new Tile(row, col, 0, SWAP_WINNER, false);
            default:
                int money = (random.nextInt(maxMoney) + 1);
                return new Tile(row, col, money, GAIN_MONEY, false);
        }
    }

    public static Tile[][] setupTiles(int numRows, int numCols, int maxMoney, Random random) {
        int num_chance = (int)(numCols * numRows * 0.1);
        int num_reds = (int)(numCols * numRows * 0.45);

        Tile[][] tile_arr = new Tile[numRows][numCols];
        int num_tiles = numRows * numCols;

        ArrayList<Pair> shuffle = new ArrayList<>();
        for (int row = 0; row < numRows; row ++) {
            for (int col = 0; col < numCols; col++) {
                shuffle.add(new Pair(row, col));
            }
        }
        Collections.shuffle(shuffle);

        for (int i = 0; i < num_chance; i++) { // chance tiles
            Pair p = shuffle.get(i);
            tile_arr[p.row][p.col] = getRandomChance(p.row, p.col, maxMoney, random);
        }

        for (int i = num_chance; i < num_chance + num_reds; i++) { // red tiles
            int money = (random.nextInt(maxMoney) + 1);
            money = -1 * money;
            Pair p = shuffle.get(i);
            tile_arr[p.row][p.col] = new Tile(p.row, p.col, money, false, true);
        }

        for (int i = num_chance + num_reds; i < num_tiles; i++) { //green tiles
            int money = (random.nextInt(maxMoney) + 1);
            Pair p = shuffle.get(i);
            tile_arr[p.row][p.col] = new Tile(p.row, p.col, money, false, false);
        }
        // Finish Tile should not change money
        tile_arr[numRows - 1][numCols - 1] = new Tile(numRows - 1, numCols - 1, 0, false, false);
        return tile_arr;
    }

    public static ArrayList<Player> getPlayersAt(ArrayList<Player> players, int row, int col) {
        return players.stream().filter((Player p )-> p.getCurrentCol() == col && p.getCurrentRow() == row).collect(Collectors.toCollection(ArrayList::new));
    }

    public static int movePlayer(Player player, int roll, Tile[][] tiles) {
        int row = player.getCurrentRow();
        int col = player.getCurrentCol();

        int boardRows = tiles.length;
        int boardCols = tiles[0].length;

        col += roll;
        if (col >= boardCols) {
            row += 1;
            col = col % boardCols;
        }

        if (row >= boardRows) {
            row = boardRows - 1;
            col = boardCols - 1;
        }

        if (row == boardRows - 1 && col == boardCols - 1) {
            player.setDone();
        }

        player.setMoney(player.getMoney() + tiles[row][col].money);
        player.setCurrentRow(row);
        player.setCurrentCol(col);

        return tiles[row][col].money;
    }

    public static boolean isGameOver(ArrayList<Player> players) {
        return players.stream().map(Player::isDone).reduce(false, Boolean::logicalOr);
    }
}
