package models;

import java.util.*;
import java.util.stream.Collectors;

public class GameLogic {

    public static int numChanceTypes = 5; // Not including None
    private static boolean paywallExists = true;
    private static final int paywallCost = 125;

    public enum Attribute {
        NONE, GAIN_MONEY, LOSE_MONEY, MOVE_FORWARD, MOVE_BACK, SWAP_RANDOM, PAYWALL
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
        public int move; // used only for attribute cards
        public Attribute attribute;
        public boolean isRedTile;

        public Tile(int row, int col, int money, Attribute attribute, boolean isRedTile) {
            this.row = row;
            this.col = col;
            this.money = money;
            this.move = 0;
            this.attribute = attribute;
            this.isRedTile = isRedTile;
        }
    }

    public static Tile getRandomChanceTile(int row, int col, int maxMoney, Random random) {
        int chanceType = random.nextInt(numChanceTypes) + 1;
        Attribute card = Attribute.values()[chanceType];
        int money = (random.nextInt(maxMoney) + 1);
        int move = (random.nextInt(6) + 1);
        Tile t;

        switch (card) {
            case LOSE_MONEY:
                return new Tile(row, col, -1 * money, Attribute.LOSE_MONEY, false);
            case MOVE_FORWARD:
                t = new Tile(row, col, 0, Attribute.MOVE_FORWARD, false);
                t.move = move;
                return t;
            case MOVE_BACK:
                t = new Tile(row, col, 0, Attribute.MOVE_BACK, false);
                t.move = -1 * move;
                return t;
            case SWAP_RANDOM:
                return new Tile(row, col, 0, Attribute.SWAP_RANDOM, false);
            default: // Gain Money
                return new Tile(row, col, money, Attribute.GAIN_MONEY, false);
        }
    }

    public static Tile[][] setupTiles(int numRows, int numCols, int maxMoney, Random random) {
        paywallExists = true;
        int num_chance = (int)(numCols * numRows * 0.25);
        int num_reds = (int)(numCols * numRows * 0.35);

        Tile[][] tile_arr = new Tile[numRows][numCols];
        int num_tiles = numRows * numCols;

        ArrayList<Pair> shuffle = new ArrayList<>();
        for (int row = 0; row < numRows; row ++) {
            for (int col = 0; col < numCols; col++) {
                shuffle.add(new Pair(row, col));
            }
        }
        Collections.shuffle(shuffle);

        for (int i = 0; i < num_chance; i++) { // attribute tiles
            Pair p = shuffle.get(i);
            tile_arr[p.row][p.col] = getRandomChanceTile(p.row, p.col, maxMoney, random);
        }

        for (int i = num_chance; i < num_chance + num_reds; i++) { // red tiles
            int money = (random.nextInt(maxMoney) + 1);
            money = -1 * money;
            Pair p = shuffle.get(i);
            tile_arr[p.row][p.col] = new Tile(p.row, p.col, money, Attribute.NONE, true);
        }

        for (int i = num_chance + num_reds; i < num_tiles; i++) { //green tiles
            int money = (random.nextInt(maxMoney) + 1);
            Pair p = shuffle.get(i);
            tile_arr[p.row][p.col] = new Tile(p.row, p.col, money, Attribute.NONE, false);
        }
        // Start and Finish Tile should not change money
        tile_arr[numRows - 1][numCols - 1] = new Tile(numRows - 1, numCols - 1, 0, Attribute.NONE, false);
        tile_arr[0][0] = new Tile(numRows - 1, numCols - 1, 0, Attribute.NONE, false);
        tile_arr[numRows / 2][numCols / 2] = new Tile(numRows / 2, numCols / 2, 0, Attribute.PAYWALL, false);
        return tile_arr;
    }

    public static ArrayList<Player> getPlayersAt(ArrayList<Player> players, int row, int col) {
        return players.stream().filter((Player p )-> p.getCurrentCol() == col && p.getCurrentRow() == row).collect(Collectors.toCollection(ArrayList::new));
    }

    private static Pair calculateNewPosition(int row, int col, int roll, int boardRows, int boardCols) {
        int idx = (row * boardCols + col) + roll;
        if (idx < 0) {
          return new Pair(0,0);
        } else if (idx >= boardRows * boardCols) {
            return new Pair(boardRows - 1, boardCols - 1);
        } else {
            return new Pair(idx / boardCols, idx % boardCols);
        }
    }

    public static int getRandomPlayer(int curPlayer, int numPlayers) {
        List<Integer> others = new ArrayList<>();
        for (int i = 0; i < numPlayers; i++) {
            if (i != curPlayer) {
                others.add(i);
            }
        }
        Collections.shuffle(others); // lmao so inefficient but who cares
        return others.get(0);
    }

//    public static int findLoser(List<Player> players) {
//        int lowestRow = Integer.MAX_VALUE;
//        int lowestCol = Integer.MAX_VALUE;
//        int idx = -1;
//
//        for (int i = 0 ; i < players.size(); i++) {
//            Player p = players.get(i);
//            if (p.getCurrentRow() <= lowestRow) {
//                if (p.getCurrentRow() < lowestRow || p.getCurrentCol() < lowestCol) {
//                    idx = i;
//                }
//            }
//        }
//        return idx;
//    }

