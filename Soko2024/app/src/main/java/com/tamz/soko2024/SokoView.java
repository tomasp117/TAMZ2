package com.tamz.soko2024;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by kru13
 */
public class SokoView extends View{

    Bitmap[] bmp;

    int lW = 10;
    int lH = 10;

    int width;
    int height;


    private int level[] = {
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
    };

    private int[] currentLevel;

    float startX, startY;

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

        currentLevel = level.clone();
    }

    void resetLevel() {
        currentLevel = level.clone();
        invalidate();
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        width = w / lW;
        height = h / lH;
        super.onSizeChanged(w, h, oldw, oldh);
    }

    //@SuppressLint("DrawAllocation")
    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {

        for (int y = 0; y < lH; y++) {
            for (int x = 0; x < lW; x++) {
                int tileType = currentLevel[y * lW + x];
                canvas.drawBitmap(bmp[tileType], null,
                        new Rect(x*width,
                                y*height,
                                (x+1)*width,
                                (y+1)*height), null);
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
        int heroIndex = -1;
        for (int i = 0; i < currentLevel.length; i++) {
            if (currentLevel[i] == 4) {
                heroIndex = i;
                break;
            }
        }

        if (heroIndex == -1) return;

        int heroX = heroIndex % lW;
        int heroY = heroIndex / lW;

        int newX = heroX + dx;
        int newY = heroY + dy;

        if (newX >= 0 && newX < lW && newY >= 0 && newY < lH && currentLevel[newY * lW + newX] != 1) {
            int newHeroIndex = newY * lW + newX;

            if (currentLevel[newHeroIndex] == 2 || currentLevel[newHeroIndex] == 5) {
                int boxNewX = newX + dx;
                int boxNewY = newY + dy;
                int boxNewIndex = boxNewY * lW + boxNewX;

                if (boxNewX >= 0 && boxNewX < lW && boxNewY >= 0 && boxNewY < lH) {
                    if (currentLevel[boxNewIndex] == 0) {
                        currentLevel[boxNewIndex] = 2;
                        currentLevel[newHeroIndex] = 4;
                        currentLevel[heroIndex] = (level[heroIndex] == 3) ? 3 : 0;
                    } else if (currentLevel[boxNewIndex] == 3) {
                        currentLevel[boxNewIndex] = 5;
                        currentLevel[newHeroIndex] = 4;
                        currentLevel[heroIndex] = (level[heroIndex] == 3) ? 3 : 0;
                    }
                }
            } else {
                currentLevel[newHeroIndex] = 4;
                currentLevel[heroIndex] = (level[heroIndex] == 3) ? 3 : 0;
            }
            invalidate();
        }
    }
}
