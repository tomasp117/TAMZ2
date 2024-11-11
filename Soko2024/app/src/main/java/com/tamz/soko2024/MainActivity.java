package com.tamz.soko2024;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
public class MainActivity extends AppCompatActivity {
    private SokoView sokoView;
    private ArrayList<String> levels;
    private int currentLevelIndex = 0;
    private AppDatabase db;  // Instance databáze
    private ScoreDao scoreDao;  // DAO pro přístup k databázi
    private TextView moveCountTextView;
    private TextView timeTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sokoView = findViewById(R.id.sokoView); // Assuming you have a SokoView in your layout
        moveCountTextView = findViewById(R.id.moveCountText);
        timeTextView = findViewById(R.id.timeText);
        // Inicializace Room databáze
        db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "soko_database").build();
        scoreDao = db.scoreDao();

        // Load levels from the assets folder
        levels = LevelLoader.loadLevels(this);

        if (levels.isEmpty()) {
            Toast.makeText(this, "No levels found", Toast.LENGTH_SHORT).show();
            return;
        }

        // Parse and load the first level
        loadLevel(currentLevelIndex);

        Button selectLevelButton = findViewById(R.id.selectLevelButton);
        selectLevelButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LevelSelectMenu.class);

            // Pass current level index and the list of levels
            intent.putExtra("currentLevelIndex", currentLevelIndex);
            intent.putStringArrayListExtra("levels", levels);

            startActivityForResult(intent, 1); // Using startActivityForResult to get the selected level back
        });

        Button nextLevelButton = findViewById(R.id.nextLevelButton);
        nextLevelButton.setOnClickListener(v -> {
            this.nextLevel();
        });

        Button resetButton = findViewById(R.id.resetLevelButton);
        resetButton.setOnClickListener(v -> {
            updateTime(0);
            updateMoveCount(0);
            sokoView.resetLevel();
        });

        // Check and display best score for the current level
        checkAndDisplayBestScore();
        startTimer();
    }

    // Metoda pro aktualizaci počtu tahů
    public void updateMoveCount(int moveCount) {
        moveCountTextView.setText("Moves: " + moveCount);
    }

    // Metoda pro aktualizaci času
    public void updateTime(long elapsedTime) {
        timeTextView.setText("Time: " + elapsedTime / 1000 + "s");
    }

    // Timer pro pravidelnou aktualizaci času
    private void startTimer() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (sokoView.isTimerStarted) {
                    long elapsedTime = System.currentTimeMillis() - sokoView.getStartTime();
                    updateTime(elapsedTime);
                }
                handler.postDelayed(this, 1000); // Aktualizace každou sekundu
            }
        }, 1000);
    }


    public void saveScoreIfBetter(int levelIndex, int moves, long time) {
        new Thread(() -> {
            ScoreEntity bestScore = scoreDao.getBestScoreForLevel(levelIndex);

            if (bestScore == null || moves < bestScore.getMoves() ||
                    (moves == bestScore.getMoves() && time < bestScore.getTime())) {
                // Pokud je nové skóre lepší, ulož jej
                ScoreEntity scoreEntity = new ScoreEntity(levelIndex, moves, time);
                scoreDao.insertScore(scoreEntity);
            }
        }).start();
    }

    private void checkAndDisplayBestScore() {
        new Thread(() -> {
            ScoreEntity bestScore = scoreDao.getBestScoreForLevel(currentLevelIndex);
            runOnUiThread(() -> {
                if (bestScore != null) {
                    String message = "Best Score for Level " + currentLevelIndex + ": Moves = " +
                            bestScore.getMoves() + ", Time = " + bestScore.getTime() + " ms";
                    Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "No best score found for this level.", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    public void nextLevel() {
        currentLevelIndex++;
        if (currentLevelIndex >= levels.size()) {
            currentLevelIndex = 0; // Restart from the first level if it's the last level
        }
        updateTime(0);
        updateMoveCount(0);
        loadLevel(currentLevelIndex);  // Load the next level
        checkAndDisplayBestScore();  // Display best score after changing level
    }

    private void loadLevel(int levelIndex) {
        String levelText = levels.get(levelIndex);
        String[] rows = levelText.split("\n");
        int height = rows.length;
        int width = 0;

        for (String row : rows) {
            if (row.length() > width) {
                width = row.length();
            }
        }

        int[] levelData = LevelParser.parseLevel(levelText, width, height);
        sokoView.setLevelData(levelData, width, height);
        sokoView.setCurrentLevelIndex(levelIndex);
    }

    // Receiving the selected level from LevelSelectMenu
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            // Get the selected level from the intent
            String selectedLevel = data.getStringExtra("selectedLevel");
            if (selectedLevel != null) {
                // Find the index of the selected level in the levels list
                currentLevelIndex = levels.indexOf(selectedLevel);
                loadLevel(currentLevelIndex); // Load the selected level
                checkAndDisplayBestScore();  // Display best score after loading the selected level
            }
        }
    }

    // Method to save score to the database
    public void saveScore(int levelIndex, int moves, long time) {
        new Thread(() -> {
            ScoreEntity scoreEntity = new ScoreEntity(levelIndex, moves, time);
            scoreDao.insertScore(scoreEntity);
        }).start();
    }
}