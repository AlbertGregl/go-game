package hr.gregl.gogame.game.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class GameMove {
    private int player;
    private int row;
    private int column;
    private LocalDateTime timestamp;

    public GameMove() {
        this.timestamp = LocalDateTime.now();
    }
    public GameMove(int player, int row, int column) {
        this.player = player;
        this.row = row;
        this.column = column;
        this.timestamp = LocalDateTime.now();
    }
    public int getPlayer() {
        return player;
    }
    public int getRow() {
        return row;
    }
    public int getColumn() {
        return column;
    }
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    public void setPlayer(int player) {
        this.player = player;
    }
    public void setRow(int row) {
        this.row = row;
    }
    public void setTimestamp(String timestampString) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
            this.timestamp = LocalDateTime.parse(timestampString, formatter);
        } catch (DateTimeParseException e) {
            this.timestamp = LocalDateTime.now();
        }
    }
    public void setColumn(int column) {
        this.column = column;
    }
    @Override
    public String toString() {
        return "GameMove{" +
                "player=" + player +
                ", row=" + row +
                ", column=" + column +
                ", timestamp=" + timestamp +
                '}';
    }

}
