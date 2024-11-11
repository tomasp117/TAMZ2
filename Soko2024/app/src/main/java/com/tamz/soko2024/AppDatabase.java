package com.tamz.soko2024;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {ScoreEntity.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract ScoreDao scoreDao(); // Metoda pro získání DAO
}