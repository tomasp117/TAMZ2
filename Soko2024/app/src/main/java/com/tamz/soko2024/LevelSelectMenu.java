package com.tamz.soko2024;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
public class LevelSelectMenu extends AppCompatActivity {
    private RecyclerView recyclerView;
    private LevelPreviewAdapter adapter;
    private ArrayList<String> levels;
    private int currentLevelIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_select);

        levels = getIntent().getStringArrayListExtra("levels");
        currentLevelIndex = getIntent().getIntExtra("currentLevelIndex", 0);

        if (levels == null || levels.isEmpty()) {
            Toast.makeText(this, "No levels available", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Generate level previews and level names
        List<Bitmap> levelPreviews = new ArrayList<>();
        List<String> levelNames = new ArrayList<>();
        for (String levelText : levels) {
            String[] rows = levelText.split("\n");
            int height = rows.length;
            int width = 0;

            for (String row : rows) {
                if (row.length() > width) {
                    width = row.length();
                }
            }

            int[] levelData = LevelParser.parseLevel(levelText, width, height);
            SokoView sokoView = new SokoView(this);
            sokoView.setLevelData(levelData, width, height);

            Bitmap preview = sokoView.generateLevelPreview();
            levelPreviews.add(preview);

            // Generate a level name (you can modify this to be more descriptive)
            levelNames.add("Level " + (levelPreviews.size())); // e.g., Level 1, Level 2, etc.
        }

        // Set up the adapter with both previews and names
        adapter = new LevelPreviewAdapter(levelPreviews, levelNames, position -> {
            // When a level is selected, pass the selected level back to MainActivity
            String selectedLevel = levels.get(position);
            Intent resultIntent = new Intent();
            resultIntent.putExtra("selectedLevel", selectedLevel);
            setResult(RESULT_OK, resultIntent);
            finish(); // Close the LevelSelectMenu
        });

        recyclerView.setAdapter(adapter);
    }
}