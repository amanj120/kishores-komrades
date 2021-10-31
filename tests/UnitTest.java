import models.GameLogic;
import models.Player;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Random;

import static org.junit.Assert.*;

public class UnitTest {

    String name;
    int money;
    int currentRow;
    int currentCol;

    public UnitTest() {
        this.name = "Broke boi Aman";
        this.money = 0;
        this.currentRow = 4;
        this.currentCol = 0;
    }

    @Test
    public void testPlayerConstructor() {
        Player player = new Player(name, money, currentRow, currentCol);
        assertEquals(player.getName(), name);
        assertEquals(player.getMoney(), money);
        assertEquals(player.getCurrentRow(), currentRow);
        assertEquals(player.getCurrentCol(), currentCol);
    }

    @Test
    public void testPlayerChangeRow() {
        Player player = new Player(name, money, currentRow, currentCol);
        player.setCurrentRow(player.getCurrentRow() - 3);
        assertEquals(player.getCurrentRow(), currentRow - 3);
    }

    @Test
    public void testPlayerChangeCol() {
        Player player = new Player(name, money, currentRow, currentCol);
        player.setCurrentCol(player.getCurrentCol() + 1);
        assertEquals(player.getCurrentCol(), currentCol + 1);
    }

    @Test
    public void testFinishTileIsNotChance() {
        GameLogic.Tile[][] t = GameLogic.setupTiles(10, 10, 100, new Random());
        assertEquals(t[9][9].chance, GameLogic.ChanceCard.NONE);
    }

    @Test
    public void testFinishTileIsNotRed() {
        GameLogic.Tile[][] t = GameLogic.setupTiles(10, 10, 100, new Random());
        assertEquals(t[9][9].isRedTile, false);
    }

    @Test
    public void testBoardHasAtLeast4Chance() {
        GameLogic.Tile[][] t = GameLogic.setupTiles(5, 10, 100, new Random());
        int num_chance = 0;
        for (GameLogic.Tile row[] : t){
            for (GameLogic.Tile tile : row) {
                if (tile.chance != GameLogic.ChanceCard.NONE) {
                    num_chance += 1;
                }
            }
        }
        assertEquals((num_chance >= 4), true);
    }

    @Test
    public void testAllRedTilesNegative() {
        GameLogic.Tile[][] t = GameLogic.setupTiles(5, 10, 100, new Random());
        for (GameLogic.Tile row[] : t){
            for (GameLogic.Tile tile : row) {
                if (tile.isRedTile) {
                    assertTrue(tile.money < 0);
                }
            }
        }
    }

    @Test
    public void testGetPlayersAt() {
        Player p1 = new Player("p1", 0, 0, 0);
        Player p2 = new Player("p1", 0, 2, 3);
        Player p3 = new Player("p1", 0, 0, 3);
        Player p4 = new Player("p1", 0, 0, 0);

        ArrayList<Player> players = new ArrayList<>();
        players.add(p1);
        players.add(p2);
        players.add(p3);
        players.add(p4);

        ArrayList<Player> at00 = GameLogic.getPlayersAt(players, 0, 0);

        assertEquals(at00.size(), 2);
        assertTrue(at00.contains(p1));
        assertTrue(at00.contains(p4));
    }

    @Test
    public void testGameOverFalse() {
        Player p1 = new Player("p1", 0, 0, 0);
        Player p2 = new Player("p1", 0, 2, 3);
        Player p3 = new Player("p1", 0, 0, 3);
        Player p4 = new Player("p1", 0, 0, 0);

        ArrayList<Player> players = new ArrayList<>();
        players.add(p1);
        players.add(p2);
        players.add(p3);
        players.add(p4);

        assertFalse(GameLogic.isGameOver(players));
    }

    @Test
    public void testGameOverTrue() {
        Player p1 = new Player("p1", 0, 0, 0);
        Player p2 = new Player("p1", 0, 2, 3);
        Player p3 = new Player("p1", 0, 0, 3);
        Player p4 = new Player("p1", 0, 0, 0);

        ArrayList<Player> players = new ArrayList<>();
        players.add(p1);
        players.add(p2);
        players.add(p3);
        players.add(p4);

        p2.setDone();

        assertTrue(GameLogic.isGameOver(players));
    }

    @Test
    public void testPlayerIsCloseToPaywall() {
        int num_rows = 10;
        int num_columns = 10;
        GameLogic.Tile[][] t = GameLogic.setupTiles(num_rows, num_columns, 100, new Random());

        Player p1 = new Player("p1", 0, 5, 0);

        // Player should be close up to num_columns divided by 2, 0...5
        for (int i = 0; i < num_columns / 2; i++) {
            int current_col = p1.getCurrentCol();
            p1.setCurrentCol(currentCol + 1);
            assertTrue(GameLogic.closeEnoughToPayPaywall(p1, t));
        }
    }

