package com.tamz.soko2024;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class LevelLoader {
    public static ArrayList<String> loadLevels(Context context) {
        ArrayList<String> levels = new ArrayList<>();
        try {
            AssetManager assetManager = context.getAssets();
            InputStreamReader inputStreamReader = new InputStreamReader(assetManager.open("levels.txt"));
            BufferedReader reader = new BufferedReader(inputStreamReader);

            String line;
            StringBuilder level = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    if (level.length() > 0) {
                        levels.add(level.toString());
                        level.setLength(0);  // Clear the StringBuilder for the next level
                    }
                } else {
                    level.append(line).append("\n");
                }
            }

            // Add the last level if there is no trailing empty line
            if (level.length() > 0) {
                levels.add(level.toString());
            }

            reader.close();
        } catch (IOException e) {
            Log.e("LevelLoader", "Error loading levels", e);
        }

        return levels;
    }
}