    public static boolean passedPaywall(int newRow, int newCol,Tile[][] tiles) {
        int nrows = tiles.length;
        int ncols = tiles[0].length;
        if (newRow == (nrows/2) && newCol >= (ncols/2)) {
            return true;
        } else if (newRow > (nrows/2)) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean closeEnoughToPayPaywall(Player p, Tile[][] tiles) {
        int nrows = tiles.length;
        int ncols = tiles[0].length;
        int paywallIdx = ((nrows/2) * ncols) + (ncols/2);
        int playerIdx = p.getCurrentCol() + p.getCurrentRow()*ncols;
        return (paywallIdx - playerIdx <= 6 && paywallIdx - playerIdx >= 1);
    }

    public static String payPaywall(Player p, Tile[][] tiles) {
        if (!closeEnoughToPayPaywall(p, tiles)) {
            return String.format("Player %s is not close enough to the paywall to pay", p.getName());
        } else if (paywallExists && p.getMoney() >= 125) {
            p.setMoney(p.getMoney() - 125);
            paywallExists = false;
            return String.format("Player %s paid the paywall and it is now open for everyone", p.getName());
        } else if (paywallExists && p.getMoney() < 125) {
            return String.format("Player %s does not have enough money to pay the paywall", p.getName());
        } else {
            return "The paywall has already been paid";
        }
    }

    public static boolean getPaywallExists() {
        return paywallExists;
    }

    public static String movePlayer(List<Player> players, int curPlayer, int roll, Tile[][] tiles) {
        Player player = players.get(curPlayer);

        int oldRow = player.getCurrentRow();
        int oldCol = player.getCurrentCol();

        int boardRows = tiles.length;
        int boardCols = tiles[0].length;

        Pair newPos = calculateNewPosition(oldRow, oldCol, roll, boardRows, boardCols);
        int temp_row = newPos.row;
        int temp_col = newPos.col;

        if (paywallExists && passedPaywall(temp_row, temp_col, tiles)) {
            player.setCurrentRow(0);
            player.setCurrentCol(0);
            return String.format("%s rolled %d. This would put them past the paywall. Thus, they go back to the start. Should've payed up earlier", player.getName(),  roll);
        }

        if (temp_row == boardRows - 1 && temp_col == boardCols - 1) {
            player.setDone();
        }
        player.setCurrentRow(temp_row);
        player.setCurrentCol(temp_col);

        switch(tiles[temp_row][temp_col].attribute) {
            case MOVE_FORWARD:
            case MOVE_BACK:
                int newroll = tiles[temp_row][temp_col].move;
                newPos = calculateNewPosition(temp_row, temp_col, newroll, boardRows, boardCols);
                int row = newPos.row;
                int col = newPos.col;
                if (paywallExists && passedPaywall(temp_row, temp_col, tiles)) {
                    player.setCurrentRow(0);
                    player.setCurrentCol(0);
                    return String.format("%s rolled %d. By landing on a attribute tile, %s's position additionally changed by %d. This would put them past the paywall. Thus, they go back to the start. Should've payed up earlier", player.getName(),  roll);
                }
                if (row == boardRows - 1 && col == boardCols - 1) {
                    player.setDone();
                }
                player.setCurrentRow(row);
                player.setCurrentCol(col);
                return String.format("%s rolled %d. By landing on a attribute tile, %s's position additionally changed by %d", player.getName(),  roll, player.getName(), newroll);
            case SWAP_RANDOM:
                Player randomPlayer = players.get(getRandomPlayer(curPlayer, players.size()));
                player.setCurrentRow(randomPlayer.getCurrentRow());
                player.setCurrentCol(randomPlayer.getCurrentCol());
                randomPlayer.setCurrentRow(temp_row);
                randomPlayer.setCurrentCol(temp_col);
                return String.format("%s rolled %d. By landing on a attribute card, %s's swapped positions with %s", player.getName(), roll, player.getName(), randomPlayer.getName());
            default: // Change money
                if (player.getMoney() + tiles[temp_row][temp_col].money < 0) {
                    player.setCurrentCol(0);
                    player.setCurrentRow(0);
                    return String.format("%s rolled %d and %s's money would have changed by %d, causing them to go bankrupt, so they start at the beginning instead", player.getName(), roll, player.getName(), tiles[temp_row][temp_col].money);
                }
                player.setMoney(player.getMoney() + tiles[temp_row][temp_col].money);
                return String.format("%s rolled %d and %s's money changed by %d", player.getName(), roll, player.getName(), tiles[temp_row][temp_col].money);
        }
    }

    public static boolean isGameOver(ArrayList<Player> players) {
        return players.stream().map(Player::isDone).reduce(false, Boolean::logicalOr);
    }

    public static int compare(Player o1, Player o2) {
        if (o1.getCurrentRow() > o2.getCurrentRow()) {
            return -1;
        } else if (o1.getCurrentRow() == o2.getCurrentRow()) {
            if (o1.getCurrentCol() > o2.getCurrentCol()) {
                return -1;
            } else if (o1.getCurrentCol() == o2.getCurrentCol()) {
                return 0;
            } else {
                return 1;
            }
        } else {
            return 1;
        }
    }

    public static String getGameOverString(ArrayList<Player> players) {
        String gameOver = "";
        for (Player p : players) {
            if (p.isDone()) {
                gameOver += String.format("%s won the game by reaching the finish line first!", p.getName());
            }
        }
        players.sort((Player o1, Player o2) -> compare(o1, o2));
        int idx = 1;
        for (Player p : players) {
            gameOver += String.format("\nplayer %s placed %d", p.getName(), idx++);
        }
        return gameOver;
    }
}
