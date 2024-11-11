package com.tamz.soko2024;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

/**
 * Created by kru13
 */
public class SokoView extends View{

    Bitmap[] bmp;

    int lW;
    int lH;

    int width;
    int height;


    private int moveCount = 0;
    private long startTime = 0;
    public boolean isTimerStarted = false;

    /*private int level[] = {
            1,1,1,1,1,1,1,1,1,0,
            1,0,0,0,0,0,0,0,1,0,
            1,0,2,3,3,2,1,0,1,0,
            1,0,1,3,2,3,2,0,1,0,
            1,0,2,3,3,2,4,0,1,0,
            1,0,1,3,2,3,2,0,1,0,
            1,0,2,3,3,2,1,0,1,0,
            1,0,0,0,0,0,0,0,1,0,
            1,1,1,1,1,1,1,1,1,0,
            0,0,0,0,0,0,0,0,0,0
    };*/

    private int[] currentLevel;
    private int[] originalLevel;

    float startX, startY;

    public void setLevelData(int[] levelData, int width, int height) {
        this.currentLevel = levelData;
        this.originalLevel = levelData.clone();
        this.lW = width;
        this.lH = height;
        updateTileSize();
        invalidate();
    }

    private void updateTileSize() {
        if (lW > 0 && lH > 0) { // Ověří, že máme platné rozměry levelu
            width = Math.min(getWidth() / lW, getHeight() / lH); // Nastaví velikost dlaždic podle rozměrů levelu a dostupného prostoru
            height = width; // Zajišťuje, že dlaždice budou čtvercové
        }
    }

    public SokoView(Context context) {
        super(context);
        init(context);
    }

    public SokoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SokoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    void init(Context context) {
        bmp = new Bitmap[6];

        bmp[0] = BitmapFactory.decodeResource(getResources(), R.drawable.empty);
        bmp[1] = BitmapFactory.decodeResource(getResources(), R.drawable.wall);
        bmp[2] = BitmapFactory.decodeResource(getResources(), R.drawable.box);
        bmp[3] = BitmapFactory.decodeResource(getResources(), R.drawable.goal);
        bmp[4] = BitmapFactory.decodeResource(getResources(), R.drawable.hero);
        bmp[5] = BitmapFactory.decodeResource(getResources(), R.drawable.boxok);

        moveCount = 0;
        startTime = 0;
        isTimerStarted = false;
    }

    void resetLevel() {
        if (originalLevel != null) {
            currentLevel = originalLevel.clone();  // Reset to the original level
            moveCount = 0;
            startTime = 0;
            isTimerStarted = false;
            invalidate();
        }
    }

    private int currentLevelIndex;

    public void setCurrentLevelIndex(int levelIndex) {
        this.currentLevelIndex = levelIndex;
    }

    public void checkLevelCompletion() {
        for (int i = 0; i < currentLevel.length; i++) {
            if (currentLevel[i] == 2) { // Box not on goal
                return; // Level is not complete yet
            }
        }
        long elapsedTime = System.currentTimeMillis() - startTime;

        // Zavolá MainActivity pro uložení skóre
        ((MainActivity) getContext()).saveScoreIfBetter(currentLevelIndex, moveCount, elapsedTime);

        // Zobrazení vítězné zprávy
        showWinMessage();

        // Resetuje počítadlo pohybů a časovač pro další level
        moveCount = 0;
        startTime = 0;
        isTimerStarted = false;
        invalidate();
    }

