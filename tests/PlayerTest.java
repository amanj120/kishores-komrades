import models.Player;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PlayerTest {

    String name;
    int money;
    int currentRow;
    int currentCol;

    public PlayerTest() {
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

}
