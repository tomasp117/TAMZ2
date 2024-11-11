package com.tamz.soko2024;

public class LevelParser {
    public static int[] parseLevel(String levelText, int width, int height) {
        int[] parsedLevel = new int[width * height];
        String[] rows = levelText.split("\n");

        for (int y = 0; y < height; y++) {
            if (y >= rows.length) break;
            String row = rows[y];
            for (int x = 0; x < width; x++) {
                if (x >= row.length()) break;

                char tile = row.charAt(x);
                switch (tile) {
                    case '#': // Wall
                        parsedLevel[y * width + x] = 1;
                        break;
                    case '.': // Goal
                        parsedLevel[y * width + x] = 3;
                        break;
                    case '$': // Box
                        parsedLevel[y * width + x] = 2;
                        break;
                    case '@': // Hero
                        parsedLevel[y * width + x] = 4;
                        break;
                    case '*': // Box on goal
                        parsedLevel[y * width + x] = 5;
                        break;
                    case ' ':
                    default:
                        parsedLevel[y * width + x] = 0; // Empty space
                        break;
                }
            }
        }

        return parsedLevel;
    }
}
