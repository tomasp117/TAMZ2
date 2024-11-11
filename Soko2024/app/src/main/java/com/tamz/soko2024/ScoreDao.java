package com.tamz.soko2024;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ScoreDao {
    // Uložit nové skóre
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertScore(ScoreEntity score);

    @Query("SELECT * FROM scores WHERE levelIndex = :levelIndex ORDER BY moves ASC, time ASC LIMIT 1")
    ScoreEntity getBestScoreForLevel(int levelIndex);
    // Získat všechny skóre

}
