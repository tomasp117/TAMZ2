package com.tamz.soko2024;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "scores")
public class ScoreEntity {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private int levelIndex; // Index levelu
    private int moves; // Počet tahů
    private long time; // Čas ve formátu milisekund

    // Konstruktor
    public ScoreEntity(int levelIndex, int moves, long time) {
        this.levelIndex = levelIndex;
        this.moves = moves;
        this.time = time;
    }

    // Gettery a settery
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLevelIndex() {
        return levelIndex;
    }

    public void setLevelIndex(int levelIndex) {
        this.levelIndex = levelIndex;
    }

    public int getMoves() {
        return moves;
    }

    public void setMoves(int moves) {
        this.moves = moves;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