    private void showWinMessage() {
        // Display a message to the user
        Toast.makeText(getContext(), "YOU WON!!", Toast.LENGTH_SHORT).show();

        // Optional: reset the level or load the next level after a short delay
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ((MainActivity) getContext()).nextLevel();
            }
        }, 2000);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (lW > 0 && lH > 0) { // Zajistí, že máme platné rozměry levelu
            width = Math.min(w / lW, h / lH); // Nastaví velikost dlaždic podle rozměrů levelu a dostupného prostoru
            height = width; // Zajišťuje, že dlaždice budou čtvercové
        }
        super.onSizeChanged(w, h, oldw, oldh);
    }

    //@SuppressLint("DrawAllocation")
    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        for (int y = 0; y < lH; y++) {
            for (int x = 0; x < lW; x++) {
                int tileType = currentLevel[y * lW + x];
                if (tileType >= 0 && tileType < bmp.length) {
                    canvas.drawBitmap(bmp[tileType], null,
                            new Rect(x * width, y * height, (x + 1) * width, (y + 1) * height), null);
                } else {
                    // Handle the case where tileType is out of bounds
                    Log.e("SokoView", "Invalid tileType: " + tileType);
                }
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = event.getX();
                startY = event.getY();
                System.out.println("DOWNNNNNNNNNNN");
                return true;
            case MotionEvent.ACTION_UP:
                float endX = event.getX();
                float endY = event.getY();
                System.out.println("UPPPP");
                handleSwipe(startX, startY, endX, endY);
                return true;
        }
        return super.onTouchEvent(event);
    }

    private void handleSwipe(float startX, float startY, float endX, float endY) {
        float deltaX = endX - startX;
        float deltaY = endY - startY;

        if (Math.abs(deltaX) > Math.abs(deltaY)) {
            if (deltaX > 0) {
                moveHero(1, 0);
            } else {
                moveHero(-1, 0);
            }
        } else {
            if (deltaY > 0) {
                moveHero(0, 1);
            } else {
                moveHero(0, -1);
            }
        }
    }

    private void moveHero(int dx, int dy) {
        if (!isTimerStarted) {
            startTime = System.currentTimeMillis();
            isTimerStarted = true;
        }
        moveCount++;

        ((MainActivity) getContext()).updateMoveCount(moveCount);


        int heroIndex = -1;
        for (int i = 0; i < currentLevel.length; i++) {
            if (currentLevel[i] == 4) { // Find hero
                heroIndex = i;
                break;
            }
        }

        if (heroIndex == -1) return; // No hero found

        int heroX = heroIndex % lW;
        int heroY = heroIndex / lW;

        int newX = heroX + dx;
        int newY = heroY + dy;

        if (newX >= 0 && newX < lW && newY >= 0 && newY < lH) {
            int newHeroIndex = newY * lW + newX;
            int tileAtNewPos = currentLevel[newHeroIndex];

            if (tileAtNewPos == 1) return; // Wall, can't move

            if (tileAtNewPos == 2 || tileAtNewPos == 5) { // Box or Box on goal
                int boxNewX = newX + dx;
                int boxNewY = newY + dy;
                int boxNewIndex = boxNewY * lW + boxNewX;

                if (boxNewX >= 0 && boxNewX < lW && boxNewY >= 0 && boxNewY < lH) {
                    int boxTileAtNewPos = currentLevel[boxNewIndex];
                    if (boxTileAtNewPos == 0 || boxTileAtNewPos == 3) { // Empty space or Goal
                        currentLevel[boxNewIndex] = (boxTileAtNewPos == 3) ? 5 : 2; // Box or Box on goal
                        currentLevel[newHeroIndex] = 4; // Move hero
                        currentLevel[heroIndex] = (originalLevel[heroIndex] == 3) ? 3 : 0; // Update old hero position
                        checkLevelCompletion();
                    }
                }
            } else { // Move hero to empty space or goal
                currentLevel[newHeroIndex] = 4;
                currentLevel[heroIndex] = (originalLevel[heroIndex] == 3) ? 3 : 0;
            }
            invalidate();
        }
    }

    public Bitmap generateLevelPreview() {
        int previewSize = 100; // Nastavte požadovanou velikost náhledu
        Bitmap previewBitmap = Bitmap.createBitmap(previewSize, previewSize, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(previewBitmap);

        int tileSize = previewSize / Math.max(lW, lH); // Velikost dlaždice pro náhled

        for (int y = 0; y < lH; y++) {
            for (int x = 0; x < lW; x++) {
                int tileType = currentLevel[y * lW + x];
                if (tileType >= 0 && tileType < bmp.length) {
                    Rect destRect = new Rect(x * tileSize, y * tileSize, (x + 1) * tileSize, (y + 1) * tileSize);
                    canvas.drawBitmap(bmp[tileType], null, destRect, null);
                }
            }
        }

        return previewBitmap;
    }

    public long getStartTime() {
        return this.startTime;
    }
}
