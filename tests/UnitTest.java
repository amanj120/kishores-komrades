import models.GameLogic;
import models.Player;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
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
        assertEquals(t[9][9].attribute, GameLogic.Attribute.NONE);
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
                if (tile.attribute != GameLogic.Attribute.NONE) {
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

        assertEquals(0, p1.getMoney());
    }

    @Test
    public void testFailPaywall() {
        int num_rows = 10;
        int num_columns = 10;
        GameLogic.Tile[][] t = GameLogic.setupTiles(num_rows, num_columns, 100, new Random());

        // Setup player right at paywall
        int player_x = num_rows / 2;
        int player_y = num_columns / 2;

        // Setup player money to beless than paywall cost
        int starting_money = 0;
        int paywall_fee = GameLogic.getPaywallCost();
        assertTrue(starting_money < paywall_fee);

        // Create Player 1 and simulate dice roll
        Player p1 = new Player("p1", 0, 0, 0);
        Player p2 = new Player("p1", 0, 2, 3);
        Player p3 = new Player("p1", 0, 0, 3);
        Player p4 = new Player("p1", 0, 0, 0);

        ArrayList<Player> players = new ArrayList<>();
        players.add(p1);
        players.add(p2);
        players.add(p3);
        players.add(p4);

        int current_player = 0;

        // Simulate all possible dice rolls that pass the barrier
        // i: Tracks the player position
        // j: Tracks the all dice values, note: we only choose values that will send us over paywall
        for (int i = 1; i < num_columns / 2; i++) {
            for (int j = i; j < 7; j++) {
                // Reset player position to right at paywall
                p1.setCurrentRow(player_x);
                p1.setCurrentCol(player_y);

                p1.setCurrentCol(player_y - i); // Update the player position
                // Are we now behind the paywall?
                assertFalse(GameLogic.passedPaywall(p1.getCurrentRow(), p1.getCurrentCol(), t));

                // Simulate moving past paywall without paying
                int simulated_dice_roll = j;
                GameLogic.movePlayer(players, current_player, simulated_dice_roll, t);

                // Assert player position is back to "square one"
                assertEquals(0, p1.getCurrentRow());
                assertEquals(0, p1.getCurrentCol());
            }
        }
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
        assertEquals(0, p1.getMoney());

        // Player 2 attempts to pass through now paid paywall
        int p2_starting_money = p2.getMoney();

        int simulated_dice_roll = 1;
        GameLogic.movePlayer(players, 1, simulated_dice_roll, t);

        // Player 2 should not have been moved back
        assertNotEquals(0, p2.getCurrentRow());
        assertNotEquals(0, p2.getCurrentCol());
        // Player 2 should not have paid the fee
        assertEquals(p2_starting_money, p2.getMoney());
    }

    @Test
    public void testTwoMiniGameTiles() {
        GameLogic.Tile[][] tiles = GameLogic.setupTiles(5, 11,100, new Random());
        int numMiniGameTiles = 0;
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[0].length; j++) {
                if (tiles[i][j].attribute == GameLogic.Attribute.MINIGAME) {
                    numMiniGameTiles++;
                }
            }
        }
        assert numMiniGameTiles == 2;
    }

    @Test
    public void testMiniGameTilesHaveNoMoney() {
        GameLogic.Tile[][] tiles = GameLogic.setupTiles(5, 11,100, new Random());
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[0].length; j++) {
                if (tiles[i][j].attribute == GameLogic.Attribute.MINIGAME) {
                    assert tiles[i][j].money == 0;
                }
            }
        }
    }

    @Test
    public void testFirstMiniGameTileLocation() {
        GameLogic.Tile[][] tiles = GameLogic.setupTiles(5, 11,100, new Random());
        assert tiles[1][2].attribute == GameLogic.Attribute.MINIGAME;
    }

    @Test
    public void testSecondMiniGameTileLocation() {
        GameLogic.Tile[][] tiles = GameLogic.setupTiles(5, 11,100, new Random());
        assert tiles[3][8].attribute == GameLogic.Attribute.MINIGAME;
    }

    @Test
    public void testMiniGameTilesHaveNoMove() {
        GameLogic.Tile[][] tiles = GameLogic.setupTiles(5, 11,100, new Random());
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[0].length; j++) {
                if (tiles[i][j].attribute == GameLogic.Attribute.MINIGAME) {
                    assert tiles[i][j].move == 0;
                }
            }
        }
    }

    @Test
    public void testRandomChanceTileMethodDoesntReturnMiniGameTile() {
        GameLogic.Tile chanceTile = GameLogic.getRandomChanceTile(1,2,25, new Random());
        assert chanceTile.attribute != GameLogic.Attribute.MINIGAME;
    }

    @Test
    public void testMoneyWonAwardWorks() {
        Player a = new Player("a", 100, 0, 0);
        Player b = new Player("b", 0, 0, 0);
        Player c = new Player("c", 0, 0, 0);
        ArrayList<Player> players = new ArrayList<>();
        players.add(a);
        players.add(b);
        players.add(c);

        String s = GameLogic.getAwardsString(players);
        assert s.contains("Most Money Award: a : 100 money earned");
    }

    @Test
    public void testMiniGamesWonAwardWorks() {
        Player a = new Player("a", 0, 0, 0);
        Player b = new Player("b", 0, 0, 0);
        Player c = new Player("c", 0, 0, 0);
        a.minigamesWon = 10;
        b.minigamesWon = 0;
        c.minigamesWon = 0;
        ArrayList<Player> players = new ArrayList<>();
        players.add(a);
        players.add(b);
        players.add(c);

        String s = GameLogic.getAwardsString(players);
        assert s.contains("Most Mini Games Won Award: a : 10 mini games won");
    }

    @Test
    public void testTilesMovedAwardWorks() {
        Player a = new Player("a", 0, 0, 0);
        Player b = new Player("b", 0, 0, 0);
        Player c = new Player("c", 0, 0, 0);
        a.tilesMoved = 10;
        b.tilesMoved = 0;
        c.tilesMoved = 0;
        ArrayList<Player> players = new ArrayList<>();
        players.add(a);
        players.add(b);
        players.add(c);

        String s = GameLogic.getAwardsString(players);
        assert s.contains("Most Tiles Moved Award: a : 10 tiles moved ");
    }

    @Test
    public void testGameOverStringOnePlayer() {
        Player a = new Player("a", 0, 0, 0);
        a.setDone();
        ArrayList<Player> players = new ArrayList<>();
        players.add(a);

        String s = GameLogic.getGameOverString(players);
        assert s.contains("a won the game by reaching the finish line first!");
    }

    @Test
    public void testGameOverStringMultiplePlayers() {
        Player a = new Player("a", 0, 0, 0);
        Player b = new Player("b", 0, 0, 0);
        Player c = new Player("c", 0, 0, 0);
        b.setDone();
        ArrayList<Player> players = new ArrayList<>();
        players.add(a);
        players.add(b);
        players.add(c);

        String s = GameLogic.getGameOverString(players);
        assert s.contains("b won the game by reaching the finish line first!");
    }

    @Test
    public void testComparePlayers() {
        Player a = new Player("a", 0, 2, 0);
        Player b = new Player("b", 0, 0, 0);
        assert GameLogic.compare(a,b) < 0;
    }
}
