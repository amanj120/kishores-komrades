package models;

public class Player {

    private String name;
    private int money;
    private int currentRow;
    private int currentCol;
    private boolean isDone;
    private int score;

    public Player(String name, int money, int currentRow, int currentCol) {
        this.name = name;
        this.money = money;
        this.currentRow = currentRow;
        this.currentCol = currentCol;
        this.isDone = false;
    }

    public String getName() {
        return this.name;
    }

    public int getMoney() {
        return this.money;
    }

    public int getCurrentRow() {
        return this.currentRow;
    }

    public int getCurrentCol() {
        return this.currentCol;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public void setCurrentRow(int currentRow) {
        this.currentRow = currentRow;
    }

    public void setCurrentCol(int currentCol) {
        this.currentCol = currentCol;
    }

    public void setDone() {
        this.isDone = true;
    }

    public boolean isDone() {
        return this.isDone;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getScore() {
        return this.score;
    }
}