    @Test
    public void testPlayerIsFarFromPaywall() {
        int num_rows = 10;
        int num_columns = 10;
        GameLogic.Tile[][] t = GameLogic.setupTiles(num_rows, num_columns, 100, new Random());

        Player p1 = new Player("p1", 0, 0, 0);

        // As long as we do not go to num_rows / 2 - 1, we should not be close
        int limit = num_rows / 2 - 1;

        for (int i = 0; i < limit; i++) {
            for (int j = 0; j < num_columns; j++) {
                int current_row = p1.getCurrentRow();
                p1.setCurrentCol(current_row + 1);

                int current_col = p1.getCurrentCol();
                p1.setCurrentCol(current_col + 1);

                assertFalse(GameLogic.closeEnoughToPayPaywall(p1, t));
            }
        }
    }

    @Test
    public void testPassPaywall() {
        int num_rows = 10;
        int num_columns = 10;
        GameLogic.Tile[][] t = GameLogic.setupTiles(num_rows, num_columns, 100, new Random());

        Player p1 = new Player("p1", 0, 0, 0);

        int paywall_x = num_rows / 2;
        int paywall_y = num_columns / 2;

        for (int i = 0; i < num_rows / 2; i++) {
            for (int j = 0; j < num_columns / 2; j++) {
                int current_row = p1.getCurrentRow();
                p1.setCurrentCol(current_row + 1);

                int current_col = p1.getCurrentCol();
                p1.setCurrentCol(current_col + 1);

                assertFalse(GameLogic.passedPaywall(i, j, t));
            }
        }

        for (int i = num_rows / 2; i < num_rows; i++) {
            for (int j = num_columns / 2; j < num_columns; j++) {
                int current_row = p1.getCurrentRow();
                p1.setCurrentCol(current_row + 1);

                int current_col = p1.getCurrentCol();
                p1.setCurrentCol(current_col + 1);

                assertTrue(GameLogic.passedPaywall(i, j, t));
            }
        }
    }

    @Test
    public void testPaywallDeductsMoney() {
        int num_rows = 10;
        int num_columns = 10;
        GameLogic.Tile[][] t = GameLogic.setupTiles(num_rows, num_columns, 100, new Random());

        // Setup player right behind paywall
        int player_x = num_rows / 2;
        int player_y = num_columns / 2 - 1;

        int paywall_fee = GameLogic.getPaywallCost();

        Player p1 = new Player("p1", paywall_fee, player_x, player_y);

        GameLogic.payPaywall(p1, t);

        assertTrue(p1.getMoney() == 0);
    }

    @Test
    public void testFailPaywall() {
        int num_rows = 10;
        int num_columns = 10;
        GameLogic.Tile[][] t = GameLogic.setupTiles(num_rows, num_columns, 100, new Random());

        // Setup player right behind paywall
        int player_x = num_rows / 2;
        int player_y = num_columns / 2 - 1;

        // Setup player money less than paywall cost
        int starting_money = 0;
        int paywall_fee = GameLogic.getPaywallCost();
        assertTrue(starting_money < paywall_fee);

        // Create Player 1 and simulate dice roll
        Player p1 = new Player("p1", starting_money, player_x, player_y);
        ArrayList<Player> players = new ArrayList<>();
        players.add(p1);
        int current_player = 0; // There's only one player

        int simulated_dice_roll = 1;
        assertTrue(simulated_dice_roll > 0 && simulated_dice_roll < 6); // Ensure roll is legal

        // Simulate moving past paywall without paying
        GameLogic.movePlayer(players, current_player, simulated_dice_roll, t);

        assertTrue(p1.getCurrentCol() == 0);
        assertTrue(p1.getCurrentRow() == 0);
        assertEquals(p1.getMoney(), starting_money);
    }

    @Test
    public void testPaywallFreeAfterPay() {
        int num_rows = 10;
        int num_columns = 10;
        GameLogic.Tile[][] t = GameLogic.setupTiles(num_rows, num_columns, 100, new Random());

        // Create two players right behind the paywall
        int player_x = num_rows / 2;
        int player_y = num_columns / 2 - 1;
        int paywall_fee = GameLogic.getPaywallCost(); // Setup player money equal to paywall cost

        Player p1 = new Player("p1", paywall_fee, player_x, player_y);
        Player p2 = new Player("p2", paywall_fee, player_x, player_y);

        ArrayList<Player> players = new ArrayList<>();
        players.add(p1);
        players.add(p2);

        // Player 1 pays the paywall
        GameLogic.payPaywall(p1, t);

        // Player 2 attempts to pass through now paid paywall
        int simulated_dice_roll = 2;
        assertTrue(simulated_dice_roll > 0 && simulated_dice_roll < 6); // Ensure roll is legal
        GameLogic.movePlayer(players, 1, simulated_dice_roll, t);

        assertNotEquals(p2.getCurrentRow(), 0);
        assertNotEquals(p2.getCurrentCol(), 0);
        assertEquals(p2.getMoney(), paywall_fee);
    }
}
