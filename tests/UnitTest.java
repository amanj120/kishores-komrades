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

}
